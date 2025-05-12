package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repository;
    private final NotificationService notificationService;

    public ReviewModel createReview(ReviewModel review) {
        if (validateReview(review)) {
            ReviewModel savedReview = repository.save(review);
            notificationService.sendReviewCreatedNotification(savedReview);
            return savedReview;
        }
        throw new RuntimeException("Review tidak valid");
    }

    public ReviewModel updateReview(ReviewModel review) {
        if (validateReview(review)) {
            ReviewModel updatedReview = repository.save(review);
            notificationService.sendReviewApprovedNotification(updatedReview);
            return updatedReview;
        }
        throw new RuntimeException("Review tidak valid untuk diperbarui");
    }

    public void deleteReview(UUID id) {
        repository.deleteById(id);
    }

    public boolean validateReview(ReviewModel review) {
        return review.getRating() >= 1 && review.getRating() <= 5;
    }

    public Double calculateEventAverageRating(UUID eventId) {
        List<ReviewModel> reviews = repository.findAllByStatus(ReviewStatus.APPROVED);

        return reviews.stream()
                .filter(r -> r.getEventId().equals(eventId))
                .mapToInt(ReviewModel::getRating)
                .average()
                .orElse(0.0);
    }

    public List<ReviewModel> getReviewsByEventId(UUID eventId) {
        return repository.findAllByEventId(eventId);
    }
}