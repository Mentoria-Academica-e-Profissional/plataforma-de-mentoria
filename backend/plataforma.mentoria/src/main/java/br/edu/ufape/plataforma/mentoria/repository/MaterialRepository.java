package br.edu.ufape.plataforma.mentoria.repository;

import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.enums.MaterialType;
import br.edu.ufape.plataforma.mentoria.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByMaterialTypeAndInterestArea(MaterialType materialType, InterestArea interestArea);
    List<Material> findByMaterialType(MaterialType materialType);
    List<Material> findByInterestArea(InterestArea interestArea);
    List<Material> findByInterestAreaContaining(InterestArea area);
    List<Material> findByMaterialTypeAndInterestAreaContaining(MaterialType tipo, InterestArea area);
    List<Material> findTop10ByOrderByIdDesc();
}