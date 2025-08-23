package br.edu.ufape.plataforma.mentoria.service;

import br.edu.ufape.plataforma.mentoria.dto.MentorDTO;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.mapper.MentorMapper;
import br.edu.ufape.plataforma.mentoria.model.Mentor;
import br.edu.ufape.plataforma.mentoria.repository.MentorRepository;
import br.edu.ufape.plataforma.mentoria.service.contract.MentorSearchServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorSearchService implements MentorSearchServiceInterface {

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private MentorMapper mentorMapper;

    @Override
    public Mentor getMentorById(Long id) {
        return mentorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Mentor.class, id));
    }

    @Override
    public MentorDTO getMentorDetailsDTO(Long id) {
        Mentor mentor = this.getMentorById(id);
        return mentorMapper.toDTO(mentor);
    }

    @Override
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    @Override
    public MentorDTO getCurrentMentor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return mentorRepository.findByUserEmail(email)
                .map(mentorMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(Mentor.class, email));
    }

    @Override
    public List<MentorDTO> findByInterestAreaAndSpecializations(InterestArea interestArea, String specialization) {
        List<Mentor> mentors = mentorRepository.findByInterestAreaAndSpecializationsContaining(interestArea,
                specialization);
        return mentors.stream()
                .map(mentorMapper::toDTO)
                .collect(Collectors.toList());
    }
}
