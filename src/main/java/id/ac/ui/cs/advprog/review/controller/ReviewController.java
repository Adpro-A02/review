package id.ac.ui.cs.advprog.review.controller;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewModel createReview(@RequestBody ReviewModel request) {
        return reviewService.createReview(request);
    }

    @PutMapping("/{id}")
    public ReviewModel updateReview(@PathVariable UUID id, @RequestBody ReviewModel request) {
        request.setId(id);
        return reviewService.updateReview(request);
    }

    @DeleteMapping("/delete-review/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable UUID id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/event/{eventId}")
    public List<ReviewModel> getReviewsByEventId(@PathVariable UUID eventId) {
        return reviewService.getReviewsByEventId(eventId);
    }

    @GetMapping("/event/{eventId}/average")
    public Double getAverageRating(@PathVariable UUID eventId) {
        return reviewService.calculateEventAverageRating(eventId);
    }
}