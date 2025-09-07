package br.edu.ufape.plataforma.mentoria.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ufape.plataforma.mentoria.dto.SessionDTO;
import br.edu.ufape.plataforma.mentoria.enums.Status;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.mapper.SessionMapper;
import br.edu.ufape.plataforma.mentoria.model.Mentor;
import br.edu.ufape.plataforma.mentoria.model.Mentored;
import br.edu.ufape.plataforma.mentoria.model.Session;
import br.edu.ufape.plataforma.mentoria.repository.MentorRepository;
import br.edu.ufape.plataforma.mentoria.repository.MentoredRepository;
import br.edu.ufape.plataforma.mentoria.repository.SessionRepository;
import br.edu.ufape.plataforma.mentoria.service.contract.SessionServiceInterface;

@Service
public class SessionService implements SessionServiceInterface {
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final MentorRepository mentorRepository;
    private final MentoredRepository mentoredRepository;

    public SessionService(SessionRepository sessionRepository,
                         SessionMapper sessionMapper,
                         MentorRepository mentorRepository,
                         MentoredRepository mentoredRepository) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
        this.mentorRepository = mentorRepository;
        this.mentoredRepository = mentoredRepository;
    }

    @Override
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Session.class, id));
    }

    @Override
    public Session createSession(SessionDTO sessionDTO) {
        sessionDTO.setStatus(Status.PENDING);

        Mentor mentor = mentorRepository.findById(sessionDTO.getMentorId())
                .orElseThrow(() -> new EntityNotFoundException(Mentor.class, sessionDTO.getMentorId()));

        Mentored mentored = mentoredRepository.findById(sessionDTO.getMentoredId())
                .orElseThrow(() -> new EntityNotFoundException(Mentored.class, sessionDTO.getMentoredId()));

        if (mentor.getId().equals(mentored.getId())) {
            throw new IllegalArgumentException("Mentor e Mentorado não podem ser a mesma pessoa.");
        }
        Session session = sessionMapper.toEntity(sessionDTO);
        session.setMentor(mentor);
        session.setMentored(mentored);

        return sessionRepository.save(session);
    }

    @Override
    public SessionDTO updateSession(Long id, SessionDTO sessionDTO) {
        Session existingSession = getSessionById(id);

        existingSession.setDate(sessionDTO.getDate());
        existingSession.setTime(sessionDTO.getTime());
        existingSession.setMeetingTopic(sessionDTO.getMeetingTopic());
        existingSession.setLocation(sessionDTO.getLocation());
        existingSession.setStatus(sessionDTO.getStatus()); // Corrige atualização do status

        return sessionMapper.toDTO(sessionRepository.save(existingSession));
    }

    @Override
    public void deleteSession(Long id) {
        Session session = getSessionById(id);
        sessionRepository.delete(session);
    }

    @Override
    public SessionDTO getSessionDTOById(Long id) {
        Session session = getSessionById(id);
        return sessionMapper.toDTO(session);
    }

    @Override
    public SessionDTO updateSessionStatus(Long id, Status newStatus) {
        Session session = getSessionById(id);
        Status currentStatus = session.getStatus();

        if (currentStatus == Status.PENDING) {
            if (newStatus != Status.ACCEPTED && newStatus != Status.REJECTED && newStatus != Status.CANCELLED) {
                throw new IllegalArgumentException("Sessão pendente só pode ser Aceita, Rejeitada ou Cancelada.");
            }
        } else if (currentStatus == Status.ACCEPTED) {
            if (newStatus != Status.COMPLETED && newStatus != Status.CANCELLED) {
                throw new IllegalArgumentException("Sessão aceita só pode ser Concluída ou Cancelada.");
            }
        } else if (currentStatus == Status.REJECTED) {
            throw new IllegalArgumentException(
                    "A sessão já está em um estado final (" + currentStatus + ") e não pode ser alterada.");
        } else if (currentStatus == Status.CANCELLED) {
            throw new IllegalArgumentException(
                    "A sessão já está em um estado final (" + currentStatus + ") e não pode ser alterada.");
        } else if (currentStatus == Status.COMPLETED) {
            throw new IllegalArgumentException(
                    "A sessão já está em um estado final (" + currentStatus + ") e não pode ser alterada.");
        }

        session.setStatus(newStatus);
        return sessionMapper.toDTO(sessionRepository.save(session));
    }

    @Override
    public List<SessionDTO> findSessionHistoryBetweenUsers(Long mentorId, Long mentoredId) {
        mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(Mentor.class, mentorId));

        mentoredRepository.findById(mentoredId)
                .orElseThrow(() -> new EntityNotFoundException(Mentored.class, mentoredId));

        List<Session> sessions = sessionRepository.findByMentorIdAndMentoredId(mentorId, mentoredId);

        return sessions.stream().map(sessionMapper::toDTO).toList();
    }

    @Override
    public List<SessionDTO> findSessionHistoryMentor(Long mentorId) {
        mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(Mentor.class, mentorId));

        List<Session> sessions = sessionRepository.findByMentorId(mentorId);

        return sessions.stream().map(sessionMapper::toDTO).toList();
    }

    @Override
    public List<SessionDTO> findSessionHistoryMentored(Long mentoredId) {
        mentoredRepository.findById(mentoredId)
                .orElseThrow(() -> new EntityNotFoundException(Mentored.class, mentoredId));

        List<Session> sessions = sessionRepository.findByMentoredId(mentoredId);

        return sessions.stream().map(sessionMapper::toDTO).toList();
    }

    @Override
    public List<SessionDTO> findAll() {
        return sessionRepository.findAll().stream()
                .map(sessionMapper::toDTO)
                .toList();
    }
}
