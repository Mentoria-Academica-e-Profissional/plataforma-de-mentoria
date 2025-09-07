package br.edu.ufape.plataforma.mentoria.model;

import java.time.LocalDateTime;

import br.edu.ufape.plataforma.mentoria.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @SequenceGenerator(name = "review_id_seq", sequenceName = "review_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "review_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull(message = "A nota é obrigatória")
    @Min(value = 1, message = "A nota minima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    private int score;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentored_id", nullable = false)
    private Mentored mentored;

    @NotNull(message = "A role é obrigatória")
    private UserRole reviewerRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Review(){}

    
    public Review(int score, String comment, Mentor mentor, Mentored mentored, Session session, UserRole reviewerRole) {
        this.score = score;
        this.comment = comment;
        this.mentor = mentor;
        this.mentored = mentored;
        this.reviewerRole = reviewerRole;
        this.session = session;
    }

    public Long getId() {
        return id;
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

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public Mentored getMentored() {
        return mentored;
    }

    public void setMentored(Mentored mentored) {
        this.mentored = mentored;
    }

    public UserRole getReviewerRole() {
        return reviewerRole;
    }

    public void setReviewerRole(UserRole reviewerRole) {
        this.reviewerRole = reviewerRole;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
