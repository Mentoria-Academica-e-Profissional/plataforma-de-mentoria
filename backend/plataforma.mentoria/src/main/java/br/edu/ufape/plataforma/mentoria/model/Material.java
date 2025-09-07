package br.edu.ufape.plataforma.mentoria.model;

import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.enums.MaterialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "material")
public class Material {

    @Id
    @SequenceGenerator(name = "material_id_seq", sequenceName = "material_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "material_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(nullable = false, length = 180)
    @NotBlank
    @Size(max = 180)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MaterialType materialType;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(length = 600)
    private String url;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "interest_area_id", joinColumns = @JoinColumn(name = "mentor_id"))
    @Column(name = "material_interest_area")
    private Set<InterestArea> interestArea = new HashSet<>();

    @ManyToOne
    private User userUploader;

    public Material() {
        // Construtor padrão exigido pelo JPA
    }

    public static class Builder {
        private String title;
        private MaterialType materialType;
        private String filePath;
        private String url;
        private Set<InterestArea> interestArea = new HashSet<>();
        private User userUploader;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder materialType(MaterialType materialType) {
            this.materialType = materialType;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder interestArea(Set<InterestArea> interestArea) {
            this.interestArea = interestArea;
            return this;
        }

        public Builder userUploader(User userUploader) {
            this.userUploader = userUploader;
            return this;
        }

        public Material build() {
            Material material = new Material();
            material.setTitle(this.title);
            material.setMaterialType(this.materialType);
            material.setFilePath(this.filePath);
            material.setUrl(this.url);
            material.setInterestArea(this.interestArea);
            material.setUserUploader(this.userUploader);
            return material;
        }
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<InterestArea> getInterestArea() {
        return interestArea;
    }

    public void setInterestArea(Set<InterestArea> interestArea) {
        this.interestArea = interestArea;
    }

    public User getUserUploader() {
        return userUploader;
    }

    public void setUserUploader(User userUploader) {
        this.userUploader = userUploader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Material that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}