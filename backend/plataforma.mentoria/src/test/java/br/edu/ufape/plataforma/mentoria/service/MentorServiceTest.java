package br.edu.ufape.plataforma.mentoria.service;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import br.edu.ufape.plataforma.mentoria.dto.MentorDTO;
import br.edu.ufape.plataforma.mentoria.dto.UpdateMentorDTO;
import br.edu.ufape.plataforma.mentoria.enums.AffiliationType;
import br.edu.ufape.plataforma.mentoria.enums.Course;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.mapper.MentorMapper;
import br.edu.ufape.plataforma.mentoria.model.Mentor;
import br.edu.ufape.plataforma.mentoria.repository.MentorRepository;
import br.edu.ufape.plataforma.mentoria.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

    @InjectMocks
    private MentorService mentorService;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private MentorMapper mentorMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorSearchService mentorSearchService;

    @Mock
    private Authentication authentication;

    @Test
    void testUpdateMentor_WithMentorEntity_Success() {
        Mentor mentor = new Mentor.Builder().build();
        Mentor savedMentor = new Mentor.Builder().build();
        Long id = 1L;

        when(mentorRepository.existsById(id)).thenReturn(true);
        when(mentorRepository.save(mentor)).thenReturn(savedMentor);

        Mentor result = mentorService.updateMentor(id, mentor);

        assertEquals(savedMentor, result);
        verify(mentorRepository).save(mentor);
    }

    @Test
    @SuppressWarnings("unused")
    void testUpdateMentor_WithMentorEntity_NotFound() {
        Mentor mentor = new Mentor.Builder().build();
        Long id = 1L;

        when(mentorRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> mentorService.updateMentor(id, mentor));
    }

    @Test
    void testUpdateMentor_WithMentorDTO_Success() {
        Long id = 1L;
        MentorDTO mentorDTO = new MentorDTO.Builder().build();
        Mentor existingMentor = new Mentor.Builder().build();
        Mentor mentorToUpdate = new Mentor.Builder().build();
        Mentor updatedMentor = new Mentor.Builder().build();
        MentorDTO updatedDTO = new MentorDTO.Builder().build();

        when(mentorSearchService.getMentorById(id)).thenReturn(existingMentor);
        when(mentorMapper.toEntity(mentorDTO)).thenReturn(mentorToUpdate);
        when(mentorRepository.save(mentorToUpdate)).thenReturn(updatedMentor);
        when(mentorMapper.toDTO(updatedMentor)).thenReturn(updatedDTO);

        MentorDTO result = mentorService.updateMentor(id, mentorDTO);

        assertEquals(updatedDTO, result);
        verify(mentorRepository).save(mentorToUpdate);
    }

    @Test
    void testUpdateMentor_WithUpdateMentorDTO_Success() {
        Long id = 1L;
        UpdateMentorDTO dto = new UpdateMentorDTO();
        Mentor mentor = new Mentor.Builder().build();

        when(mentorSearchService.getMentorById(id)).thenReturn(mentor);
        when(mentorRepository.save(mentor)).thenReturn(mentor);

        Mentor result = mentorService.updateMentor(id, dto);

        assertEquals(mentor, result);
        verify(mentorRepository).save(mentor);
    }

    @Test
    void testDeleteById_Success() {
        Long id = 1L;
        when(mentorRepository.existsById(id)).thenReturn(true);

        mentorService.deleteById(id);

        verify(mentorRepository).deleteById(id);
    }

    @Test
    @SuppressWarnings("unused")
    void testDeleteById_NotFound() {
        Long id = 1L;
        when(mentorRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> mentorService.deleteById(id));
    }

    @Test
    @SuppressWarnings("unused")
    void testUpdateMentor_WithMentorDTO_NotFound() {
        Long id = 2L;
        MentorDTO mentorDTO = new MentorDTO.Builder().build();

        when(mentorSearchService.getMentorById(id)).thenThrow(new EntityNotFoundException(Mentor.class, id));

        assertThrows(EntityNotFoundException.class, () -> mentorService.updateMentor(id, mentorDTO));
    }

    @Test
    void testUpdateMentor_WithUpdateMentorDTO_PartialUpdate() {
        Long id = 3L;
        UpdateMentorDTO dto = new UpdateMentorDTO();
        Mentor mentor = new Mentor.Builder().build();

        // Only set fullName and course
        dto.setFullName("New Name");
        dto.setCourse(Course.CIENCIA_DA_COMPUTACAO);

        when(mentorSearchService.getMentorById(id)).thenReturn(mentor);
        when(mentorRepository.save(mentor)).thenReturn(mentor);

        Mentor result = mentorService.updateMentor(id, dto);

        assertEquals(mentor, result);
        verify(mentorRepository).save(mentor);
        assertEquals("New Name", mentor.getFullName());
        assertEquals(Course.CIENCIA_DA_COMPUTACAO, mentor.getCourse());
    }

    @Test
    void testUpdateMentor_WithUpdateMentorDTO_NotFound() {
        Long id = 4L;
        UpdateMentorDTO dto = new UpdateMentorDTO();

        when(mentorSearchService.getMentorById(id)).thenThrow(new EntityNotFoundException(Mentor.class, id));

        assertThrows(EntityNotFoundException.class, () -> mentorService.updateMentor(id, dto));
    }

    @Test
    void testUpdateMentor_WithUpdateMentorDTO_AllFields() {
        Long id = 5L;
        UpdateMentorDTO dto = new UpdateMentorDTO();
        Mentor mentor = new Mentor.Builder().build();

        // Set all fields to test complete coverage
        dto.setFullName("Complete Name");
        dto.setBirthDate(LocalDate.of(1990, 5, 15));
        dto.setCourse(Course.ADMINISTRACAO);
        dto.setInterestArea(Arrays.asList(InterestArea.TECNOLOGIA_DA_INFORMACAO, InterestArea.CIENCIA_DE_DADOS_E_IA));
        dto.setProfessionalSummary("Experienced professional");
        dto.setAffiliationType(AffiliationType.DOCENTE);
        dto.setSpecializations(Arrays.asList("AI", "Data Science"));

        when(mentorSearchService.getMentorById(id)).thenReturn(mentor);
        when(mentorRepository.save(mentor)).thenReturn(mentor);

        Mentor result = mentorService.updateMentor(id, dto);

        assertEquals(mentor, result);
        verify(mentorRepository).save(mentor);
        assertEquals("Complete Name", mentor.getFullName());
        assertEquals(LocalDate.of(1990, 5, 15), mentor.getBirthDate());
        assertEquals(Course.ADMINISTRACAO, mentor.getCourse());
        assertEquals(Arrays.asList(InterestArea.TECNOLOGIA_DA_INFORMACAO, InterestArea.CIENCIA_DE_DADOS_E_IA),
                mentor.getInterestArea());
        assertEquals("Experienced professional", mentor.getProfessionalSummary());
        assertEquals(AffiliationType.DOCENTE, mentor.getAffiliationType());
        assertEquals(Arrays.asList("AI", "Data Science"), mentor.getSpecializations());
    }

    @Test
    void testUpdateMentor_WithUpdateMentorDTO_NullFields() {
        Long id = 6L;
        UpdateMentorDTO dto = new UpdateMentorDTO();
        Mentor mentor = new Mentor.Builder().build();

        // Set some initial values to verify they don't change when null is passed
        mentor.setFullName("Original Name");
        mentor.setBirthDate(LocalDate.of(1985, 1, 1));
        mentor.setCourse(Course.MEDICINA);
        mentor.setInterestArea(Arrays.asList(InterestArea.ADMINISTRACAO_E_GESTAO));
        mentor.setProfessionalSummary("Original Summary");
        mentor.setAffiliationType(AffiliationType.PESQUISADOR);
        mentor.setSpecializations(Arrays.asList("Original Spec"));

        // All fields in dto are null (default)
        when(mentorSearchService.getMentorById(id)).thenReturn(mentor);
        when(mentorRepository.save(mentor)).thenReturn(mentor);

        Mentor result = mentorService.updateMentor(id, dto);

        assertEquals(mentor, result);
        verify(mentorRepository).save(mentor);
        // Verify original values are preserved when null is passed
        assertEquals("Original Name", mentor.getFullName());
        assertEquals(LocalDate.of(1985, 1, 1), mentor.getBirthDate());
        assertEquals(Course.MEDICINA, mentor.getCourse());
        assertEquals(Arrays.asList(InterestArea.ADMINISTRACAO_E_GESTAO), mentor.getInterestArea());
        assertEquals("Original Summary", mentor.getProfessionalSummary());
        assertEquals(AffiliationType.PESQUISADOR, mentor.getAffiliationType());
        assertEquals(Arrays.asList("Original Spec"), mentor.getSpecializations());
    }
}
