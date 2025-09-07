package br.edu.ufape.plataforma.mentoria.repository;

import java.util.List;
import java.util.Optional;

import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ufape.plataforma.mentoria.model.Mentor;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {
    boolean existsByCpf(String cpf);
    Optional<Mentor> findByUserEmail(String email);
    Mentor findByUserId(Long Id);
    List<Mentor> findByInterestAreaAndSpecializationsContaining(InterestArea interestArea, String specialization);
}