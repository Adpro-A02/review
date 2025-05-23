package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository repository;
    private final NotificationService notificationService;

    public ReviewModel createReview(ReviewModel review) {
        if (validateReview(review)) {
            try {
                ReviewModel savedReview = repository.save(review);
                notificationService.sendReviewCreatedNotification(savedReview);
                return savedReview;
            } catch (PersistenceException e) {
                throw new RuntimeException("Error saving review: " + e.getMessage(), e);
            }
        }
        throw new RuntimeException("Review tidak valid");
    }

    public ReviewModel updateReview(ReviewModel review) {
        if (validateReview(review)) {
            try {
                ReviewModel updatedReview = repository.save(review);
                notificationService.sendReviewApprovedNotification(updatedReview);
                return updatedReview;
            } catch (PersistenceException e) {
                throw new RuntimeException("Error updating review: " + e.getMessage(), e);
            }
        }
        throw new RuntimeException("Review tidak valid untuk diperbarui");
    }

    public void deleteReview(UUID id) {
        try {
            repository.deleteById(id);
        } catch (PersistenceException e) {
            throw new RuntimeException("Error deleting review: " + e.getMessage(), e);
        }
    }

    public ReviewModel approveReview(UUID reviewId) {
        ReviewModel review = repository.findById(reviewId);
        if (review == null) {
            throw new RuntimeException("Review tidak ditemukan dengan id: " + reviewId);
        }

        if (!validateReview(review)) {
            throw new RuntimeException("Review tidak valid dan tidak bisa di-approve.");
        }

        try {
            ReviewModel updatedReview = repository.updateStatus(reviewId, ReviewStatus.APPROVED);
            notificationService.sendReviewApprovedNotification(updatedReview);
            return updatedReview;
        } catch (PersistenceException e) {
            throw new RuntimeException("Gagal approve review: " + e.getMessage(), e);
        }
    }

    public boolean validateReview(ReviewModel review) {
        return review.getRating() != null
                && review.getRating() >= 1
                && review.getRating() <= 5
                && review.getComment() != null
                && !review.getComment().trim().isEmpty();
    }

    public Double calculateEventAverageRating(UUID eventId) {
        try {
            List<ReviewModel> reviews = repository.findAllByEventIdAndStatus(eventId, ReviewStatus.APPROVED);
            System.out.println("Reviews found: " + reviews.size());
            for (ReviewModel r : reviews) {
                System.out.println("Review rating: " + r.getRating() + ", eventId: " + r.getEventId());
            }
            if (reviews.isEmpty()) {
                return 0.0;
            }
            return reviews.stream()
                    .mapToInt(ReviewModel::getRating)
                    .average()
                    .orElse(0.0);
        } catch (PersistenceException e) {
            throw new RuntimeException("Error saat menghitung rata-rata rating: " + e.getMessage(), e);
        }
    }

    public List<ReviewModel> getReviewsByEventId(UUID eventId) {
        try {
            return repository.findAllByEventId(eventId);
        } catch (PersistenceException e) {
            throw new RuntimeException("Error saat mengambil reviews: " + e.getMessage(), e);
        }
    }

    public List<ReviewModel> getReviewsForOrganizer(UUID eventId, UUID organizerId) {
        try {

            List<ReviewModel> reviews = repository.findAllByEventIdAndOrganizerId(eventId, organizerId);
            if (reviews.isEmpty()) {
                throw new RuntimeException("Tidak ada review untuk event yang dikelola oleh organizer ini.");
            }
            return reviews;
        } catch (PersistenceException e) {
            throw new RuntimeException("Error saat mengambil review untuk organizer: " + e.getMessage(), e);
        }
    }
}