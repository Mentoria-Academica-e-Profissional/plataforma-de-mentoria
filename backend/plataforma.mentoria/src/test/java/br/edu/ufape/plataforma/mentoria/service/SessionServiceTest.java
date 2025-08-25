package br.edu.ufape.plataforma.mentoria.service;

import br.edu.ufape.plataforma.mentoria.dto.SessionDTO;
import br.edu.ufape.plataforma.mentoria.enums.*;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.mapper.SessionMapper;
import br.edu.ufape.plataforma.mentoria.model.Mentor;
import br.edu.ufape.plataforma.mentoria.model.Mentored;
import br.edu.ufape.plataforma.mentoria.model.Session;
import br.edu.ufape.plataforma.mentoria.model.User;
import br.edu.ufape.plataforma.mentoria.repository.MentorRepository;
import br.edu.ufape.plataforma.mentoria.repository.MentoredRepository;
import br.edu.ufape.plataforma.mentoria.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private MentoredRepository mentoredRepository;

    @Mock
    private SessionMapper sessionMapper;

    private Mentor mentor;
    private Mentored mentored;
    private Session session;
    private SessionDTO sessionDTO;

    @BeforeEach
    void setUp() {
        User user = new User("user@gmail.com", "Joestar@123", UserRole.MENTORADO);
        user.setId(1L);
        mentored = new Mentored("Joestar", "12345678900",
                LocalDate.of(2000, 1, 1),
                Course.ADMINISTRACAO, user,
                "Estudante de Administração", List.of(InterestArea.CIBERSEGURANCA));
        mentored.setId(1L);

        User guest = new User("guest@gmail.com", "Joestar@123", UserRole.MENTOR);
        guest.setId(2L);
        mentor = new Mentor.Builder()
                .fullName("Guest Mentor")
                .cpf("98765432100")
                .birthDate(LocalDate.of(1990, 1, 1))
                .course(Course.ADMINISTRACAO)
                .user(guest)
                .professionalSummary("Mentor profissional")
                .affiliationType(AffiliationType.GESTOR)
                .specializations(List.of("Gestão de Projetos"))
                .interestArea(List.of(InterestArea.CIBERSEGURANCA))
                .build();
        mentor.setId(2L);

        session = new Session(mentor, mentored,
                LocalDate.of(2023, 10, 1),
                LocalTime.of(10, 0),
                "Discussão sobre o projeto",
                "Discord");

       sessionDTO = new SessionDTO(
                mentor.getId(),
                mentored.getId(),
                LocalDate.of(2023, 10, 1),
                LocalTime.of(10, 0),
                "Discussão sobre o projeto",
                "Discord"
        );
    }

    @Test
    void createSession() {
        when(mentoredRepository.findById(mentored.getId())).thenReturn(Optional.of(mentored));
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(sessionMapper.toEntity(sessionDTO)).thenReturn(session);
        when(sessionRepository.save(session)).thenReturn(session);

        Session createdSession = sessionService.createSession(sessionDTO);

        assertNotNull(createdSession);
        assertEquals(session.getMeetingTopic(), createdSession.getMeetingTopic());
    }

    @Test
    void createSession_SameMentorAndMentored_ShouldThrow() {
        mentored.setId(1L);
        mentor.setId(1L);
        sessionDTO.setMentorId(1L);
        sessionDTO.setMentoredId(1L);

        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(mentoredRepository.findById(1L)).thenReturn(Optional.of(mentored));

        assertThrows(IllegalArgumentException.class, () -> sessionService.createSession(sessionDTO));
    }

    @Test
    void createSession_MentorNotFound_ShouldThrow() {
        when(mentorRepository.findById(sessionDTO.getMentorId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> sessionService.createSession(sessionDTO));
    }

    @Test
    void createSession_MentoredNotFound_ShouldThrow() {
        when(mentorRepository.findById(sessionDTO.getMentorId())).thenReturn(Optional.of(mentor));
        when(mentoredRepository.findById(sessionDTO.getMentoredId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sessionService.createSession(sessionDTO));
    }

    @Test
    void getSessionById() {
        when(sessionRepository.findById(session.getId())).thenReturn(java.util.Optional.of(session));

        Session result = sessionService.getSessionById(session.getId());

        assertNotNull(result);
        assertEquals(session.getId(), result.getId());
    }

    @Test
    void getSessionById_NotFound() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> sessionService.getSessionById(99L));
    }

    @Test
    void deleteSession() {
        when(sessionRepository.findById(session.getId())).thenReturn(java.util.Optional.of(session));
        sessionService.deleteSession(session.getId());


        verify(sessionRepository, times(1)).delete(session);
    }

    @Test
    void deleteSession_SessionNotFound() {
        Long fakeId = 999L;
        when(sessionRepository.findById(fakeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> sessionService.deleteSession(fakeId));
    }

    @Test
    void findSessionHistoryBetweenUsers() {
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(mentoredRepository.findById(mentored.getId())).thenReturn(Optional.of(mentored));

        when(sessionRepository.findByMentorIdAndMentoredId(mentor.getId(), mentored.getId()))
                .thenReturn(List.of(session));

     
        when(sessionMapper.toDTO(session)).thenReturn(sessionDTO);

   
        List<SessionDTO> sessionHistory = sessionService.findSessionHistoryBetweenUsers(mentor.getId(), mentored.getId());

        assertNotNull(sessionHistory);
        assertFalse(sessionHistory.isEmpty());
        assertEquals(1, sessionHistory.size());
        assertEquals(sessionDTO, sessionHistory.getFirst());
    }

    @Test
    void findSessionHistoryBetweenUsers_MentoredNotFound_ShouldThrow() {
        when(mentorRepository.findById(sessionDTO.getMentorId())).thenReturn(Optional.of(mentor));
        when(mentoredRepository.findById(sessionDTO.getMentoredId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> sessionService.findSessionHistoryBetweenUsers(mentor.getId(), mentored.getId()));
    }

    @Test
    void findSessionHistoryBetweenUsers_MentorNotFound_ShouldThrow() {
        when(mentorRepository.findById(sessionDTO.getMentorId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> sessionService.findSessionHistoryBetweenUsers(mentor.getId(), mentored.getId()));
    }

    @Test
    void findSessionHistoryMentor() {
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

    
        when(sessionRepository.findByMentorId(mentor.getId()))
                .thenReturn(List.of(session));

        when(sessionMapper.toDTO(session)).thenReturn(sessionDTO);

        List<SessionDTO> sessionHistoryUser = sessionService.findSessionHistoryMentor(mentor.getId());

        assertNotNull(sessionHistoryUser);
        assertFalse(sessionHistoryUser.isEmpty());
        assertEquals(1, sessionHistoryUser.size());
        assertEquals(sessionDTO, sessionHistoryUser.getFirst());
    }

    @Test
    void findSessionHistoryMentor_MentorNotFound_ShouldThrow() {
        when(mentorRepository.findById(sessionDTO.getMentorId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> sessionService.findSessionHistoryMentor(mentor.getId()));
    }

    @Test
    void findSessionHistoryMentored() {
       when(mentoredRepository.findById(mentored.getId())).thenReturn(Optional.of(mentored));

    
       when(sessionRepository.findByMentoredId(mentored.getId()))
               .thenReturn(List.of(session));

       when(sessionMapper.toDTO(session)).thenReturn(sessionDTO);

       List<SessionDTO> sessionHistoryUser = sessionService.findSessionHistoryMentored(mentored.getId());

       assertNotNull(sessionHistoryUser);
        assertFalse(sessionHistoryUser.isEmpty());
        assertEquals(1, sessionHistoryUser.size());
        assertEquals(sessionDTO, sessionHistoryUser.getFirst());
    }

    @Test
    void findSessionHistoryMentored_MentoredNotFound_ShouldThrow() {
        when(mentoredRepository.findById(sessionDTO.getMentoredId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> sessionService.findSessionHistoryMentored(mentored.getId()));
    }

    @Test
    void updateSession() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDTO(any(Session.class))).thenAnswer(inv -> {
            Session s = inv.getArgument(0);
            SessionDTO dto = new SessionDTO();
            dto.setDate(s.getDate());
            dto.setTime(s.getTime());
            dto.setMeetingTopic(s.getMeetingTopic());
            dto.setLocation(s.getLocation());
            dto.setStatus(s.getStatus());
            return dto;
        });

 
        session.setStatus(Status.CANCELLED);
        sessionDTO.setStatus(Status.CANCELLED);
        SessionDTO updated = sessionService.updateSession(session.getId(), sessionDTO);

        assertNotNull(updated);
        assertEquals(Status.CANCELLED, updated.getStatus());
    }

    @Test
    void getSessionDTOById() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(sessionMapper.toDTO(session)).thenReturn(sessionDTO);

        SessionDTO result = sessionService.getSessionDTOById(session.getId());
        assertNotNull(result);
        assertEquals(session.getStatus(), result.getStatus());
    }

    @Test
    void updateSessionStatus_PendingToAccepted() {
        session.setStatus(Status.PENDING);
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDTO(any(Session.class))).thenReturn(sessionDTO);

        SessionDTO result = sessionService.updateSessionStatus(session.getId(), Status.ACCEPTED);

        assertNotNull(result);
        verify(sessionRepository).save(session);
    }

    @Test
    void updateSessionStatus_AcceptedToCompleted() {
        session.setStatus(Status.ACCEPTED);
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDTO(any(Session.class))).thenReturn(sessionDTO);

        SessionDTO result = sessionService.updateSessionStatus(session.getId(), Status.COMPLETED);

        assertNotNull(result);
        verify(sessionRepository).save(session);
    }

    @Test
    void updateSessionStatus_PendingToInvalid_ShouldThrow() {
        session.setStatus(Status.PENDING);
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
    
        assertThrows(IllegalArgumentException.class, () ->
                sessionService.updateSessionStatus(session.getId(), Status.COMPLETED)
        );
    }

    @Test
    void updateSessionStatus_AcceptedToInvalid_ShouldThrow() {
        session.setStatus(Status.ACCEPTED);
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
  
        assertThrows(IllegalArgumentException.class, () ->
                sessionService.updateSessionStatus(session.getId(), Status.REJECTED)
        );
    }

    @Test
    void updateSessionStatus_FinalStatus_ShouldThrow() {
        session.setStatus(Status.REJECTED);
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.of(session));
        assertThrows(IllegalArgumentException.class, () ->
                sessionService.updateSessionStatus(session.getId(), Status.PENDING)
        );
    }

    @Test
    void findAll() {
        Session session2 = new Session(mentor, mentored,
                LocalDate.of(2023, 11, 1),
                LocalTime.of(15, 0),
                "Nova sessão",
                "Google Meet");

        when(sessionRepository.findAll()).thenReturn(List.of(session, session2));

        List<SessionDTO> result = sessionService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}