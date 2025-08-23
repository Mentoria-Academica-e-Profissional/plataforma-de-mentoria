package br.edu.ufape.plataforma.mentoria.controller;

import br.edu.ufape.plataforma.mentoria.dto.SessionDTO;
import br.edu.ufape.plataforma.mentoria.enums.Status;
import br.edu.ufape.plataforma.mentoria.mapper.SessionMapper;
import br.edu.ufape.plataforma.mentoria.model.Session;
import br.edu.ufape.plataforma.mentoria.service.contract.SessionServiceInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private SessionServiceInterface sessionService;

    @Autowired
    private SessionMapper sessionMapper;

    @GetMapping
    public ResponseEntity<List<SessionDTO>> getAllSessions() {
        List<SessionDTO> sessions = sessionService.findAll();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSessionById(@PathVariable Long id) {
        SessionDTO sessionDTO = sessionService.getSessionDTOById(id);
        return ResponseEntity.ok(sessionDTO);
    }

    @PostMapping
    public ResponseEntity<SessionDTO> createSession(@Valid @RequestBody SessionDTO sessionDTO) {
        Session newSession = sessionService.createSession(sessionDTO);
        return new ResponseEntity<>(sessionMapper.toDTO(newSession), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionDTO> updateSession(@PathVariable Long id, @Valid @RequestBody SessionDTO sessionDTO) {
        SessionDTO updatedSession = sessionService.updateSession(id, sessionDTO);
        return ResponseEntity.ok(updatedSession);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SessionDTO> updateSessionStatus(@PathVariable Long id, @RequestParam Status newStatus) {
        SessionDTO updatedSession = sessionService.updateSessionStatus(id, newStatus);
        return ResponseEntity.ok(updatedSession);
    }

    @GetMapping("/history/mentor/{mentorId}")
    public ResponseEntity<List<SessionDTO>> getSessionHistoryByMentor(@PathVariable Long mentorId) {
        List<SessionDTO> history = sessionService.findSessionHistoryMentor(mentorId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/mentored/{mentoredId}")
    public ResponseEntity<List<SessionDTO>> getSessionHistoryByMentored(@PathVariable Long mentoredId) {
        List<SessionDTO> history = sessionService.findSessionHistoryMentored(mentoredId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/between/{mentorId}/{mentoredId}")
    public ResponseEntity<List<SessionDTO>> getSessionHistoryBetweenUsers(@PathVariable Long mentorId, @PathVariable Long mentoredId) {
        List<SessionDTO> history = sessionService.findSessionHistoryBetweenUsers(mentorId, mentoredId);
        return ResponseEntity.ok(history);
    }
}
