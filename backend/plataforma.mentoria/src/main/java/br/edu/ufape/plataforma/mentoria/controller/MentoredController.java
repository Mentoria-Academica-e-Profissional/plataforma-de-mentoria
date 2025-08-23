package br.edu.ufape.plataforma.mentoria.controller;

import java.util.Collections;
import java.util.List;
import br.edu.ufape.plataforma.mentoria.service.contract.MentorSearchServiceInterface;
import br.edu.ufape.plataforma.mentoria.service.contract.MentoredSearchServiceInterface;
import br.edu.ufape.plataforma.mentoria.service.contract.MentoredServiceInterface;
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
import br.edu.ufape.plataforma.mentoria.dto.UpdateMentoredDTO;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.mapper.MentoredMapper;
import br.edu.ufape.plataforma.mentoria.model.Mentored;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/mentored")
public class MentoredController {

    @Autowired
    private MentoredServiceInterface mentoredService;

    @Autowired
    private MentoredSearchServiceInterface mentoredSearchService;
    
    @Autowired
    MentorSearchServiceInterface mentorSearchService;

    @Autowired
    private MentoredMapper mentoredMapper;

    @GetMapping("/{idMentored}")
    public ResponseEntity<MentoredDTO> getMentoredDetails(@PathVariable Long idMentored) {
        MentoredDTO mentoredDTO = mentoredSearchService.getMentoredDetailsDTO(idMentored);
        return ResponseEntity.ok(mentoredDTO);
    }

    @PostMapping
    public ResponseEntity<MentoredDTO> createMentored(@Valid @RequestBody MentoredDTO mentoredDTO) {
        MentoredDTO savedMentoredDTO = mentoredService.createMentored(mentoredDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMentoredDTO);
    }

    @PutMapping("/{idMentored}")
    public ResponseEntity<MentoredDTO> updateMentored(@PathVariable Long idMentored,
            @Valid @RequestBody MentoredDTO mentoredDTO) {
        MentoredDTO updatedMentoredDTO = mentoredService.updateMentored(idMentored, mentoredDTO);
        if (updatedMentoredDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedMentoredDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MentoredDTO> updateMentored(@PathVariable Long id, @RequestBody @Valid UpdateMentoredDTO dto) {
        Mentored updated = mentoredService.updateMentored(id, dto);
        MentoredDTO responseDto = mentoredMapper.toDTO(updated);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{idMentored}")
    public ResponseEntity<String> deleteMentored(@PathVariable Long idMentored) {
        try {
            mentoredService.deleteById(idMentored);
            return ResponseEntity.ok("Mentored(a) removido(a) com sucesso!");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/mentors/search")
    public ResponseEntity<List<MentorDTO>> searchMentor(
            @RequestParam(required = false) String interestArea,
            @RequestParam(required = false) String specializations) {

        if (interestArea == null && (specializations == null || specializations.isEmpty())) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        InterestArea interestAreaEnum = null;
        if (interestArea != null && !interestArea.isBlank()) {
            try {
                interestAreaEnum = InterestArea.valueOf(interestArea);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }
        }

        List<MentorDTO> results = mentorSearchService.findByInterestAreaAndSpecializations(interestAreaEnum, specializations);
        return ResponseEntity.ok(results);
    }

    @GetMapping
    public ResponseEntity<List<Mentored>> getAllMentoreds(){
        List<Mentored> results = mentoredService.getAllMentored();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/me")
    public ResponseEntity<MentoredDTO> getCurrentMentored() {
        MentoredDTO mentored = mentoredSearchService.getCurrentMentored();
        return ResponseEntity.ok(mentored);
    }

}