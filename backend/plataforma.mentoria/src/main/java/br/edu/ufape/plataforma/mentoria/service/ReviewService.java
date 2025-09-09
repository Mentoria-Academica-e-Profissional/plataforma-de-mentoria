package br.edu.ufape.plataforma.mentoria.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import br.edu.ufape.plataforma.mentoria.dto.ReviewDTO;
import br.edu.ufape.plataforma.mentoria.dto.ReviewResponseDTO;
import br.edu.ufape.plataforma.mentoria.exceptions.BusinessException;
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.model.Review;
import br.edu.ufape.plataforma.mentoria.model.Session;
import br.edu.ufape.plataforma.mentoria.repository.ReviewRepository;
import br.edu.ufape.plataforma.mentoria.repository.SessionRepository;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.plataforma.mentoria.enums.*;
import br.edu.ufape.plataforma.mentoria.mapper.ReviewMapper;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SessionRepository sessionRepository;

    public ReviewService(ReviewRepository reviewRepository, SessionRepository sessionRepository) {
        this.reviewRepository = reviewRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public ReviewResponseDTO createReview(ReviewDTO dto) {

        Session session = sessionRepository.findById(dto.getSessionId())
            .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada"));

        if (session.getStatus() != Status.COMPLETED) {
            throw new BusinessException("A avaliação só pode ser feita para sessões concluídas.");
        }

        boolean reviewAlreadyExists = reviewRepository.existsBySessionIdAndReviewerRole(dto.getSessionId(), dto.getReviewerRole());
        
        if (reviewAlreadyExists) {
            throw new BusinessException("Esta sessão já foi avaliada por este usuário.");
        }

        Review review = new Review(dto.getScore(), dto.getComment(), session.getMentor(), session.getMentored(), session, dto.getReviewerRole());
        Review savedReview = reviewRepository.save(review);
        return ReviewMapper.mapToResponseReviewDTO(savedReview);
    }

    public List<ReviewResponseDTO> getReceivedReviewsForUser(Long userId) {
        List<Review> reviews = reviewRepository.findReviewsReceivedByUser(
            userId, 
            UserRole.MENTOR, 
            UserRole.MENTORADO
        );

        return reviews.stream()
                .map(ReviewMapper::mapToResponseReviewDTO)
                .collect(Collectors.toList());
    }

    // public List<ReviewResponseDTO> getCreatedReviewsByUser(Long userId, UserRole userRole) {
    //     List<Review> reviews = reviewRepository.findReviewsCreatedByUserRole(userId, userRole);
        
    //     return reviews.stream()
    //             .map(ReviewMapper::mapToResponseReviewDTO)
    //             .collect(Collectors.toList());
    // }

    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
            .map(ReviewMapper::mapToResponseReviewDTO)
            .collect(Collectors.toList());
    }
    
}
