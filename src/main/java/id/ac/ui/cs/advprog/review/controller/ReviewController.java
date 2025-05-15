package id.ac.ui.cs.advprog.review.controller;

import id.ac.ui.cs.advprog.review.dto.ReviewDTO;
import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.service.ReviewService;
import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewRepository repository;

    private ReviewDTO toDTO(ReviewModel model) {
        if (model == null) return null;
        return ReviewDTO.builder()
                .id(model.getId())
                .eventId(model.getEventId())
                .userId(model.getUserId())
                .rating(model.getRating())
                .comment(model.getComment())
                .createdDate(model.getCreatedDate())
                .updatedDate(model.getUpdatedDate())
                .status(model.getStatus())
                .build();
    }

    private ReviewModel toEntity(ReviewDTO dto) {
        if (dto == null) return null;
        return ReviewModel.builder()
                .id(dto.getId())
                .eventId(dto.getEventId())
                .userId(dto.getUserId())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdDate(dto.getCreatedDate())
                .updatedDate(dto.getUpdatedDate())
                .status(dto.getStatus())
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDTO createReview(@RequestBody ReviewDTO request) {
        ReviewModel model = toEntity(request);
        if (model.getStatus() == null) {
            model.setStatus(ReviewStatus.APPROVED);
        }
        ReviewModel saved = reviewService.createReview(model);
        return toDTO(saved);
    }

    @PutMapping("/{id}")
    public ReviewDTO updateReview(@PathVariable UUID id, @RequestBody ReviewDTO request) {
        ReviewModel existingReview = repository.findById(id);
        if (existingReview == null) {
            throw new RuntimeException("Review dengan ID " + id + " tidak ditemukan");
        }

        if (request.getRating() != null) {
            existingReview.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            existingReview.setComment(request.getComment());
        }

        ReviewModel updated = reviewService.updateReview(existingReview);
        return toDTO(updated);
    }

    @DeleteMapping("/delete-review/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable UUID id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/event/{eventId}")
    public List<ReviewDTO> getReviewsByEventId(@PathVariable UUID eventId) {
        List<ReviewModel> models = reviewService.getReviewsByEventId(eventId);
        return models.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/event/{eventId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable UUID eventId) {
        Double average = reviewService.calculateEventAverageRating(eventId);
        return ResponseEntity.ok(average);
    }
}