package br.edu.ufape.plataforma.mentoria.controller;

import br.edu.ufape.plataforma.mentoria.dto.SessionDTO;
import br.edu.ufape.plataforma.mentoria.enums.Status;
import br.edu.ufape.plataforma.mentoria.model.Session;
import br.edu.ufape.plataforma.mentoria.model.Mentor;
import br.edu.ufape.plataforma.mentoria.model.Mentored;
import br.edu.ufape.plataforma.mentoria.model.User;
import br.edu.ufape.plataforma.mentoria.enums.AffiliationType;
import br.edu.ufape.plataforma.mentoria.enums.Course;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.service.contract.SessionServiceInterface;
import br.edu.ufape.plataforma.mentoria.mapper.SessionMapper;
import br.edu.ufape.plataforma.mentoria.repository.MentorRepository;
import br.edu.ufape.plataforma.mentoria.repository.MentoredRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDateTime;
import java.util.Collections;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SessionServiceInterface sessionService;
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private MentorRepository mentorRepository;
    @Autowired
    private MentoredRepository mentoredRepository;

    private SessionDTO buildValidSessionDTO() {
        // Cria e persiste um Mentor
        User mentorUser = new User();
        mentorUser.setEmail("mentor" + System.currentTimeMillis() + "@test.com");
        mentorUser.setPassword("senha123");
        mentorUser.setRole(br.edu.ufape.plataforma.mentoria.enums.UserRole.MENTOR);
        String mentorCpf = java.util.UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        if (mentorCpf.length() > 11) mentorCpf = mentorCpf.substring(0, 11);
        else while (mentorCpf.length() < 11) mentorCpf += "0";
        Mentor mentor = new Mentor.Builder()
                .fullName("Mentor Teste")
                .cpf(mentorCpf)
                .birthDate(java.time.LocalDate.of(1990, 1, 1))
                .course(Course.CIENCIA_DA_COMPUTACAO)
                .user(mentorUser)
                .professionalSummary("Resumo")
                .affiliationType(AffiliationType.DOCENTE)
                .specializations(java.util.Arrays.asList("Java"))
                .interestArea(java.util.Collections.singletonList(InterestArea.TECNOLOGIA_DA_INFORMACAO))
                .build();
        mentor = mentorRepository.save(mentor);

        // Cria e persiste um Mentored
        User mentoredUser = new User();
        mentoredUser.setEmail("mentored" + System.currentTimeMillis() + "@test.com");
        mentoredUser.setPassword("senha123");
        mentoredUser.setRole(br.edu.ufape.plataforma.mentoria.enums.UserRole.MENTORADO);
        Mentored mentored = new Mentored();
        mentored.setFullName("Mentorado Teste");
        String mentoredCpf = java.util.UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        if (mentoredCpf.length() > 11) mentoredCpf = mentoredCpf.substring(0, 11);
        else while (mentoredCpf.length() < 11) mentoredCpf += "1";
        mentored.setCpf(mentoredCpf);
        mentored.setBirthDate(java.time.LocalDate.of(2000, 1, 1));
        mentored.setCourse(Course.CIENCIA_DA_COMPUTACAO);
        mentored.setUser(mentoredUser);
        mentored.setInterestArea(java.util.Collections.singletonList(InterestArea.TECNOLOGIA_DA_INFORMACAO));
        mentored = mentoredRepository.save(mentored);

        SessionDTO dto = new SessionDTO();
        dto.setMentorId(mentor.getId());
        dto.setMentoredId(mentored.getId());
        dto.setDate(java.time.LocalDate.now().plusDays(1));
        dto.setStatus(Status.PENDING);
        dto.setTime(java.time.LocalTime.of(10, 0));
        dto.setLocation("Sala 101");
        dto.setMeetingTopic("Mentoria de Java");
        // Adicione outros campos obrigatórios conforme necessário
        return dto;
    }

    @Test
    @WithMockUser
    public void testCreateSession() throws Exception {
        SessionDTO sessionDTO = buildValidSessionDTO();
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(sessionDTO.getStatus().toString())));
    }

    @Test
    @WithMockUser
    public void testGetAllSessions() throws Exception {
        mockMvc.perform(get("/sessions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void testGetSessionById_NotFound() throws Exception {
        mockMvc.perform(get("/sessions/{id}", 99999L))
                .andExpect(status().isNotFound()); // Espera 404 para sessão inexistente
    }

    @Test
    @WithMockUser
    public void testUpdateSession() throws Exception {
        SessionDTO sessionDTO = buildValidSessionDTO();
        Session newSession = sessionService.createSession(sessionDTO);
        SessionDTO updatedDTO = sessionMapper.toDTO(newSession);
        updatedDTO.setStatus(Status.COMPLETED);
        mockMvc.perform(put("/sessions/{id}", newSession.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Status.COMPLETED.toString())));
    }

    @Test
    @WithMockUser
    public void testDeleteSession() throws Exception {
        SessionDTO sessionDTO = buildValidSessionDTO();
        Session newSession = sessionService.createSession(sessionDTO);
        mockMvc.perform(delete("/sessions/{id}", newSession.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void testUpdateSessionStatus() throws Exception {
        SessionDTO sessionDTO = buildValidSessionDTO();
        Session newSession = sessionService.createSession(sessionDTO);
        mockMvc.perform(patch("/sessions/{id}/status", newSession.getId())
                .param("newStatus", Status.CANCELLED.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(Status.CANCELLED.toString())));
    }
}
