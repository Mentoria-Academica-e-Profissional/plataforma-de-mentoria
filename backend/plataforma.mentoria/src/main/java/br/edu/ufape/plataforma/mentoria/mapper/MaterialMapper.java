package br.edu.ufape.plataforma.mentoria.mapper;

import br.edu.ufape.plataforma.mentoria.dto.MaterialDTO;
import br.edu.ufape.plataforma.mentoria.model.Material;
import org.springframework.stereotype.Component;

@Component
public class MaterialMapper {

    public MaterialDTO toDTO(Material material) {
        if (material == null) {
            return null;
        }

        MaterialDTO dto = new MaterialDTO();
        dto.setId(material.getId());
        dto.setTitle(material.getTitle());
        dto.setMaterialType(material.getMaterialType());
        dto.setUrl(material.getUrl());
        dto.setFilePath(material.getFilePath());
        dto.setInterestArea(material.getInterestArea());

        // Verificar se o userUploader não é nulo antes de acessar seu ID
        if (material.getUserUploader() != null) {
            dto.setUserUploaderId(material.getUserUploader().getId());
        }

        return dto;
    }

    public Material toEntity(MaterialDTO dto) {
        if (dto == null) {
            return null;
        }

        Material material = new Material();
        material.setId(dto.getId());
        material.setTitle(dto.getTitle());
        material.setMaterialType(dto.getMaterialType());
        material.setUrl(dto.getUrl());
        material.setInterestArea(dto.getInterestArea());
        material.setFilePath(dto.getFilePath());

        return material;
    }
}