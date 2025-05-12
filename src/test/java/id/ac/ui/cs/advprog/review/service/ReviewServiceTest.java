package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewModel review;
    private UUID reviewId;
    private UUID eventId;
    private UUID userId;

    @BeforeEach
    public void setUp() {
        reviewId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();

        review = ReviewModel.builder()
                .id(reviewId)
                .eventId(eventId)
                .userId(userId)
                .rating(5)
                .comment("Great event!")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .status(ReviewStatus.APPROVED)
                .build();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateReview() {
        when(reviewRepository.save(review)).thenReturn(review);

        ReviewModel createdReview = reviewService.createReview(review);

        assertNotNull(createdReview);
        assertEquals(reviewId, createdReview.getId());
        verify(notificationService, times(1)).sendReviewCreatedNotification(createdReview);
    }

    @Test
    public void testCreateReviewInvalid() {
        review.setRating(6);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(review);
        });

        assertEquals("Review tidak valid", exception.getMessage());
        verify(reviewRepository, never()).save(review);
        verify(notificationService, never()).sendReviewCreatedNotification(any());
    }

    @Test
    public void testUpdateReview() {
        when(reviewRepository.save(review)).thenReturn(review);

        ReviewModel updatedReview = reviewService.updateReview(review);

        assertNotNull(updatedReview);
        assertEquals(reviewId, updatedReview.getId());
        verify(notificationService, times(1)).sendReviewApprovedNotification(updatedReview);
    }

    @Test
    public void testUpdateReviewInvalid() {
        review.setRating(0);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(review);
        });

        assertEquals("Review tidak valid untuk diperbarui", exception.getMessage());
        verify(reviewRepository, never()).save(review);
        verify(notificationService, never()).sendReviewApprovedNotification(any());
    }

    @Test
    public void testDeleteReview() {
        doNothing().when(reviewRepository).deleteById(reviewId);

        reviewService.deleteReview(reviewId);

        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    public void testValidateReviewValid() {
        assertTrue(reviewService.validateReview(review));
    }

    @Test
    public void testValidateReviewInvalidLowRating() {
        review.setRating(0);
        assertFalse(reviewService.validateReview(review));
    }

    @Test
    public void testValidateReviewInvalidHighRating() {
        review.setRating(6);
        assertFalse(reviewService.validateReview(review));
    }

    @Test
    public void testCalculateEventAverageRating() {
        List<ReviewModel> reviews = Arrays.asList(
                ReviewModel.builder().eventId(eventId).rating(5).status(ReviewStatus.APPROVED).build(),
                ReviewModel.builder().eventId(eventId).rating(4).status(ReviewStatus.APPROVED).build()
        );
        when(reviewRepository.findAllByStatus(ReviewStatus.APPROVED)).thenReturn(reviews);

        Double averageRating = reviewService.calculateEventAverageRating(eventId);

        assertNotNull(averageRating);
        assertEquals(4.5, averageRating);
    }

    @Test
    public void testCalculateEventAverageRatingNoReviews() {
        when(reviewRepository.findAllByStatus(ReviewStatus.APPROVED)).thenReturn(Collections.emptyList());

        Double averageRating = reviewService.calculateEventAverageRating(eventId);

        assertNotNull(averageRating);
        assertEquals(0.0, averageRating);
    }

    @Test
    public void testCalculateEventAverageRatingDifferentEventId() {
        UUID differentEventId = UUID.randomUUID();
        List<ReviewModel> reviews = Arrays.asList(
                ReviewModel.builder().eventId(differentEventId).rating(5).status(ReviewStatus.APPROVED).build(),
                ReviewModel.builder().eventId(differentEventId).rating(4).status(ReviewStatus.APPROVED).build()
        );
        when(reviewRepository.findAllByStatus(ReviewStatus.APPROVED)).thenReturn(reviews);

        Double averageRating = reviewService.calculateEventAverageRating(eventId);

        assertNotNull(averageRating);
        assertEquals(0.0, averageRating);
    }
}