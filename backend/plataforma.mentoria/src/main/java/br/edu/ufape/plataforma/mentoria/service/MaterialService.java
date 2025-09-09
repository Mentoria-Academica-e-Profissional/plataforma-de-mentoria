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
import java.nio.file.StandardCopyOption;
import java.util.*;
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

    public MaterialDTO createMaterial(MaterialDTO materialDTO, MultipartFile arquivo, Long userID) throws IOException {
        logger.debug("Criando material para usuário ID: {}", userID);

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userID));

        Material material = materialMapper.toEntity(materialDTO);
        material.setUserUploader(user);

        processFileUpload(material, arquivo);

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

        handleFileUpdate(existingMaterial, updatedMaterial, arquivo);

        Material materialSalvo = materialRepository.save(updatedMaterial);
        logger.info("Material atualizado com sucesso. ID: {}", id);

        return materialMapper.toDTO(materialSalvo);
    }

    public void deleteById(Long id) {
        logger.debug("Deletando material ID: {}", id);

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Material.class, id));

        deleteFileIfExists(material.getFilePath());
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

    private void processFileUpload(Material material, MultipartFile arquivo) throws IOException {
        if (material.getMaterialType() == MaterialType.LINK) {
            material.setFilePath(null);
            return;
        }

        if (isFileUploadRequired(material.getMaterialType()) && arquivo != null && !arquivo.isEmpty()) {
            String filePath = saveUploadedFile(arquivo);
            material.setFilePath(filePath);
        }
    }

    private void handleFileUpdate(Material existingMaterial, Material updatedMaterial, MultipartFile arquivo) throws IOException {
        if (updatedMaterial.getMaterialType() == MaterialType.LINK) {
            deleteFileIfExists(existingMaterial.getFilePath());
            updatedMaterial.setFilePath(null);
        } else if (isFileUploadRequired(updatedMaterial.getMaterialType()) && arquivo != null && !arquivo.isEmpty()) {
            deleteFileIfExists(existingMaterial.getFilePath());

            String newFilePath = saveUploadedFile(arquivo);
            updatedMaterial.setFilePath(newFilePath);
        } else {
            updatedMaterial.setFilePath(existingMaterial.getFilePath());
        }
    }

    private boolean isFileUploadRequired(MaterialType materialType) {
        return materialType == MaterialType.VIDEO || materialType == MaterialType.DOCUMENTO;
    }

    private String saveUploadedFile(MultipartFile arquivo) throws IOException {
        String nomeArquivo = UUID.randomUUID().toString() + "_" + arquivo.getOriginalFilename();
        Path caminhoCompleto = uploadDir.resolve(nomeArquivo);

        Files.copy(arquivo.getInputStream(), caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);
        logger.debug("Arquivo salvo: {}", caminhoCompleto.toAbsolutePath());

        return caminhoCompleto.toString();
    }

    private void deleteFileIfExists(String filePath) {
        if (filePath != null) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
                logger.debug("Arquivo deletado: {}", filePath);
            } catch (IOException e) {
                logger.warn("Não foi possível excluir o arquivo: {}", filePath, e);
            }
        }
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