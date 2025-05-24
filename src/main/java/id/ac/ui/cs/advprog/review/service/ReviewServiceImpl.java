package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository repository;
    private final NotificationService notificationService;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ReviewModel> createReview(ReviewModel model) {
        Optional<ReviewModel> existingReview = repository.findByUserIdAndEventId(model.getUserId(), model.getEventId());
        if (existingReview.isPresent()) {
            throw new IllegalStateException("User sudah pernah membuat review untuk event ini.");
        }

        ReviewModel saved = repository.save(model);
        return CompletableFuture.completedFuture(saved);
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


    @Override
    @Transactional
    public ReviewModel flagReview(UUID reviewId, String role) {
        if (!"Organizer".equalsIgnoreCase(role)) {
            throw new SecurityException("Hanya organizer yang dapat melakukan flag review.");
        }

        ReviewModel review = repository.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review tidak ditemukan dengan id " + reviewId);
        }

        if (review.getStatus() != ReviewStatus.APPROVED) {
            throw new IllegalStateException("Review hanya bisa di-flag jika statusnya APPROVED.");
        }

        return repository.updateStatus(reviewId, ReviewStatus.FLAGGED);
    }

    public List<ReviewModel> getReviewsByStatus(ReviewStatus status) {
        try {
            return repository.findAllByStatus(status);
        } catch (PersistenceException e) {
            throw new RuntimeException("Error saat mengambil review berdasarkan status: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ReviewModel cancelFlag(UUID reviewId, String role) {
        if (!"Organizer".equalsIgnoreCase(role)) {
            throw new SecurityException("Hanya organizer yang dapat membatalkan flag review.");
        }

        ReviewModel review = repository.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review tidak ditemukan dengan id " + reviewId);
        }

        if (review.getStatus() != ReviewStatus.FLAGGED) {
            throw new IllegalStateException("Review hanya bisa dibatalkan flag-nya jika statusnya FLAGGED.");
        }
        return repository.updateStatus(reviewId, ReviewStatus.APPROVED);
    }

}