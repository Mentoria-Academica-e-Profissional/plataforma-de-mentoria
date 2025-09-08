package br.edu.ufape.plataforma.mentoria.controller;

import br.edu.ufape.plataforma.mentoria.enums.AffiliationType;

import br.edu.ufape.plataforma.mentoria.dto.MentorDTO;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.enums.Course;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import br.edu.ufape.plataforma.mentoria.model.User;
import br.edu.ufape.plataforma.mentoria.enums.UserRole;
import br.edu.ufape.plataforma.mentoria.repository.UserRepository;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MentorControllerIntegrationTest {
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private UserRepository userRepository;

        private static final String TEST_PASSWORD = "senha123";

        // Cria um usuário único e retorna o e-mail
        private String createUniqueUser() {
                String email = "mentor" + System.currentTimeMillis() + "@test.com";
                User user = new User();
                user.setEmail(email);
                user.setPassword(TEST_PASSWORD);
                user.setRole(UserRole.MENTOR);
                userRepository.save(user);
                return email;
        }

        private MentorDTO buildValidMentorDTO(String cpf) {
                MentorDTO mentorDTO = new MentorDTO();
                mentorDTO.setFullName("Test Mentor");
                mentorDTO.setCpf(cpf);
                mentorDTO.setBirthDate(java.time.LocalDate.of(1990, 1, 1));
                mentorDTO.setCourse(Course.CIENCIA_DA_COMPUTACAO);
                mentorDTO.setProfessionalSummary("Resumo profissional de teste");
                mentorDTO.setAffiliationType(AffiliationType.DOCENTE);
                mentorDTO.setSpecializations(java.util.Arrays.asList("Java", "Spring"));
                mentorDTO.setInterestArea(java.util.Collections.singletonList(InterestArea.TECNOLOGIA_DA_INFORMACAO));
                return mentorDTO;
        }

        // Gera um CPF único válido (11 a 14 dígitos)
        private String generateUniqueCpf(String prefix) {
                String millis = String.valueOf(System.currentTimeMillis());
                // Garante que o CPF tenha no máximo 14 caracteres
                return prefix + millis.substring(millis.length() - (14 - prefix.length()));
        }

        @Test
        void shouldCreateAndGetMentor() throws Exception {
                String email = createUniqueUser();
                String cpf = generateUniqueCpf("12345");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);

                // Autentica como o usuário criado
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                // Create mentor
                ResultActions createResult = mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)));
                createResult.andExpect(status().isCreated())
                                .andExpect(jsonPath("$.fullName", is("Test Mentor")));

                // Get all mentors
                mockMvc.perform(get("/mentor").with(userAuth))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", not(empty())));
        }

        @Test
        void shouldReturnNotFoundForNonexistentMentor() throws Exception {
                String email = createUniqueUser();
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");
                mockMvc.perform(get("/mentor/999999").with(userAuth))
                                .andExpect(status().isNotFound());
        }

        @Test
        void shouldUpdateMentor() throws Exception {
                String email = createUniqueUser();
                String cpf = generateUniqueCpf("54321");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                // Create mentor
                String response = mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andReturn().getResponse().getContentAsString();
                MentorDTO created = objectMapper.readValue(response, MentorDTO.class);

                // Update mentor
                created.setFullName("Mentor Updated");
                mockMvc.perform(put("/mentor/" + created.getId())
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(created)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.fullName", is("Mentor Updated")));
        }

        @Test
        void shouldDeleteMentor() throws Exception {
                String email = createUniqueUser();
                String cpf = generateUniqueCpf("67890");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                // Create mentor
                String response = mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andReturn().getResponse().getContentAsString();
                MentorDTO created = objectMapper.readValue(response, MentorDTO.class);

                // Delete mentor
                mockMvc.perform(delete("/mentor/" + created.getId())
                                .with(userAuth))
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString("sucesso")));
        }

        @Test
        void shouldGetMentorById() throws Exception {
                String email = createUniqueUser();
                String cpf = generateUniqueCpf("11111");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                // Cria mentor
                String response = mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andReturn().getResponse().getContentAsString();
                MentorDTO created = objectMapper.readValue(response, MentorDTO.class);

                // Busca mentor por id
                mockMvc.perform(get("/mentor/" + created.getId()).with(userAuth))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.fullName", is("Test Mentor")));
        }

        @Test
        void shouldReturnBadRequestForInvalidMentor() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");
                MentorDTO mentorDTO = buildValidMentorDTO("123"); // CPF inválido (curto)

                mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnConflictForDuplicateCpf() throws Exception {
                String email = createUniqueUser();
                String cpf = generateUniqueCpf("22222");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                // Cria mentor
                mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andExpect(status().isCreated());

                // Tenta criar mentor com mesmo CPF
                mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andExpect(status().isConflict());
        }

        @Test
        void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
                String cpf = generateUniqueCpf("33333");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);
                // Não autentica
                mockMvc.perform(post("/mentor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andExpect(status().isUnauthorized());
        }
}