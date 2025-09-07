package br.edu.ufape.plataforma.mentoria.mapper;

import org.springframework.stereotype.Component;
import br.edu.ufape.plataforma.mentoria.dto.ReviewResponseDTO;
import br.edu.ufape.plataforma.mentoria.model.Review;

@Component
public class ReviewMapper {
    public static ReviewResponseDTO mapToResponseReviewDTO(Review review){
        ReviewResponseDTO reviewResponse = new ReviewResponseDTO();
        reviewResponse.setId(review.getId());
        reviewResponse.setScore(review.getScore());
        reviewResponse.setComment(review.getComment());
        reviewResponse.setCreatedAt(review.getCreatedAt());
        reviewResponse.setSessionId(review.getSession().getId());
        reviewResponse.setMentorId(review.getMentor().getId());
        reviewResponse.setMentoredId(review.getMentored().getId());
        reviewResponse.setReviewerRole(review.getReviewerRole());
        reviewResponse.setSessionId(review.getSession().getId());

        return reviewResponse;
    }
}
