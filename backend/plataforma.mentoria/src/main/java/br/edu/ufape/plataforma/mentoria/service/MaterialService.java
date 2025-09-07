package br.edu.ufape.plataforma.mentoria.service;

import br.edu.ufape.plataforma.mentoria.dto.MaterialDTO;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.enums.MaterialType;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;
    private final MentoredRepository mentoredRepository;
    private final MentorRepository mentorRepository;
    private final MaterialMapper materialMapper;
    private final Path uploadDir = Paths.get("upload");

    public MaterialService(MaterialRepository materialRepository,
                           UserRepository userRepository,
                           MaterialMapper materialMapper,
                           MentoredRepository mentoredRepository,
                           MentorRepository mentorRepository) {
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
        this.materialMapper = materialMapper;
        this.mentoredRepository = mentoredRepository;
        this.mentorRepository = mentorRepository;
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de upload", e);
        }
    }


    public MaterialDTO createMaterial(MaterialDTO materialDTO, MultipartFile arquivo, Long userID) throws IOException {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userID));
        Material material = materialMapper.toEntity(materialDTO);
        if (material.getMaterialType() == MaterialType.LINK) {
            material.setFilePath(null);
        }
        else if ((material.getMaterialType() == MaterialType.VIDEO ||
                material.getMaterialType() == MaterialType.DOCUMENTO) &&
                arquivo != null && !arquivo.isEmpty()) {
            String nomeArquivo = UUID.randomUUID().toString() + "_" + arquivo.getOriginalFilename();
            Path caminhoCompleto = uploadDir.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), caminhoCompleto);
            material.setFilePath(caminhoCompleto.toString());
        }
        material.setUserUploader(user);
        Material materialSalvo = materialRepository.save(material);
        return materialMapper.toDTO(materialSalvo);
    }

    public MaterialDTO getMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Material.class, id));

        return materialMapper.toDTO(material);
    }

    public List<MaterialDTO> listAll() {
        List<Material> materiais = materialRepository.findAll();
        return materiais.stream()
                .map(materialMapper::toDTO)
                .collect(Collectors.toList());
    }

    public MaterialDTO updateById(Long id, MaterialDTO materialDTO, MultipartFile arquivo) throws IOException {
        // Verificar se o material existe
        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Material.class, id));

        // Converter DTO para entidade
        Material updatedMaterial = materialMapper.toEntity(materialDTO);
        updatedMaterial.setId(id);
        updatedMaterial.setUserUploader(existingMaterial.getUserUploader());

        if (updatedMaterial.getMaterialType() == MaterialType.LINK) {
            updatedMaterial.setFilePath(null);
        }
        else if ((updatedMaterial.getMaterialType() == MaterialType.VIDEO ||
                updatedMaterial.getMaterialType() == MaterialType.DOCUMENTO) &&
                arquivo != null && !arquivo.isEmpty()) {
            if (existingMaterial.getFilePath() != null) {
                try {
                    Files.deleteIfExists(Paths.get(existingMaterial.getFilePath()));
                } catch (IOException e) {
                    System.err.println("Não foi possível excluir o arquivo antigo: " + e.getMessage());
                }
            }
            String nomeArquivo = UUID.randomUUID().toString() + "_" + arquivo.getOriginalFilename();
            Path caminhoCompleto = uploadDir.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), caminhoCompleto);
            updatedMaterial.setFilePath(caminhoCompleto.toString());
        } else {
            updatedMaterial.setFilePath(existingMaterial.getFilePath());
        }
        Material materialSalvo = materialRepository.save(updatedMaterial);
        return materialMapper.toDTO(materialSalvo);
    }

    public void deleteById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Material.class, id));
        if (material.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(material.getFilePath()));
            } catch (IOException e) {
                System.err.println("Não foi possível excluir o arquivo: " + e.getMessage());
            }
        }
        materialRepository.delete(material);
    }

    public List<MaterialDTO> filterByInterestArea(List<InterestArea> areas) {
        if (areas == null || areas.isEmpty()) {
            return listAll();
        }

        List<Material> materiais = new ArrayList<>();
        for (InterestArea area : areas) {
            materiais.addAll(materialRepository.findByInterestAreaContaining(area));
        }

        return materiais.stream()
                .distinct()
                .map(materialMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<MaterialDTO> suggestMaterials(Long usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, usuarioId));

        Set<InterestArea> areasDeInteresse = new HashSet<>();

        if (usuario.getRole() == br.edu.ufape.plataforma.mentoria.enums.UserRole.MENTOR) {
            Mentor mentor = mentorRepository.findByUserId(usuarioId);
            if (mentor != null) {
                areasDeInteresse.addAll(mentor.getInterestArea());
            }
        } else {
            // Verificar se é um mentorado
            Mentored mentored = mentoredRepository.findByUserId(usuarioId);
            if (mentored != null) {
                areasDeInteresse.addAll(mentored.getInterestArea());
            }
        }

        if (areasDeInteresse.isEmpty()) {
            List<Material> materiaisRecentes = materialRepository.findTop10ByOrderByIdDesc();
            return materiaisRecentes.stream()
                    .map(materialMapper::toDTO)
                    .collect(Collectors.toList());
        }

        List<Material> materiaisSugeridos = new ArrayList<>();
        for (InterestArea area : areasDeInteresse) {
            materiaisSugeridos.addAll(materialRepository.findByInterestAreaContaining(area));
        }

        return materiaisSugeridos.stream()
                .distinct()
                .limit(20)
                .map(materialMapper::toDTO)
                .collect(Collectors.toList());
    }
}
