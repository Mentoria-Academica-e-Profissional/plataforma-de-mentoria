package br.edu.ufape.plataforma.mentoria.dto;

import br.edu.ufape.plataforma.mentoria.enums.UserRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewDTO {
        
    @NotNull(message = "A nota é obrigatória")
    @Min(value = 1, message = "A nota minima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    private int score;

    @Size(max = 600, message = "O comentário não pode exceder 600 caracteres.")
    private String comment;

    @NotNull(message = "O ID da sessão é obrigatório.")
    private Long sessionId;

    @NotNull(message =  "O ROLE do usuário avaliador é obrigatório")
    private UserRole reviewerRole;

    public ReviewDTO(int score, String comment, Long sessionId, UserRole reviewerRole) {
        this.score = score;
        this.comment = comment;
        this.sessionId = sessionId;
        this.reviewerRole = reviewerRole;
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

    public UserRole getReviewerRole() {
        return reviewerRole;
    }

    public void setReviewerRole(UserRole reviewerRole) {
        this.reviewerRole = reviewerRole;
    }
}
