package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewModel createReview(ReviewModel review);
    ReviewModel updateReview(ReviewModel review);
    void deleteReview(UUID id);
    boolean validateReview(ReviewModel review);
    Double calculateEventAverageRating(UUID eventId);
    List<ReviewModel> getReviewsByEventId(UUID eventId);
}
