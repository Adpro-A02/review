package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository repository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private ReviewModel review1, review2;
    private UUID eventId1, userId1, reviewId1;
    private UUID eventId2, userId2, reviewId2;
    private UUID organizerId;

    @BeforeEach
    void setUp() {
        eventId1 = UUID.randomUUID();
        userId1 = UUID.randomUUID();
        reviewId1 = UUID.randomUUID();
        organizerId = UUID.randomUUID();

        review1 = ReviewModel.builder()
                .id(reviewId1)
                .eventId(eventId1)
                .userId(userId1)
                .organizerId(organizerId)
                .rating(5)
                .comment("Excellent!")
                .createdDate(LocalDateTime.now())
                .status(ReviewStatus.APPROVED)
                .build();

        eventId2 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        reviewId2 = UUID.randomUUID();

        review2 = ReviewModel.builder()
                .id(reviewId2)
                .eventId(eventId1)
                .userId(userId2)
                .organizerId(organizerId)
                .rating(3)
                .comment("Okay.")
                .createdDate(LocalDateTime.now())
                .status(ReviewStatus.APPROVED)
                .build();
    }

    @Test
    void testCreateReview_success() throws ExecutionException, InterruptedException {
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.empty());
        when(repository.save(any(ReviewModel.class))).thenReturn(review1);

        CompletableFuture<ReviewModel> future = reviewService.createReview(review1);
        ReviewModel savedReview = future.get();

        assertNotNull(savedReview);
        assertEquals(review1.getComment(), savedReview.getComment());
        verify(repository).save(review1);
    }

    @Test
    void testCreateReview_alreadyExists_throwsException() {
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.of(review1));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reviewService.createReview(review1);
        });
        assertEquals("User sudah pernah membuat review untuk event ini.", exception.getMessage());
        verify(repository, never()).save(any(ReviewModel.class));
    }

    @Test
    void testUpdateReview_success() {
        when(repository.save(review1)).thenReturn(review1);
        ReviewModel updatedReview = reviewService.updateReview(review1);

        assertNotNull(updatedReview);
        assertEquals(review1.getComment(), updatedReview.getComment());
        verify(repository).save(review1);
        verify(notificationService).sendReviewApprovedNotification(updatedReview);
    }

    @Test
    void testUpdateReview_invalidReview_throwsException() {
        ReviewModel invalidReview = ReviewModel.builder().rating(0).comment("").build();
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(invalidReview);
        });
        assertEquals("Review tidak valid untuk diperbarui", exception.getMessage());
        verify(repository, never()).save(any(ReviewModel.class));
        verify(notificationService, never()).sendReviewApprovedNotification(any(ReviewModel.class));
    }

    @Test
    void testUpdateReview_persistenceException_throwsException() {
        when(repository.save(review1)).thenThrow(new PersistenceException("DB error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(review1);
        });
        assertEquals("Error updating review: DB error", exception.getMessage());
        verify(notificationService, never()).sendReviewApprovedNotification(any(ReviewModel.class));
    }

    @Test
    void testDeleteReview_success() {
        doNothing().when(repository).deleteById(reviewId1);
        assertDoesNotThrow(() -> reviewService.deleteReview(reviewId1));
        verify(repository).deleteById(reviewId1);
    }

    @Test
    void testDeleteReview_persistenceException_throwsException() {
        doThrow(new PersistenceException("DB error")).when(repository).deleteById(reviewId1);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId1);
        });
        assertEquals("Error deleting review: DB error", exception.getMessage());
    }

    @Test
    void testValidateReview_valid() {
        assertTrue(reviewService.validateReview(review1));
    }

    @Test
    void testValidateReview_invalid_nullRating() {
        review1.setRating(null);
        assertFalse(reviewService.validateReview(review1));
    }

    @Test
    void testValidateReview_invalid_ratingTooLow() {
        review1.setRating(0);
        assertFalse(reviewService.validateReview(review1));
    }

    @Test
    void testValidateReview_invalid_ratingTooHigh() {
        review1.setRating(6);
        assertFalse(reviewService.validateReview(review1));
    }

    @Test
    void testValidateReview_invalid_nullComment() {
        review1.setComment(null);
        assertFalse(reviewService.validateReview(review1));
    }

    @Test
    void testValidateReview_invalid_emptyComment() {
        review1.setComment("   ");
        assertFalse(reviewService.validateReview(review1));
    }

    @Test
    void testCalculateEventAverageRating_withReviews() {
        review1.setRating(5);
        review2.setRating(3);
        when(repository.findAllByEventIdAndStatus(eventId1, ReviewStatus.APPROVED)).thenReturn(Arrays.asList(review1, review2));

        Double average = reviewService.calculateEventAverageRating(eventId1);
        assertEquals(4.0, average);
    }

    @Test
    void testCalculateEventAverageRating_noReviews() {
        when(repository.findAllByEventIdAndStatus(eventId1, ReviewStatus.APPROVED)).thenReturn(Collections.emptyList());
        Double average = reviewService.calculateEventAverageRating(eventId1);
        assertEquals(0.0, average);
    }

    @Test
    void testCalculateEventAverageRating_persistenceException() {
        when(repository.findAllByEventIdAndStatus(eventId1, ReviewStatus.APPROVED)).thenThrow(new PersistenceException("DB error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.calculateEventAverageRating(eventId1);
        });
        assertEquals("Error saat menghitung rata-rata rating: DB error", exception.getMessage());
    }

    @Test
    void testGetReviewsByEventId_success() {
        List<ReviewModel> reviews = Arrays.asList(review1, review2);
        when(repository.findAllByEventId(eventId1)).thenReturn(reviews);
        List<ReviewModel> result = reviewService.getReviewsByEventId(eventId1);
        assertEquals(reviews, result);
    }

    @Test
    void testGetReviewsByEventId_persistenceException() {
        when(repository.findAllByEventId(eventId1)).thenThrow(new PersistenceException("DB error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewsByEventId(eventId1);
        });
        assertEquals("Error saat mengambil reviews: DB error", exception.getMessage());
    }

    @Test
    void testGetReviewsForOrganizer_success() {
        List<ReviewModel> reviews = Collections.singletonList(review1);
        when(repository.findAllByEventIdAndOrganizerId(eventId1, organizerId)).thenReturn(reviews);
        List<ReviewModel> result = reviewService.getReviewsForOrganizer(eventId1, organizerId);
        assertEquals(reviews, result);
    }

    @Test
    void testGetReviewsForOrganizer_noReviews_throwsException() {
        when(repository.findAllByEventIdAndOrganizerId(eventId1, organizerId)).thenReturn(Collections.emptyList());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewsForOrganizer(eventId1, organizerId);
        });
        assertEquals("Tidak ada review untuk event yang dikelola oleh organizer ini.", exception.getMessage());
    }

    @Test
    void testGetReviewsForOrganizer_persistenceException() {
        when(repository.findAllByEventIdAndOrganizerId(eventId1, organizerId)).thenThrow(new PersistenceException("DB error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewsForOrganizer(eventId1, organizerId);
        });
        assertEquals("Error saat mengambil review untuk organizer: DB error", exception.getMessage());
    }

    @Test
    void testFlagReview_success() {

        review1.setStatus(ReviewStatus.APPROVED);
        when(repository.findById(reviewId1)).thenReturn(review1);

        ReviewModel flaggedReviewState = ReviewModel.builder()
                .id(review1.getId())
                .eventId(review1.getEventId())
                .userId(review1.getUserId())
                .organizerId(review1.getOrganizerId())
                .rating(review1.getRating())
                .comment(review1.getComment())
                .createdDate(review1.getCreatedDate())
                .status(ReviewStatus.FLAGGED)
                .build();

        when(repository.updateStatus(reviewId1, ReviewStatus.FLAGGED)).thenReturn(flaggedReviewState);

        ReviewModel flaggedReview = reviewService.flagReview(reviewId1, "Organizer");

        assertNotNull(flaggedReview);

        assertEquals(ReviewStatus.FLAGGED, flaggedReview.getStatus());
        verify(repository).updateStatus(reviewId1, ReviewStatus.FLAGGED);
    }

    @Test
    void testFlagReview_wrongRole_throwsSecurityException() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            reviewService.flagReview(reviewId1, "Attendee");
        });
        assertEquals("Hanya organizer yang dapat melakukan flag review.", exception.getMessage());
    }

    @Test
    void testFlagReview_notFound_throwsIllegalArgumentException() {
        when(repository.findById(reviewId1)).thenReturn(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.flagReview(reviewId1, "Organizer");
        });
        assertEquals("Review tidak ditemukan dengan id " + reviewId1, exception.getMessage());
    }

    @Test
    void testFlagReview_notApprovedStatus_throwsIllegalStateException() {
        review1.setStatus(ReviewStatus.FLAGGED);
        when(repository.findById(reviewId1)).thenReturn(review1);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reviewService.flagReview(reviewId1, "Organizer");
        });
        assertEquals("Review hanya bisa di-flag jika statusnya APPROVED.", exception.getMessage());
    }

    @Test
    void testGetReviewsByStatus_success() {
        List<ReviewModel> reviews = Collections.singletonList(review1);
        review1.setStatus(ReviewStatus.FLAGGED);
        when(repository.findAllByStatus(ReviewStatus.FLAGGED)).thenReturn(reviews);
        List<ReviewModel> result = reviewService.getReviewsByStatus(ReviewStatus.FLAGGED);
        assertEquals(reviews, result);
    }

    @Test
    void testGetReviewsByStatus_persistenceException() {
        when(repository.findAllByStatus(ReviewStatus.FLAGGED)).thenThrow(new PersistenceException("DB error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewsByStatus(ReviewStatus.FLAGGED);
        });
        assertEquals("Error saat mengambil review berdasarkan status: DB error", exception.getMessage());
    }

    @Test
    void testCancelFlag_success() {

        review1.setStatus(ReviewStatus.FLAGGED);
        when(repository.findById(reviewId1)).thenReturn(review1);

        ReviewModel approvedReviewState = ReviewModel.builder()
                .id(review1.getId())
                .eventId(review1.getEventId())
                .userId(review1.getUserId())
                .organizerId(review1.getOrganizerId())
                .rating(review1.getRating())
                .comment(review1.getComment())
                .createdDate(review1.getCreatedDate())
                .status(ReviewStatus.APPROVED)
                .build();

        when(repository.updateStatus(reviewId1, ReviewStatus.APPROVED)).thenReturn(approvedReviewState);

        ReviewModel unflaggedReview = reviewService.cancelFlag(reviewId1, "Organizer");

        assertNotNull(unflaggedReview);

        assertEquals(ReviewStatus.APPROVED, unflaggedReview.getStatus());
        verify(repository).updateStatus(reviewId1, ReviewStatus.APPROVED);
    }

    @Test
    void testCancelFlag_wrongRole_throwsSecurityException() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            reviewService.cancelFlag(reviewId1, "Attendee");
        });
        assertEquals("Hanya organizer yang dapat membatalkan flag review.", exception.getMessage());
    }

    @Test
    void testCancelFlag_notFound_throwsIllegalArgumentException() {
        when(repository.findById(reviewId1)).thenReturn(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewService.cancelFlag(reviewId1, "Organizer");
        });
        assertEquals("Review tidak ditemukan dengan id " + reviewId1, exception.getMessage());
    }

    @Test
    void testCancelFlag_notFlaggedStatus_throwsIllegalStateException() {
        review1.setStatus(ReviewStatus.APPROVED);
        when(repository.findById(reviewId1)).thenReturn(review1);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reviewService.cancelFlag(reviewId1, "Organizer");
        });
        assertEquals("Review hanya bisa dibatalkan flag-nya jika statusnya FLAGGED.", exception.getMessage());
    }

    @Test
    void testGetReviewsByUserId_success() {
        List<ReviewModel> reviews = Collections.singletonList(review1);
        when(repository.findAllByUserId(userId1)).thenReturn(reviews);
        List<ReviewModel> result = reviewService.getReviewsByUserId(userId1);
        assertEquals(reviews, result);
    }

    @Test
    void testGetReviewsByUserId_persistenceException() {
        when(repository.findAllByUserId(userId1)).thenThrow(new PersistenceException("DB error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewsByUserId(userId1);
        });
        assertEquals("Error saat mengambil reviews untuk user: DB error", exception.getMessage());
    }

    @Test
    void testGetReviewByUserIdAndEventId_success() {
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.of(review1));
        ReviewModel result = reviewService.getReviewByUserIdAndEventId(userId1, eventId1);
        assertEquals(review1, result);
    }

    @Test
    void testGetReviewByUserIdAndEventId_notFound_throwsException() {
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewByUserIdAndEventId(userId1, eventId1);
        });
        assertEquals("Review tidak ditemukan untuk user " + userId1 + " dan event " + eventId1, exception.getMessage());
    }

    @Test
    void testGetReviewByUserIdAndEventId_persistenceException() {
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenThrow(new PersistenceException("DB error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewByUserIdAndEventId(userId1, eventId1);
        });
        assertEquals("Error saat mengambil review: DB error", exception.getMessage());
    }

    @Test
    void testUpdateReviewByUserIdAndEventId_success() {
        ReviewModel updateData = ReviewModel.builder().rating(4).comment("Updated comment").build();
        ReviewModel existingReview = ReviewModel.builder()
                .id(reviewId1).userId(userId1).eventId(eventId1).rating(3).comment("Old").status(ReviewStatus.APPROVED).build();

        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.of(existingReview));

        when(repository.save(any(ReviewModel.class))).thenAnswer(invocation -> {
            ReviewModel saved = invocation.getArgument(0);

            saved.setId(existingReview.getId());
            saved.setUserId(existingReview.getUserId());
            saved.setEventId(existingReview.getEventId());
            saved.setStatus(existingReview.getStatus());
            return saved;
        });

        ReviewModel updatedReview = reviewService.updateReviewByUserIdAndEventId(userId1, eventId1, updateData);

        assertNotNull(updatedReview);
        assertEquals(updateData.getRating(), updatedReview.getRating());
        assertEquals(updateData.getComment(), updatedReview.getComment());
        verify(repository).save(any(ReviewModel.class));
        verify(notificationService).sendReviewApprovedNotification(updatedReview);
    }

    @Test
    void testUpdateReviewByUserIdAndEventId_partialUpdate_onlyRating() {
        ReviewModel updateData = ReviewModel.builder().rating(4).comment(null).build();
        ReviewModel existingReview = ReviewModel.builder()
                .id(reviewId1).userId(userId1).eventId(eventId1).rating(3).comment("Original Comment").status(ReviewStatus.APPROVED).build();

        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.of(existingReview));
        when(repository.save(any(ReviewModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewModel updatedReview = reviewService.updateReviewByUserIdAndEventId(userId1, eventId1, updateData);

        assertEquals(4, updatedReview.getRating());
        assertEquals("Original Comment", updatedReview.getComment());
        verify(repository).save(existingReview);
    }

    @Test
    void testUpdateReviewByUserIdAndEventId_partialUpdate_onlyComment() {
        ReviewModel updateData = ReviewModel.builder().rating(null).comment("New Comment").build();
        ReviewModel existingReview = ReviewModel.builder()
                .id(reviewId1).userId(userId1).eventId(eventId1).rating(3).comment("Old Comment").status(ReviewStatus.APPROVED).build();

        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.of(existingReview));
        when(repository.save(any(ReviewModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewModel updatedReview = reviewService.updateReviewByUserIdAndEventId(userId1, eventId1, updateData);

        assertEquals(3, updatedReview.getRating());
        assertEquals("New Comment", updatedReview.getComment());
        verify(repository).save(existingReview);
    }

    @Test
    void testUpdateReviewByUserIdAndEventId_notFound_throwsException() {
        ReviewModel updateData = ReviewModel.builder().rating(4).comment("Updated").build();
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReviewByUserIdAndEventId(userId1, eventId1, updateData);
        });
        assertEquals("Review tidak ditemukan untuk user " + userId1 + " dan event " + eventId1, exception.getMessage());
    }

    @Test
    void testUpdateReviewByUserIdAndEventId_invalidData_throwsException() {
        ReviewModel updateData = ReviewModel.builder().rating(0).comment("").build();
        ReviewModel existingReview = ReviewModel.builder()
                .id(reviewId1).userId(userId1).eventId(eventId1).rating(3).comment("Old").status(ReviewStatus.APPROVED).build();

        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.of(existingReview));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReviewByUserIdAndEventId(userId1, eventId1, updateData);
        });
        assertEquals("Review tidak valid untuk diperbarui", exception.getMessage());
    }

    @Test
    void testUpdateReviewByUserIdAndEventId_persistenceException() {
        ReviewModel updateData = ReviewModel.builder().rating(4).comment("Updated").build();
        ReviewModel existingReview = ReviewModel.builder()
                .id(reviewId1).userId(userId1).eventId(eventId1).rating(3).comment("Old").status(ReviewStatus.APPROVED).build();

        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.of(existingReview));
        when(repository.save(any(ReviewModel.class))).thenThrow(new PersistenceException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReviewByUserIdAndEventId(userId1, eventId1, updateData);
        });
        assertEquals("Error updating review: DB error", exception.getMessage());
        verify(notificationService, never()).sendReviewApprovedNotification(any(ReviewModel.class));
    }

    @Test
    void testDeleteReviewByUserIdAndEventId_success() {
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.of(review1));
        doNothing().when(repository).deleteById(review1.getId());

        assertDoesNotThrow(() -> reviewService.deleteReviewByUserIdAndEventId(userId1, eventId1));
        verify(repository).deleteById(review1.getId());
    }

    @Test
    void testDeleteReviewByUserIdAndEventId_notFound_throwsException() {
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReviewByUserIdAndEventId(userId1, eventId1);
        });
        assertEquals("Review tidak ditemukan untuk user " + userId1 + " dan event " + eventId1, exception.getMessage());
    }

    @Test
    void testDeleteReviewByUserIdAndEventId_persistenceExceptionOnFind() {
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenThrow(new PersistenceException("DB find error"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReviewByUserIdAndEventId(userId1, eventId1);
        });

        assertEquals("Error deleting review: DB find error", exception.getMessage());
    }

    @Test
    void testDeleteReviewByUserIdAndEventId_persistenceExceptionOnDelete() {
        when(repository.findByUserIdAndEventId(userId1, eventId1)).thenReturn(Optional.of(review1));
        doThrow(new PersistenceException("DB delete error")).when(repository).deleteById(review1.getId());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReviewByUserIdAndEventId(userId1, eventId1);
        });
        assertEquals("Error deleting review: DB delete error", exception.getMessage());
    }
}