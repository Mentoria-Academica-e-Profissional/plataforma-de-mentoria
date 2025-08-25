package br.edu.ufape.plataforma.mentoria.service;

import br.edu.ufape.plataforma.mentoria.dto.MentoredDTO;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.mapper.MentoredMapper;
import br.edu.ufape.plataforma.mentoria.model.Mentored;
import br.edu.ufape.plataforma.mentoria.repository.MentoredRepository;
import br.edu.ufape.plataforma.mentoria.service.contract.MentoredSearchServiceInterface;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MentoredSearchService implements MentoredSearchServiceInterface {

    private final MentoredRepository mentoredRepository;
    private final MentoredMapper mentoredMapper;

    public MentoredSearchService(MentoredRepository mentoredRepository, MentoredMapper mentoredMapper) {
        this.mentoredRepository = mentoredRepository;
        this.mentoredMapper = mentoredMapper;
    }

    // Buscar mentorados por id //
    @Override
    public Mentored getMentoredById(Long id) {
        return mentoredRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Mentored.class, id));
    }
    @Override
    public MentoredDTO getMentoredDetailsDTO(Long id) {
        Mentored mentored = this.getMentoredById(id);
        return mentoredMapper.toDTO(mentored);
    }
    @Override
    public MentoredDTO getCurrentMentored() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return mentoredRepository.findByUserEmail(email)
                .map(mentoredMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(Mentored.class, email));
    }
    @Override
    public List<MentoredDTO> findByInterestArea(InterestArea interestArea) {
        List<Mentored> mentoreds = mentoredRepository.findByInterestArea(interestArea);
        return mentoreds.stream()
                .map(mentoredMapper::toDTO)
                .toList();
    }

}
