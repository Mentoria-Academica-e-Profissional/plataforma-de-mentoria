package br.edu.ufape.plataforma.mentoria.controller;

import br.edu.ufape.plataforma.mentoria.enums.AffiliationType;
import br.edu.ufape.plataforma.mentoria.dto.MentorDTO;
import br.edu.ufape.plataforma.mentoria.dto.UpdateMentorDTO;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.enums.Course;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import br.edu.ufape.plataforma.mentoria.model.User;
import br.edu.ufape.plataforma.mentoria.enums.UserRole;
import br.edu.ufape.plataforma.mentoria.repository.UserRepository;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MentorControllerMissingIntegrationTest {
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private UserRepository userRepository;

        private static final String TEST_PASSWORD = "senha123";

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

        private UpdateMentorDTO buildValidUpdateMentorDTO() {
                UpdateMentorDTO updateDTO = new UpdateMentorDTO();
                updateDTO.setFullName("Updated Mentor Name");
                updateDTO.setProfessionalSummary("Updated professional summary");
                updateDTO.setSpecializations(java.util.Arrays.asList("Java", "Spring Boot", "Microservices"));
                return updateDTO;
        }

        private String generateUniqueCpf(String prefix) {
                String millis = String.valueOf(System.currentTimeMillis());
                return prefix + millis.substring(millis.length() - (14 - prefix.length()));
        }

        @Test
        void shouldPartiallyUpdateMentorWithPatch() throws Exception {
                String email = createUniqueUser();
                String cpf = generateUniqueCpf("44444");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                String response = mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andReturn().getResponse().getContentAsString();
                MentorDTO created = objectMapper.readValue(response, MentorDTO.class);

                UpdateMentorDTO updateDTO = buildValidUpdateMentorDTO();
                mockMvc.perform(patch("/mentor/" + created.getId())
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.fullName", is("Updated Mentor Name")))
                                .andExpect(jsonPath("$.professionalSummary", is("Updated professional summary")));
        }

        @Test
        void shouldReturnNotFoundForPatchWithInvalidId() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");
                UpdateMentorDTO updateDTO = buildValidUpdateMentorDTO();

                mockMvc.perform(patch("/mentor/999999")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void shouldSearchMentoredsByInterestArea() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(get("/mentor/mentoreds/search")
                                .param("interestArea", "TECNOLOGIA_DA_INFORMACAO")
                                .with(userAuth))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        void shouldReturnBadRequestForInvalidInterestArea() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(get("/mentor/mentoreds/search")
                                .param("interestArea", "INVALID_AREA")
                                .with(userAuth))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$", empty()));
        }

        @Test
        void shouldReturnEmptyListForBlankInterestArea() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(get("/mentor/mentoreds/search")
                                .param("interestArea", "")
                                .with(userAuth))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$", empty()));
        }

        @Test
        void shouldReturnEmptyListWhenNoInterestAreaProvided() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(get("/mentor/mentoreds/search")
                                .with(userAuth))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$", empty()));
        }

        @Test
        void shouldGetCurrentMentor() throws Exception {
                String email = createUniqueUser();
                String cpf = generateUniqueCpf("55555");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/mentor/me")
                                .with(userAuth))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.fullName", is("Test Mentor")));
        }

        @Test
        void shouldReturnNotFoundForCurrentMentorWhenNotExists() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(get("/mentor/me")
                                .with(userAuth))
                                .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnNotFoundForUpdateNonExistentMentor() throws Exception {
                String email = createUniqueUser();
                String cpf = generateUniqueCpf("66666");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(put("/mentor/999999")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnBadRequestForMalformedJson() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ invalid json }"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestForEmptyRequestBody() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(""))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnBadRequestForValidationErrors() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                MentorDTO invalidMentorDTO = new MentorDTO();

                invalidMentorDTO.setFullName("");
                invalidMentorDTO.setCpf("");

                mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidMentorDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnUnsupportedMediaTypeForWrongContentType() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                String cpf = generateUniqueCpf("88888");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);

                mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        void shouldReturnEmptyListWhenNoMentorsExist() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(get("/mentor")
                                .with(userAuth))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());

        }

        @Test
        void shouldReturnBadRequestForPatchWithInvalidBody() throws Exception {
                String email = createUniqueUser();
                String cpf = generateUniqueCpf("99999");
                MentorDTO mentorDTO = buildValidMentorDTO(cpf);
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                String response = mockMvc.perform(post("/mentor")
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mentorDTO)))
                                .andReturn().getResponse().getContentAsString();
                MentorDTO created = objectMapper.readValue(response, MentorDTO.class);

                mockMvc.perform(patch("/mentor/" + created.getId())
                                .with(userAuth)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ invalid json }"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldHandleCaseSensitiveInterestAreaSearch() throws Exception {
                String email = createUniqueUser();
                var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user(email).roles("MENTOR");

                mockMvc.perform(get("/mentor/mentoreds/search")
                                .param("interestArea", "tecnologia_da_informacao")
                                .with(userAuth))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$", empty()));
        }
}