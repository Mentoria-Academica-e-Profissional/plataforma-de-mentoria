package br.edu.ufape.plataforma.mentoria.repository;

import br.edu.ufape.plataforma.mentoria.enums.UserRole;
import br.edu.ufape.plataforma.mentoria.model.Review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

    /**
     * Verifica se já existe uma avaliação para uma dada sessão e um papel de avaliador.
     * Retorna 'true' se uma avaliação já existir, 'false' caso contrário.
     * @param sessionId O ID da sessão a ser verificada.
     * @param reviewerRole O papel do usuário que está tentando avaliar.
     * @return boolean
     */
    boolean existsBySessionIdAndReviewerRole(Long sessionId, UserRole reviewerRole);

    
    /**
     * Encontra todas as avaliações que um determinado usuário (seja ele mentor ou mentorado) recebeu.
     * @param userId O ID do usuário (que é o mesmo para User, Mentor e Mentored).
     * @param mentorRole O enum UserRole.MENTOR.
     * @param mentoredRole O enum UserRole.MENTORADO.
     * @return Uma lista de avaliações recebidas pelo usuário.
     */
    @Query("SELECT r FROM Review r WHERE (r.mentor.id = :userId AND r.reviewerRole = :mentoredRole) OR (r.mentored.id = :userId AND r.reviewerRole = :mentorRole)")
    List<Review> findReviewsReceivedByUser(
        @Param("userId") Long userId,
        @Param("mentorRole") UserRole mentorRole,
        @Param("mentoredRole") UserRole mentoredRole
    );
    
}
