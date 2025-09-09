package br.edu.ufape.plataforma.mentoria.service;

import br.edu.ufape.plataforma.mentoria.dto.MaterialDTO;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.enums.MaterialType;
import br.edu.ufape.plataforma.mentoria.enums.UserRole;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.mapper.MaterialMapper;
import br.edu.ufape.plataforma.mentoria.model.Material;
import br.edu.ufape.plataforma.mentoria.model.Mentor;
import br.edu.ufape.plataforma.mentoria.model.Mentored;
import br.edu.ufape.plataforma.mentoria.model.User;
import br.edu.ufape.plataforma.mentoria.repository.MaterialRepository;
import br.edu.ufape.plataforma.mentoria.repository.MentorRepository;
import br.edu.ufape.plataforma.mentoria.repository.MentoredRepository;
import br.edu.ufape.plataforma.mentoria.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaterialService {

    private static final Logger logger = LoggerFactory.getLogger(MaterialService.class);

    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;
    private final MentoredRepository mentoredRepository;
    private final MentorRepository mentorRepository;
    private final MaterialMapper materialMapper;
    private final Path uploadDir;

    // Padrão para caracteres permitidos no nome do arquivo
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");
    
    // Extensões permitidas
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".pdf", ".doc", ".docx", ".txt", ".jpg", ".jpeg", ".png", ".mp4", ".avi", ".mov"
    );

    public MaterialService(MaterialRepository materialRepository,
                           UserRepository userRepository,
                           MaterialMapper materialMapper,
                           MentoredRepository mentoredRepository,
                           MentorRepository mentorRepository,
                           @Value("${app.upload.dir:upload}") String uploadDirPath) {
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
        this.materialMapper = materialMapper;
        this.mentoredRepository = mentoredRepository;
        this.mentorRepository = mentorRepository;
        this.uploadDir = Paths.get(uploadDirPath);

        initializeUploadDirectory();
    }

    private void initializeUploadDirectory() {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                logger.info("Diretório de upload criado: {}", uploadDir.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Erro ao criar diretório de upload: {}", uploadDir.toAbsolutePath(), e);
            throw new RuntimeException("Não foi possível criar o diretório de upload", e);
        }
    }

    /**
     * Sanitiza o nome do arquivo removendo caracteres perigosos e validando a extensão
     */
    private String sanitizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do arquivo não pode estar vazio");
        }
        
        // Remove path separators e caracteres perigosos
        String sanitized = originalFilename.replaceAll("[/\\\\:*?\"<>|]", "_");
        
        // Remove sequências de pontos que podem ser usadas para path traversal
        sanitized = sanitized.replaceAll("\\.{2,}", ".");
        
        // Remove espaços no início e fim
        sanitized = sanitized.trim();
        
        // Verifica se tem extensão válida
        String extension = getFileExtension(sanitized).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Extensão de arquivo não permitida: " + extension);
        }
        
        // Limita o tamanho do nome
        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }
        
        return sanitized;
    }
    
    /**
     * Extrai a extensão do arquivo
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }
    
    /**
     * Cria um nome de arquivo seguro usando UUID + nome sanitizado
     */
    private String createSecureFilename(String originalFilename) {
        String sanitizedName = sanitizeFilename(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "_" + sanitizedName;
    }
    
    /**
     * Valida se o caminho final está dentro do diretório de upload permitido
     */
    private Path validateAndResolvePath(String filename) throws IOException {
        Path resolvedPath = uploadDir.resolve(filename).normalize();
        
        // Verifica se o caminho final ainda está dentro do diretório de upload
        if (!resolvedPath.startsWith(uploadDir.toAbsolutePath().normalize())) {
            throw new SecurityException("Tentativa de path traversal detectada");
        }
        
        return resolvedPath;
    }

    public MaterialDTO createMaterial(MaterialDTO materialDTO, MultipartFile arquivo, Long userID) throws IOException {
        logger.debug("Criando material para usuário ID: {}", userID);

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userID));

        Material material = materialMapper.toEntity(materialDTO);
        
        if (material.getMaterialType() == MaterialType.LINK) {
            material.setFilePath(null);
        }
        else if ((material.getMaterialType() == MaterialType.VIDEO ||
                material.getMaterialType() == MaterialType.DOCUMENTO) &&
                arquivo != null && !arquivo.isEmpty()) {
            
            // Validação de tamanho do arquivo (exemplo: máximo 50MB)
            if (arquivo.getSize() > 50 * 1024 * 1024) {
                throw new IllegalArgumentException("Arquivo muito grande. Máximo permitido: 50MB");
            }
            
            String nomeArquivoSeguro = createSecureFilename(arquivo.getOriginalFilename());
            Path caminhoCompleto = validateAndResolvePath(nomeArquivoSeguro);
            
            Files.copy(arquivo.getInputStream(), caminhoCompleto);
            material.setFilePath(caminhoCompleto.toString());
        }
        
        material.setUserUploader(user);

        Material materialSalvo = materialRepository.save(material);
        logger.info("Material criado com sucesso. ID: {}", materialSalvo.getId());

        return materialMapper.toDTO(materialSalvo);
    }

    @Transactional(readOnly = true)
    public MaterialDTO getMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Material.class, id));
        return materialMapper.toDTO(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialDTO> listAll() {
        return materialRepository.findAll()
                .stream()
                .map(materialMapper::toDTO)
                .collect(Collectors.toList());
    }

    public MaterialDTO updateById(Long id, MaterialDTO materialDTO, MultipartFile arquivo) throws IOException {
        logger.debug("Atualizando material ID: {}", id);

        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Material.class, id));

        Material updatedMaterial = materialMapper.toEntity(materialDTO);
        updatedMaterial.setId(id);
        updatedMaterial.setUserUploader(existingMaterial.getUserUploader());

        if (updatedMaterial.getMaterialType() == MaterialType.LINK) {
            updatedMaterial.setFilePath(null);
        }
        else if ((updatedMaterial.getMaterialType() == MaterialType.VIDEO ||
                updatedMaterial.getMaterialType() == MaterialType.DOCUMENTO) &&
                arquivo != null && !arquivo.isEmpty()) {
            
            // Validação de tamanho do arquivo
            if (arquivo.getSize() > 50 * 1024 * 1024) {
                throw new IllegalArgumentException("Arquivo muito grande. Máximo permitido: 50MB");
            }
            
            // Remove arquivo antigo se existir
            if (existingMaterial.getFilePath() != null) {
                try {
                    Path oldPath = Paths.get(existingMaterial.getFilePath());
                    // Valida se o arquivo antigo está no diretório permitido antes de excluir
                    if (oldPath.startsWith(uploadDir.toAbsolutePath().normalize())) {
                        Files.deleteIfExists(oldPath);
                    }
                } catch (IOException e) {
                    System.err.println("Não foi possível excluir o arquivo antigo: " + e.getMessage());
                }
            }
            
            String nomeArquivoSeguro = createSecureFilename(arquivo.getOriginalFilename());
            Path caminhoCompleto = validateAndResolvePath(nomeArquivoSeguro);
            
            Files.copy(arquivo.getInputStream(), caminhoCompleto);
            updatedMaterial.setFilePath(caminhoCompleto.toString());
        } else {
            updatedMaterial.setFilePath(existingMaterial.getFilePath());
        }
        
        Material materialSalvo = materialRepository.save(updatedMaterial);
        logger.info("Material atualizado com sucesso. ID: {}", id);

        return materialMapper.toDTO(materialSalvo);
    }

    public void deleteById(Long id) {
        logger.debug("Deletando material ID: {}", id);

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Material.class, id));
                
        if (material.getFilePath() != null) {
            try {
                Path filePath = Paths.get(material.getFilePath());
                // Valida se o arquivo está no diretório permitido antes de excluir
                if (filePath.startsWith(uploadDir.toAbsolutePath().normalize())) {
                    Files.deleteIfExists(filePath);
                } else {
                    System.err.println("Tentativa de exclusão de arquivo fora do diretório permitido: " + material.getFilePath());
                }
            } catch (IOException e) {
                System.err.println("Não foi possível excluir o arquivo: " + e.getMessage());
            }
        }
        materialRepository.delete(material);

        logger.info("Material deletado com sucesso. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<MaterialDTO> filterByInterestArea(List<InterestArea> areas) {
        if (areas == null || areas.isEmpty()) {
            return listAll();
        }

        Set<Material> materialsSet = new LinkedHashSet<>();
        for (InterestArea area : areas) {
            materialsSet.addAll(materialRepository.findByInterestAreaContaining(area));
        }

        return materialsSet.stream()
                .map(materialMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaterialDTO> suggestMaterials(Long usuarioId) {
        logger.debug("Buscando sugestões de materiais para usuário ID: {}", usuarioId);

        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, usuarioId));

        Set<InterestArea> areasDeInteresse = getUserInterestAreas(usuario);

        if (areasDeInteresse.isEmpty()) {
            logger.debug("Usuário sem áreas de interesse, retornando materiais recentes");
            return getRecentMaterials();
        }

        return getSuggestedMaterialsByInterestAreas(areasDeInteresse);
    }

    private Set<InterestArea> getUserInterestAreas(User usuario) {
        Set<InterestArea> areasDeInteresse = new HashSet<>();

        if (usuario.getRole() == UserRole.MENTOR) {
            Mentor mentor = mentorRepository.findByUserId(usuario.getId());
            if (mentor != null) {
                areasDeInteresse.addAll(mentor.getInterestArea());
            }
        } else {
            Mentored mentored = mentoredRepository.findByUserId(usuario.getId());
            if (mentored != null) {
                areasDeInteresse.addAll(mentored.getInterestArea());
            }
        }

        return areasDeInteresse;
    }

    private List<MaterialDTO> getRecentMaterials() {
        return materialRepository.findTop10ByOrderByIdDesc()
                .stream()
                .map(materialMapper::toDTO)
                .collect(Collectors.toList());
    }

    private List<MaterialDTO> getSuggestedMaterialsByInterestAreas(Set<InterestArea> areasDeInteresse) {
        Set<Material> materiaisSugeridos = new LinkedHashSet<>();

        for (InterestArea area : areasDeInteresse) {
            materiaisSugeridos.addAll(materialRepository.findByInterestAreaContaining(area));
        }

        return materiaisSugeridos.stream()
                .limit(20)
                .map(materialMapper::toDTO)
                .collect(Collectors.toList());
    }
}