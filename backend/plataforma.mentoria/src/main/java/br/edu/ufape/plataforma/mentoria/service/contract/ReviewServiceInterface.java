package br.edu.ufape.plataforma.mentoria.service.contract;

import java.util.List;

import br.edu.ufape.plataforma.mentoria.dto.ReviewDTO;
import br.edu.ufape.plataforma.mentoria.dto.ReviewResponseDTO;

public interface ReviewServiceInterface {

    public ReviewResponseDTO createReview(ReviewDTO dto);
    public List<ReviewResponseDTO> getReceivedReviewsForUser(Long userId);
    
}
