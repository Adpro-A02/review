package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ReviewService {
    CompletableFuture<ReviewModel> createReview(ReviewModel model);
    ReviewModel updateReview(ReviewModel review);
    void deleteReview(UUID id);
    boolean validateReview(ReviewModel review);
    Double calculateEventAverageRating(UUID eventId);
    List<ReviewModel> getReviewsByEventId(UUID eventId);
    List<ReviewModel> getReviewsForOrganizer(UUID eventId, UUID organizerId);
    ReviewModel flagReview(UUID reviewId, String role);
    List<ReviewModel> getReviewsByStatus(ReviewStatus status);
}

