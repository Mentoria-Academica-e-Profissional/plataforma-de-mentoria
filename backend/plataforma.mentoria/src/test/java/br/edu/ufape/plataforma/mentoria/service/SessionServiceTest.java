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
        mentor = new Mentor("Guest Mentor", "98765432100",
                LocalDate.of(1990, 1, 1),
                Course.ADMINISTRACAO, guest,
                "Mentor profissional", AffiliationType.GESTOR,
                List.of("Gestão de Projetos"), List.of(InterestArea.CIBERSEGURANCA));
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

        // Mocka o retorno do sessionRepository.findByUserAndGuestOrUserAndGuest
        when(sessionRepository.findByMentorIdAndMentoredId(mentor.getId(), mentored.getId()))
                .thenReturn(List.of(session));

        // Mocka a conversão da entidade Session para SessionDTO
        when(sessionMapper.toDTO(session)).thenReturn(sessionDTO);

        // Chama o método do serviço
        List<SessionDTO> sessionHistory = sessionService.findSessionHistoryBetweenUsers(mentor.getId(), mentored.getId());

        // Verificações
        assertNotNull(sessionHistory);
        assertFalse(sessionHistory.isEmpty());
        assertEquals(1, sessionHistory.size());
        assertEquals(sessionDTO, sessionHistory.get(0));
    }

    @Test
    void findSessionHistoryMentor() {
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        // Retorna sessão só em findByUserId, findByGuestId vazio
        when(sessionRepository.findByMentorId(mentor.getId()))
                .thenReturn(List.of(session));

        when(sessionMapper.toDTO(session)).thenReturn(sessionDTO);

        List<SessionDTO> sessionHistoryUser = sessionService.findSessionHistoryMentor(mentor.getId());

        assertNotNull(sessionHistoryUser);
        assertFalse(sessionHistoryUser.isEmpty());
        assertEquals(1, sessionHistoryUser.size());
        assertEquals(sessionDTO, sessionHistoryUser.get(0));
    }

   @Test
    void findSessionHistoryMentored() {
       when(mentoredRepository.findById(mentored.getId())).thenReturn(Optional.of(mentored));

       // Retorna sessão só em findByUserId, findByGuestId vazio
       when(sessionRepository.findByMentoredId(mentored.getId()))
               .thenReturn(List.of(session));

       when(sessionMapper.toDTO(session)).thenReturn(sessionDTO);

       List<SessionDTO> sessionHistoryUser = sessionService.findSessionHistoryMentored(mentored.getId());

       assertNotNull(sessionHistoryUser);
        assertFalse(sessionHistoryUser.isEmpty());
        assertEquals(1, sessionHistoryUser.size());
        assertEquals(sessionDTO, sessionHistoryUser.get(0));
    }

}