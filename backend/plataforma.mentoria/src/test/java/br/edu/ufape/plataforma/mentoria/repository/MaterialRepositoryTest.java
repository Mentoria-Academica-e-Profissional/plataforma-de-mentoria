package br.edu.ufape.plataforma.mentoria.repository;

import br.edu.ufape.plataforma.mentoria.enums.*;
import br.edu.ufape.plataforma.mentoria.model.Material;
import br.edu.ufape.plataforma.mentoria.model.Mentor;
import br.edu.ufape.plataforma.mentoria.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MaterialRepositoryTest {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Test
    void findByMaterialTypeAndInterestArea() {
        // Preparando um mentor para associar ao material
         User user = new User();
        user.setEmail("mentor2@teste.com");
        user.setPassword("senhaSegura123");
        user.setRole(UserRole.MENTOR);

        Mentor mentor = new Mentor.Builder()
                .fullName("Carlos Mendes")
                .cpf("45678912300")
                .specializations(List.of("Análise de Dados"))
                .affiliationType(AffiliationType.DOCENTE)
                .birthDate(LocalDate.of(1988, 3, 10))
                .professionalSummary("Especialista em dados")
                .user(user)
                .interestArea(List.of(InterestArea.CIBERSEGURANCA, InterestArea.DESENVOLVIMENTO_DE_SOFTWARE))
                .course(Course.ADMINISTRACAO)
                .build();
        mentorRepository.save(mentor);

        // Criando materiais com diferentes tipos e áreas
        Material material1 = new Material.Builder()
                .title("Artigo sobre Engenharia")
                .materialType(MaterialType.VIDEO)
                .interestArea(Set.of(InterestArea.CIBERSEGURANCA))
                .userUploader(user)
                .build();
        materialRepository.save(material1);

        Material material2 = new Material.Builder()
                .title("Livro sobre Cibersegurança")
                .materialType(MaterialType.VIDEO)
                .interestArea(Set.of(InterestArea.CIBERSEGURANCA))
                .userUploader(user)
                .build();
        materialRepository.save(material2);

        // Testando a busca
        List<Material> encontrados = materialRepository.findByMaterialTypeAndInterestArea(
                MaterialType.VIDEO, InterestArea.CIBERSEGURANCA);

        assertFalse(encontrados.isEmpty());
        assertEquals(2, encontrados.size());
        assertEquals("Artigo sobre Engenharia", encontrados.getFirst().getTitle());
    }

    @Test
    void findByMaterialType() {
        // Preparando um mentor
        User user = new User();
        user.setEmail("mentor2@teste.com");
        user.setPassword("senhaSegura123");
        user.setRole(UserRole.MENTOR);

        Mentor mentor = new Mentor.Builder()
                .fullName("Carlos Mendes")
                .cpf("45678912300")
                .specializations(List.of("Análise de Dados"))
                .affiliationType(AffiliationType.DOCENTE)
                .birthDate(LocalDate.of(1988, 3, 10))
                .professionalSummary("Especialista em dados")
                .user(user)
                .interestArea(List.of(InterestArea.CIBERSEGURANCA))
                .course(Course.ADMINISTRACAO)
                .build();
        mentorRepository.save(mentor);

        // Criando materiais do mesmo tipo
        Material material1 = new Material.Builder()
                .title("Livro de Programação")
                .materialType(MaterialType.VIDEO)
                .interestArea(Set.of(InterestArea.DESENVOLVIMENTO_DE_SOFTWARE))
                .userUploader(user)
                .build();
        materialRepository.save(material1);

        Material material2 = new Material.Builder()
                .title("Livro de Redes")
                .materialType(MaterialType.VIDEO)
                .interestArea(Set.of(InterestArea.CIBERSEGURANCA))
                .userUploader(user)
                .build();
        materialRepository.save(material2);

        // Testando a busca por tipo
        List<Material> encontrados = materialRepository.findByMaterialType(MaterialType.VIDEO);

        assertEquals(2, encontrados.size());
    }

    @Test
    void findByInterestArea() {
        // Preparando um mentor
        User user = new User();
        user.setEmail("mentor3@teste.com");
        user.setPassword("senhaSegura123");
        user.setRole(UserRole.MENTOR);

        Mentor mentor = new Mentor.Builder()
                .fullName("Carlos Mendes")
                .cpf("45678912300")
                .specializations(List.of("Análise de Dados"))
                .affiliationType(AffiliationType.DOCENTE)
                .birthDate(LocalDate.of(1988, 3, 10))
                .professionalSummary("Especialista em dados")
                .user(user)
                .interestArea(List.of(InterestArea.CIBERSEGURANCA))
                .course(Course.ADMINISTRACAO)
                .build();
        mentorRepository.save(mentor);

        // Criando materiais com diferentes áreas
        Material material1 = new Material.Builder()
                .title("Artigo sobre Dados")
                .materialType(MaterialType.LINK)
                .interestArea(Set.of(InterestArea.ARTES_E_DESIGN))
                .userUploader(user)
                .build();
        materialRepository.save(material1);

        Material material2 = new Material.Builder()
                .title("Tutorial sobre Dados")
                .materialType(MaterialType.DOCUMENTO)
                .interestArea(Set.of(InterestArea.FINANCAS_E_CONTABILIDADE, InterestArea.ARTES_E_DESIGN))
                .userUploader(user)
                .build();
        materialRepository.save(material2);

        Material material3 = new Material.Builder()
                .title("Livro sobre Engenharia")
                .materialType(MaterialType.VIDEO)
                .interestArea(Set.of(InterestArea.ARTES_E_DESIGN))
                .userUploader(user)
                .build();
        materialRepository.save(material3);

        // Testando a busca por área de interesse
        List<Material> encontrados = materialRepository.findByInterestArea(InterestArea.ARTES_E_DESIGN);
        assertEquals(3, encontrados.size());
    }
}