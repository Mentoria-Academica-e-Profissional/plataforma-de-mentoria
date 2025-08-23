package br.edu.ufape.plataforma.mentoria.controller;

import java.util.Collections;
import java.util.List;
import br.edu.ufape.plataforma.mentoria.service.contract.MentorSearchServiceInterface;
import br.edu.ufape.plataforma.mentoria.service.contract.MentorServiceInterface;
import br.edu.ufape.plataforma.mentoria.service.contract.MentoredSearchServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MentorServiceInterface mentorService;

    @Autowired
    private MentoredSearchServiceInterface mentoredSearchService;

    @Autowired
    private MentorSearchServiceInterface mentorSearchService;

    @Autowired
    private MentorMapper mentorMapper;

    @GetMapping("/{idMentor}")
    public ResponseEntity<MentorDTO> getMentorDetails(@PathVariable Long idMentor) {
        MentorDTO mentorDTO = mentorSearchService.getMentorDetailsDTO(idMentor);
        if (mentorDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mentorDTO);
    }

    @PostMapping
    public ResponseEntity<MentorDTO> createMentor(@Valid @RequestBody MentorDTO mentorDTO) {
        MentorDTO savedMentorDTO = mentorService.createMentor(mentorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMentorDTO);
    }

     @PutMapping("/{idMentor}")
     public ResponseEntity<MentorDTO> updateMentor(@PathVariable Long idMentor,
            @Valid @RequestBody MentorDTO mentorDTO){
        MentorDTO updatedMentorDTO = mentorService.updateMentor(idMentor, mentorDTO);
         if (updatedMentorDTO == null) {
            return ResponseEntity.notFound().build();
         }
         return ResponseEntity.ok(updatedMentorDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MentorDTO> updateMentor(@PathVariable Long id, @RequestBody @Valid UpdateMentorDTO updateMentorDTO) {
        Mentor updatedMentor = mentorService.updateMentor(id, updateMentorDTO);
        MentorDTO dto = mentorMapper.toDTO(updatedMentor);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{idMentor}")
    public ResponseEntity<String> deleteMentor(@PathVariable Long idMentor) {
        try {
            mentorService.deleteById(idMentor);
            return ResponseEntity.ok("Mentor(a) removido(a) com sucesso!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/mentoreds/search")
    public ResponseEntity<List<MentoredDTO>> searchMentored(
            @RequestParam(required = false) String interestArea){

        if (interestArea == null || interestArea.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        InterestArea interestAreaEnum = null;
        try {
            interestAreaEnum = InterestArea.valueOf(interestArea);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<MentoredDTO> results = mentoredSearchService.findByInterestArea(interestAreaEnum);
        return ResponseEntity.ok(results);
    }

    @GetMapping
    public ResponseEntity<List<Mentor>> getAllMentors(){
        List<Mentor> results = mentorSearchService.getAllMentors();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/me")
    public ResponseEntity<MentorDTO> getCurrentMentor() {
        MentorDTO mentor = mentorSearchService.getCurrentMentor();
        if (mentor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mentor);
    }
}
