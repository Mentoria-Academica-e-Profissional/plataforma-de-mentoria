package br.edu.ufape.plataforma.mentoria.controller;

import br.edu.ufape.plataforma.mentoria.dto.MentoredDTO;
import br.edu.ufape.plataforma.mentoria.enums.Course;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
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
import java.util.Collections;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MentoredControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    private static final String TEST_PASSWORD = "senha123";

    private String createUniqueUser() {
        String email = "mentored" + System.currentTimeMillis() + "@test.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword(TEST_PASSWORD);
        user.setRole(UserRole.MENTORADO);
        userRepository.save(user);
        return email;
    }

    private MentoredDTO buildValidMentoredDTO(String cpf) {
        MentoredDTO mentoredDTO = new MentoredDTO();
        mentoredDTO.setFullName("Test Mentored");
        mentoredDTO.setCpf(cpf);
        mentoredDTO.setBirthDate(java.time.LocalDate.of(1995, 5, 5));
        mentoredDTO.setCourse(Course.CIENCIA_DA_COMPUTACAO);
        mentoredDTO.setAcademicSummary("Resumo acadêmico de teste");
        mentoredDTO.setInterestArea(Collections.singletonList(InterestArea.TECNOLOGIA_DA_INFORMACAO));
        return mentoredDTO;
    }

    private String generateUniqueCpf(String prefix) {
        String millis = String.valueOf(System.currentTimeMillis());
        return prefix + millis.substring(millis.length() - (14 - prefix.length()));
    }

    @Test
    void shouldCreateAndGetMentored() throws Exception {
        String email = createUniqueUser();
        String cpf = generateUniqueCpf("12345");
        MentoredDTO mentoredDTO = buildValidMentoredDTO(cpf);
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userAuth =
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(email).roles("MENTORED");

        ResultActions createResult = mockMvc.perform(post("/api/mentored") // CORRIGIDO
                .with(userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentoredDTO)));
        createResult.andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName", is("Test Mentored")));

        mockMvc.perform(get("/api/mentored").with(userAuth)) // CORRIGIDO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldReturnNotFoundForNonexistentMentored() throws Exception {
        String email = createUniqueUser();
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userAuth =
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(email).roles("MENTORED");
        mockMvc.perform(get("/api/mentored/999999").with(userAuth)) // CORRIGIDO
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateMentored() throws Exception {
        String email = createUniqueUser();
        String cpf = generateUniqueCpf("54321");
        MentoredDTO mentoredDTO = buildValidMentoredDTO(cpf);
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userAuth =
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(email).roles("MENTORED");

        String response = mockMvc.perform(post("/api/mentored") // CORRIGIDO
                .with(userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentoredDTO)))
                .andReturn().getResponse().getContentAsString();
        MentoredDTO created = objectMapper.readValue(response, MentoredDTO.class);

        created.setFullName("Mentored Updated");
        mockMvc.perform(put("/api/mentored/" + created.getId()) // CORRIGIDO
                .with(userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Mentored Updated")));
    }

    @Test
    void shouldDeleteMentored() throws Exception {
        String email = createUniqueUser();
        String cpf = generateUniqueCpf("67890");
        MentoredDTO mentoredDTO = buildValidMentoredDTO(cpf);
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userAuth =
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(email).roles("MENTORED");

        String response = mockMvc.perform(post("/api/mentored") // CORRIGIDO
                .with(userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentoredDTO)))
                .andReturn().getResponse().getContentAsString();
        MentoredDTO created = objectMapper.readValue(response, MentoredDTO.class);

        mockMvc.perform(delete("/api/mentored/" + created.getId()) // CORRIGIDO
                .with(userAuth))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("sucesso")));
    }

    @Test
    void shouldGetMentoredById() throws Exception {
        String email = createUniqueUser();
        String cpf = generateUniqueCpf("11111");
        MentoredDTO mentoredDTO = buildValidMentoredDTO(cpf);
        var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(email).roles("MENTORED");

        String response = mockMvc.perform(post("/api/mentored") // CORRIGIDO
                .with(userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentoredDTO)))
                .andReturn().getResponse().getContentAsString();
        MentoredDTO created = objectMapper.readValue(response, MentoredDTO.class);

        mockMvc.perform(get("/api/mentored/" + created.getId()).with(userAuth)) // CORRIGIDO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Test Mentored")));
    }

    @Test
    void shouldReturnBadRequestForInvalidMentored() throws Exception {
        String email = createUniqueUser();
        var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(email).roles("MENTORED");
        MentoredDTO mentoredDTO = buildValidMentoredDTO("123"); // CPF inválido

        mockMvc.perform(post("/api/mentored") // CORRIGIDO
                .with(userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentoredDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictForDuplicateCpf() throws Exception {
        String email = createUniqueUser();
        String cpf = generateUniqueCpf("22222");
        MentoredDTO mentoredDTO = buildValidMentoredDTO(cpf);
        var userAuth = org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(email).roles("MENTORED");

        mockMvc.perform(post("/api/mentored") // CORRIGIDO
                .with(userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentoredDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/mentored") // CORRIGIDO
                .with(userAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentoredDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        String cpf = generateUniqueCpf("33333");
        MentoredDTO mentoredDTO = buildValidMentoredDTO(cpf);
        mockMvc.perform(post("/api/mentored") // CORRIGIDO
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentoredDTO)))
                .andExpect(status().isUnauthorized());
    }
}