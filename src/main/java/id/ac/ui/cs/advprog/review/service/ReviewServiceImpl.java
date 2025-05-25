package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.annotation.Timed;

import jakarta.persistence.PersistenceException;
import org.springframework.test.annotation.Timed;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository repository;
    private final NotificationService notificationService;

    @Override
    @Async("taskExecutor")
    @Transactional
    @Timed(value = "review.createReview.time", description = "Time taken to create review")
    public CompletableFuture<ReviewModel> createReview(ReviewModel model) {
        logger.info("Creating review for userId={}, eventId={}", model.getUserId(), model.getEventId());
        Optional<ReviewModel> existingReview = repository.findByUserIdAndEventId(model.getUserId(), model.getEventId());
        if (existingReview.isPresent()) {
            logger.warn("User {} sudah pernah membuat review untuk event {}", model.getUserId(), model.getEventId());
            throw new IllegalStateException("User sudah pernah membuat review untuk event ini.");
        }

        ReviewModel saved = repository.save(model);
        logger.info("Review created with id={}", saved.getId());
        return CompletableFuture.completedFuture(saved);
    }

    @Override
    @Timed(value = "review.updateReview.time", description = "Time taken to update review")
    public ReviewModel updateReview(ReviewModel review) {
        logger.info("Updating review with id={}", review.getId());
        if (validateReview(review)) {
            try {
                ReviewModel updatedReview = repository.save(review);
                notificationService.sendReviewApprovedNotification(updatedReview);
                logger.info("Review updated successfully with id={}", updatedReview.getId());
                return updatedReview;
            } catch (PersistenceException e) {
                logger.error("Error updating review", e);
                throw new RuntimeException("Error updating review: " + e.getMessage(), e);
            }
        }
        throw new RuntimeException("Review tidak valid untuk diperbarui");
    }

    @Override
    @Timed(value = "review.deleteReview.time", description = "Time taken to delete review")
    public void deleteReview(UUID id) {
        logger.info("Deleting review with id={}", id);
        try {
            repository.deleteById(id);
            logger.info("Review deleted with id={}", id);
        } catch (PersistenceException e) {
            logger.error("Error deleting review", e);
            throw new RuntimeException("Error deleting review: " + e.getMessage(), e);
        }
    }

    @Override
    @Timed(value = "review.approveReview.time", description = "Time taken to approve review")
    public ReviewModel approveReview(UUID reviewId) {
        logger.info("Approving review with id={}", reviewId);
        ReviewModel review = repository.findById(reviewId);
        if (review == null) {
            logger.warn("Review not found with id={}", reviewId);
            throw new RuntimeException("Review tidak ditemukan dengan id: " + reviewId);
        }

        if (!validateReview(review)) {
            logger.warn("Review tidak valid dan tidak bisa di-approve, id={}", reviewId);
            throw new RuntimeException("Review tidak valid dan tidak bisa di-approve.");
        }

        try {
            ReviewModel updatedReview = repository.updateStatus(reviewId, ReviewStatus.APPROVED);
            notificationService.sendReviewApprovedNotification(updatedReview);
            logger.info("Review approved with id={}", reviewId);
            return updatedReview;
        } catch (PersistenceException e) {
            logger.error("Gagal approve review", e);
            throw new RuntimeException("Gagal approve review: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateReview(ReviewModel review) {
        boolean valid = review.getRating() != null
                && review.getRating() >= 1
                && review.getRating() <= 5
                && review.getComment() != null
                && !review.getComment().trim().isEmpty();
        logger.debug("Validasi review id={} result={}", review.getId(), valid);
        return valid;
    }

    @Override
    @Timed(value = "review.calculateEventAverageRating.time", description = "Time taken to calculate average rating")
    public Double calculateEventAverageRating(UUID eventId) {
        logger.info("Calculating average rating for eventId={}", eventId);
        try {
            List<ReviewModel> reviews = repository.findAllByEventIdAndStatus(eventId, ReviewStatus.APPROVED);
            if (reviews.isEmpty()) {
                logger.info("No approved reviews found for eventId={}", eventId);
                return 0.0;
            }
            double average = reviews.stream()
                    .mapToInt(ReviewModel::getRating)
                    .average()
                    .orElse(0.0);
            logger.info("Average rating for eventId={} is {}", eventId, average);
            return average;
        } catch (PersistenceException e) {
            logger.error("Error saat menghitung rata-rata rating", e);
            throw new RuntimeException("Error saat menghitung rata-rata rating: " + e.getMessage(), e);
        }
    }

    @Override
    @Timed(value = "review.getReviewsByEventId.time", description = "Time taken to get reviews by event id")
    public List<ReviewModel> getReviewsByEventId(UUID eventId) {
        logger.info("Getting reviews for eventId={}", eventId);
        try {
            return repository.findAllByEventId(eventId);
        } catch (PersistenceException e) {
            logger.error("Error saat mengambil reviews", e);
            throw new RuntimeException("Error saat mengambil reviews: " + e.getMessage(), e);
        }
    }

    @Override
    @Timed(value = "review.getReviewsForOrganizer.time", description = "Time taken to get reviews for organizer")
    public List<ReviewModel> getReviewsForOrganizer(UUID eventId, UUID organizerId) {
        logger.info("Getting reviews for eventId={} and organizerId={}", eventId, organizerId);
        try {
            List<ReviewModel> reviews = repository.findAllByEventIdAndOrganizerId(eventId, organizerId);
            if (reviews.isEmpty()) {
                logger.warn("Tidak ada review untuk event yang dikelola oleh organizer ini, eventId={}, organizerId={}", eventId, organizerId);
                throw new RuntimeException("Tidak ada review untuk event yang dikelola oleh organizer ini.");
            }
            return reviews;
        } catch (PersistenceException e) {
            logger.error("Error saat mengambil review untuk organizer", e);
            throw new RuntimeException("Error saat mengambil review untuk organizer: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @Timed(value = "review.flagReview.time", description = "Time taken to flag review")
    public ReviewModel flagReview(UUID reviewId, String role) {
        logger.info("Flagging review id={} by role={}", reviewId, role);
        if (!"Organizer".equalsIgnoreCase(role)) {
            logger.warn("Unauthorized flag attempt by role={}", role);
            throw new SecurityException("Hanya organizer yang dapat melakukan flag review.");
        }

        ReviewModel review = repository.findById(reviewId);
        if (review == null) {
            logger.warn("Review not found with id={}", reviewId);
            throw new IllegalArgumentException("Review tidak ditemukan dengan id " + reviewId);
        }

        if (review.getStatus() != ReviewStatus.APPROVED) {
            logger.warn("Review status bukan APPROVED sehingga tidak bisa di-flag, id={}", reviewId);
            throw new IllegalStateException("Review hanya bisa di-flag jika statusnya APPROVED.");
        }

        ReviewModel flaggedReview = repository.updateStatus(reviewId, ReviewStatus.FLAGGED);
        logger.info("Review flagged successfully, id={}", reviewId);
        return flaggedReview;
    }

    @Override
    @Timed(value = "review.getReviewsByStatus.time", description = "Time taken to get reviews by status")
    public List<ReviewModel> getReviewsByStatus(ReviewStatus status) {
        logger.info("Getting reviews by status={}", status);
        try {
            return repository.findAllByStatus(status);
        } catch (PersistenceException e) {
            logger.error("Error saat mengambil review berdasarkan status", e);
            throw new RuntimeException("Error saat mengambil review berdasarkan status: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @Timed(value = "review.cancelFlag.time", description = "Time taken to cancel flag on review")
    public ReviewModel cancelFlag(UUID reviewId, String role) {
        logger.info("Canceling flag on review id={} by role={}", reviewId, role);
        if (!"Organizer".equalsIgnoreCase(role)) {
            logger.warn("Unauthorized cancel flag attempt by role={}", role);
            throw new SecurityException("Hanya organizer yang dapat membatalkan flag review.");
        }

        ReviewModel review = repository.findById(reviewId);
        if (review == null) {
            logger.warn("Review not found with id={}", reviewId);
            throw new IllegalArgumentException("Review tidak ditemukan dengan id " + reviewId);
        }

        if (review.getStatus() != ReviewStatus.FLAGGED) {
            logger.warn("Review status bukan FLAGGED sehingga tidak bisa dibatalkan flag-nya, id={}", reviewId);
            throw new IllegalStateException("Review hanya bisa dibatalkan flag-nya jika statusnya FLAGGED.");
        }
        ReviewModel updatedReview = repository.updateStatus(reviewId, ReviewStatus.APPROVED);
        logger.info("Flag canceled successfully for review id={}", reviewId);
        return updatedReview;
    }
}
