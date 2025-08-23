package br.edu.ufape.plataforma.mentoria.model;

import java.time.LocalDate;
import java.util.List;
import br.edu.ufape.plataforma.mentoria.enums.Course;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Person {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Course course;

    @ElementCollection(targetClass = InterestArea.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "interest_area")
    private List<InterestArea> interestArea;

    public Person() {

    }

    public Person(String fullName, String cpf, LocalDate birthDate, Course course, List<InterestArea> interestArea) {
        this.fullName = fullName;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.course = course;
        this.interestArea = interestArea;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<InterestArea> getInterestArea() {
        return interestArea;
    }

    public void setInterestArea(List<InterestArea> interestArea) {
        this.interestArea = interestArea;
    }
}