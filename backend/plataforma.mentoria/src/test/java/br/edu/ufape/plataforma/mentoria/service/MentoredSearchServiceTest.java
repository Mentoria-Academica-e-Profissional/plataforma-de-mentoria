package br.edu.ufape.plataforma.mentoria.service;

import br.edu.ufape.plataforma.mentoria.dto.MentoredDTO;
import br.edu.ufape.plataforma.mentoria.enums.Course;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.enums.UserRole;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.mapper.MentoredMapper;
import br.edu.ufape.plataforma.mentoria.model.Mentored;
import br.edu.ufape.plataforma.mentoria.model.User;
import br.edu.ufape.plataforma.mentoria.repository.MentoredRepository;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentoredSearchServiceTest {

    @InjectMocks
    private MentoredSearchService mentoredSearchService;

    @Mock
    private MentoredRepository mentoredRepository;

    @Mock
    private MentoredMapper mentoredMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private Mentored mentored;

    @BeforeEach
    void setUp() {
        User user = new User("user@gmail.com", "Joestar@123", UserRole.MENTORADO);
        user.setId(1L);
        mentored = new Mentored("Joestar", "12345678900",
                LocalDate.of(2000, 1, 1),
                Course.ADMINISTRACAO, user,
                "Estudante de Administração", List.of(InterestArea.CIBERSEGURANCA));
        mentored.setId(1L);
    }

    @Test
    void getCurrentMentored() {
        String userEmail = "user@gmail.com";
        MentoredDTO expectedDto = new MentoredDTO();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn(userEmail);
        when(mentoredRepository.findByUserEmail(userEmail)).thenReturn(Optional.of(mentored));
        when(mentoredMapper.toDTO(mentored)).thenReturn(expectedDto);

        MentoredDTO actualDto = mentoredSearchService.getCurrentMentored();

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);

    }

    @Test
    void testGetCurrentMentored_NotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String userEmail = "notfound@gmail.com";
        when(authentication.getName()).thenReturn(userEmail);
        when(mentoredRepository.findByUserEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            mentoredSearchService.getCurrentMentored();
        });
    }

    @Test
    void findByInterestArea() {
        InterestArea areaDeInteresse = InterestArea.CIBERSEGURANCA;
        List<Mentored> mentoredList = List.of(mentored);
        MentoredDTO mentoredDTO = new MentoredDTO();

        when(mentoredRepository.findByInterestArea(areaDeInteresse)).thenReturn(mentoredList);
        when(mentoredMapper.toDTO(mentored)).thenReturn(mentoredDTO);

        List<MentoredDTO> result = mentoredSearchService.findByInterestArea(areaDeInteresse);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mentoredDTO, result.getFirst());

    }

    @Test
    void getMentoredById() {
        Long mentoredId = 1L;
        when(mentoredRepository.findById(mentoredId)).thenReturn(Optional.of(mentored));
        Mentored foundMentored = mentoredSearchService.getMentoredById(mentoredId);

        assertNotNull(foundMentored);
        assertEquals(mentoredId, foundMentored.getId());
    }

    @Test
    void getMentoredById_NotFound() {
        Long mentoredId = 2L;
        when(mentoredRepository.findById(mentoredId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            mentoredSearchService.getMentoredById(mentoredId);
        });
    }

    @Test
    void getMentoredDetailsDTO() {
        Long mentoredId = 1L;
        MentoredDTO expectedDto = new MentoredDTO();

        when(mentoredRepository.findById(mentoredId)).thenReturn(Optional.of(mentored));
        when(mentoredMapper.toDTO(mentored)).thenReturn(expectedDto);

        MentoredDTO actualDto = mentoredSearchService.getMentoredDetailsDTO(mentoredId);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }


}