package br.edu.ufape.plataforma.mentoria.controller;

import java.util.Collections;
import java.util.List;
import br.edu.ufape.plataforma.mentoria.service.contract.MentorSearchServiceInterface;
import br.edu.ufape.plataforma.mentoria.service.contract.MentorServiceInterface;
import br.edu.ufape.plataforma.mentoria.service.contract.MentoredSearchServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.edu.ufape.plataforma.mentoria.dto.MentorDTO;
import br.edu.ufape.plataforma.mentoria.dto.MentoredDTO;
import br.edu.ufape.plataforma.mentoria.dto.UpdateMentorDTO;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.mapper.MentorMapper;
import br.edu.ufape.plataforma.mentoria.model.Mentor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/mentor")
public class MentorController {

    private final MentorServiceInterface mentorService;
    private final MentoredSearchServiceInterface mentoredSearchService;
    private final MentorSearchServiceInterface mentorSearchService;
    private final MentorMapper mentorMapper;

    public MentorController(
            MentorServiceInterface mentorService,
            MentoredSearchServiceInterface mentoredSearchService,
            MentorSearchServiceInterface mentorSearchService,
            MentorMapper mentorMapper) {
        this.mentorService = mentorService;
        this.mentoredSearchService = mentoredSearchService;
        this.mentorSearchService = mentorSearchService;
        this.mentorMapper = mentorMapper;
    }

    @GetMapping("/{idMentor}")
    public ResponseEntity<MentorDTO> getMentorDetails(@PathVariable Long idMentor) {
        try {
            MentorDTO mentorDTO = mentorSearchService.getMentorDetailsDTO(idMentor);
            return ResponseEntity.ok(mentorDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<MentorDTO> createMentor(@Valid @RequestBody MentorDTO mentorDTO) {
        try {
            MentorDTO savedMentorDTO = mentorService.createMentor(mentorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMentorDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{idMentor}")
    public ResponseEntity<MentorDTO> updateMentor(@PathVariable Long idMentor,
            @Valid @RequestBody MentorDTO mentorDTO) {
        try {
            MentorDTO updatedMentorDTO = mentorService.updateMentor(idMentor, mentorDTO);
            if (updatedMentorDTO == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedMentorDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MentorDTO> partialUpdateMentor(@PathVariable Long id,
            @RequestBody @Valid UpdateMentorDTO updateMentorDTO) {
        try {
            Mentor updatedMentor = mentorService.updateMentor(id, updateMentorDTO);
            if (updatedMentor == null) {
                return ResponseEntity.notFound().build();
            }
            MentorDTO dto = mentorMapper.toDTO(updatedMentor);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{idMentor}")
    public ResponseEntity<String> deleteMentor(@PathVariable Long idMentor) {
        try {
            mentorService.deleteById(idMentor);
            return ResponseEntity.ok("Mentor(a) removido(a) com sucesso!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    @GetMapping("/mentoreds/search")
    public ResponseEntity<List<MentoredDTO>> searchMentored(
            @RequestParam(required = false) InterestArea interestArea) {

        if (interestArea == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<MentoredDTO> results = mentoredSearchService.findByInterestArea(interestArea);
        return ResponseEntity.ok(results);
    }

    @GetMapping
    public ResponseEntity<List<MentorDTO>> getAllMentors() {
        try {
            List<Mentor> results = mentorSearchService.getAllMentors();
            if (results == null || results.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            List<MentorDTO> dtos = results.stream().map(mentorMapper::toDTO).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<MentorDTO> getCurrentMentor() {
        try {
            MentorDTO mentor = mentorSearchService.getCurrentMentor();
            return ResponseEntity.ok(mentor);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}