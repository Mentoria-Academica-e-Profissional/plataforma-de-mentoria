package br.edu.ufape.plataforma.mentoria.dto;

import br.edu.ufape.plataforma.mentoria.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class SessionDTO {

    private Long id;

    @NotNull(message = "O ID do mentor é obrigatório")
    private Long mentorId;

    @NotNull(message = "O ID do mentorado é obrigatório")
    private Long mentoredId;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime time;

    @NotNull
    private String meetingTopic;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private String location;

    public SessionDTO() {
    }

    public SessionDTO(Long mentorId, Long mentoredId, LocalDate date, LocalTime time, String meetingTopic, String location) {
        this.mentorId = mentorId;
        this.mentoredId = mentoredId;
        this.date = date;
        this.time = time;
        this.meetingTopic = meetingTopic;
        this.status = Status.PENDING;
        this.location = location;
    }

    public @NotNull(message = "O ID do mentor é obrigatório") Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(@NotNull(message = "O ID do mentor é obrigatório") Long mentorId) {
        this.mentorId = mentorId;
    }

    public @NotNull(message = "O ID do mentorado é obrigatório") Long getMentoredId() {
        return mentoredId;
    }

    public void setMentoredId(@NotNull(message = "O ID do mentorado é obrigatório") Long mentoredId) {
        this.mentoredId = mentoredId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getMeetingTopic() {
        return meetingTopic;
    }

    public void setMeetingTopic(String meetingTopic) {
        this.meetingTopic = meetingTopic;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
