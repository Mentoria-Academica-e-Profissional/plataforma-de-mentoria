package br.edu.ufape.plataforma.mentoria.dto;

import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.enums.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class MaterialDTO {

    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 180, message = "O título deve ter no máximo 180 caracteres")
    private String title;

    @NotNull(message = "O tipo de material é obrigatório")
    private MaterialType materialType;

    private String url;

    private String filePath;

    @NotNull(message = "Ao menos uma área de interesse deve ser informada")
    private Set<InterestArea> interestArea = new HashSet<>();

    private Long userUploaderId;

    // Construtores
    public MaterialDTO() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Set<InterestArea> getInterestArea() {
        return interestArea;
    }

    public void setInterestArea(Set<InterestArea> interestArea) {
        this.interestArea = interestArea;
    }

    public Long getUserUploaderId() {
        return userUploaderId;
    }

    public void setUserUploaderId(Long userUploaderId) {
        this.userUploaderId = userUploaderId;
    }
}