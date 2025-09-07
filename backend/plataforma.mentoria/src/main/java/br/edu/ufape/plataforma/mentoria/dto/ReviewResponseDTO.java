package br.edu.ufape.plataforma.mentoria.dto;

import java.time.LocalDateTime;
import br.edu.ufape.plataforma.mentoria.enums.UserRole;

public class ReviewResponseDTO {
    private Long id;
    private int score;
    private String comment;
    private Long sessionId;
    private Long mentorId;
    private Long mentoredId;
    private UserRole reviewerRole;
    private LocalDateTime createdAt;
    
    public ReviewResponseDTO(){}
    
    public ReviewResponseDTO(Long id, int score, String comment, Long sessionId, Long mentorId, Long mentoredId, UserRole reviewerRole,
            LocalDateTime createdAt) {
        this.id = id;
        this.score = score;
        this.comment = comment;
        this.sessionId = sessionId;
        this.mentorId = mentorId;
        this.mentoredId = mentoredId;
        this.reviewerRole = reviewerRole;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public Long getMentoredId() {
        return mentoredId;
    }

    public void setMentoredId(Long mentoredId) {
        this.mentoredId = mentoredId;
    }

    public UserRole getReviewerRole() {
        return reviewerRole;
    }

    public void setReviewerRole(UserRole reviewerRole) {
        this.reviewerRole = reviewerRole;
    }
}