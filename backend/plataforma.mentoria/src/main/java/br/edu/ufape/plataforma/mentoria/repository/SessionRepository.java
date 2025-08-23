package br.edu.ufape.plataforma.mentoria.repository;

import br.edu.ufape.plataforma.mentoria.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>{
    List<Session> findByMentorId(Long userId);
    List<Session> findByMentoredId(Long guestId);
    List<Session> findByMentorIdAndMentoredId(Long userId, Long guestId);
}
