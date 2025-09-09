package br.edu.ufape.plataforma.mentoria.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.plataforma.mentoria.dto.ReviewDTO;
import br.edu.ufape.plataforma.mentoria.dto.ReviewResponseDTO;
import br.edu.ufape.plataforma.mentoria.model.User;
import br.edu.ufape.plataforma.mentoria.service.AuthService;
import br.edu.ufape.plataforma.mentoria.service.ReviewService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/avaliacoes")
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;

    public ReviewController(ReviewService reviewService, AuthService authService) {
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        ReviewResponseDTO createdReview = reviewService.createReview(reviewDTO);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<ReviewResponseDTO>> getMyReviews() {
        User user = authService.getCurrentUser();
        List<ReviewResponseDTO> reviews = reviewService.getReceivedReviewsForUser(user.getId());
        return ResponseEntity.ok(reviews);
    }

    // @GetMapping("/criadas")
    // public ResponseEntity<List<ReviewResponseDTO>> getMyCreatedReviews() {
    //     User user = authService.getCurrentUser();
    //     List<ReviewResponseDTO> reviews = reviewService.getCreatedReviewsByUser(user.getId(), user.getRole());
    //     return ResponseEntity.ok(reviews);
    // }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }
}
