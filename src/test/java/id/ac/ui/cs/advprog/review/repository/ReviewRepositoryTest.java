package id.ac.ui.cs.advprog.review.repository;

import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewRepositoryTest {

    @InjectMocks
    private ReviewRepository reviewRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<ReviewModel> typedQuery;

    @Mock
    private jakarta.persistence.Query mockQuery; // For updateStatus which uses a generic Query

    private ReviewModel review;
    private UUID reviewId;
    private UUID eventId;
    private UUID userId;
    private UUID organizerId;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
        organizerId = UUID.randomUUID();

        review = ReviewModel.builder()
                .id(reviewId)
                .eventId(eventId)
                .userId(userId)
                .organizerId(organizerId)
                .rating(5)
                .comment("Great event!")
                .status(ReviewStatus.APPROVED)
                .build();
    }

    @Test
    void testSave_newReview() {
        ReviewModel newReview = ReviewModel.builder().eventId(eventId).userId(userId).build(); // No ID
        ReviewModel savedReview = reviewRepository.save(newReview);
        verify(entityManager).persist(newReview);
        assertEquals(newReview, savedReview);
    }

    @Test
    void testSave_updateReview() {
        when(entityManager.merge(review)).thenReturn(review);
        ReviewModel updatedReview = reviewRepository.save(review);
        verify(entityManager).merge(review);
        assertEquals(review, updatedReview);
    }

    @Test
    void testUpdateStatus_success() {
        when(entityManager.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("status"), any(ReviewStatus.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("id"), any(UUID.class))).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(1); // 1 row updated
        when(entityManager.find(ReviewModel.class, reviewId)).thenReturn(review);
        review.setStatus(ReviewStatus.FLAGGED); // Expected state after update

        ReviewModel updatedReview = reviewRepository.updateStatus(reviewId, ReviewStatus.FLAGGED);

        verify(entityManager).createQuery("UPDATE ReviewModel r SET r.status = :status WHERE r.id = :id");
        verify(mockQuery).setParameter("status", ReviewStatus.FLAGGED);
        verify(mockQuery).setParameter("id", reviewId);
        verify(mockQuery).executeUpdate();
        verify(entityManager).find(ReviewModel.class, reviewId);
        assertEquals(ReviewStatus.FLAGGED, updatedReview.getStatus());
    }

    @Test
    void testUpdateStatus_reviewNotFound_throwsException() {
        when(entityManager.createQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(0); // 0 rows updated

        assertThrows(IllegalArgumentException.class, () -> {
            reviewRepository.updateStatus(reviewId, ReviewStatus.FLAGGED);
        });
        verify(entityManager).createQuery("UPDATE ReviewModel r SET r.status = :status WHERE r.id = :id");
    }

    @Test
    void testDeleteById_reviewExists() {
        when(entityManager.find(ReviewModel.class, reviewId)).thenReturn(review);
        reviewRepository.deleteById(reviewId);
        verify(entityManager).remove(review);
    }

    @Test
    void testDeleteById_reviewNotExists() {
        when(entityManager.find(ReviewModel.class, reviewId)).thenReturn(null);
        reviewRepository.deleteById(reviewId);
        verify(entityManager, never()).remove(any(ReviewModel.class));
    }

    @Test
    void testFindById_exists() {
        when(entityManager.find(ReviewModel.class, reviewId)).thenReturn(review);
        ReviewModel found = reviewRepository.findById(reviewId);
        assertEquals(review, found);
    }

    @Test
    void testFindById_notExists() {
        when(entityManager.find(ReviewModel.class, reviewId)).thenReturn(null);
        ReviewModel found = reviewRepository.findById(reviewId);
        assertNull(found);
    }

    @Test
    void testFindAll() {
        List<ReviewModel> reviews = Collections.singletonList(review);
        when(entityManager.createQuery("SELECT r FROM ReviewModel r", ReviewModel.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reviews);

        List<ReviewModel> result = reviewRepository.findAll();
        assertEquals(reviews, result);
    }

    @Test
    void testFindAll_empty() {
        when(entityManager.createQuery("SELECT r FROM ReviewModel r", ReviewModel.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        List<ReviewModel> result = reviewRepository.findAll();
        assertTrue(result.isEmpty());
    }


    @Test
    void testFindAllByEventId() {
        List<ReviewModel> reviews = Collections.singletonList(review);
        when(entityManager.createQuery("SELECT r FROM ReviewModel r WHERE r.eventId = :eventId", ReviewModel.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reviews);

        List<ReviewModel> result = reviewRepository.findAllByEventId(eventId);
        assertEquals(reviews, result);
    }

    @Test
    void testFindAllByEventIdAndOrganizerId() {
        List<ReviewModel> reviews = Collections.singletonList(review);
        when(entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId AND r.organizerId = :organizerId",
                ReviewModel.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("organizerId", organizerId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reviews);

        List<ReviewModel> result = reviewRepository.findAllByEventIdAndOrganizerId(eventId, organizerId);
        assertEquals(reviews, result);
    }

    @Test
    void testFindAllByStatus() {
        List<ReviewModel> reviews = Collections.singletonList(review);
        when(entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.status = :status", ReviewModel.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("status", ReviewStatus.APPROVED)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reviews);

        List<ReviewModel> result = reviewRepository.findAllByStatus(ReviewStatus.APPROVED);
        assertEquals(reviews, result);
    }

    @Test
    void testFindAllByEventIdAndStatus() {
        List<ReviewModel> reviews = Collections.singletonList(review);
        when(entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId AND r.status = :status",
                ReviewModel.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("status", ReviewStatus.APPROVED)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reviews);

        List<ReviewModel> result = reviewRepository.findAllByEventIdAndStatus(eventId, ReviewStatus.APPROVED);
        assertEquals(reviews, result);
    }

    @Test
    void testFindByUserIdAndEventId_found() {
        List<ReviewModel> reviews = Collections.singletonList(review);
        when(entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.userId = :userId AND r.eventId = :eventId",
                ReviewModel.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reviews);

        Optional<ReviewModel> result = reviewRepository.findByUserIdAndEventId(userId, eventId);
        assertTrue(result.isPresent());
        assertEquals(review, result.get());
    }

    @Test
    void testFindByUserIdAndEventId_notFound() {
        when(entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.userId = :userId AND r.eventId = :eventId",
                ReviewModel.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery);
        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        Optional<ReviewModel> result = reviewRepository.findByUserIdAndEventId(userId, eventId);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllByUserId() {
        List<ReviewModel> reviews = Collections.singletonList(review);
        when(entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.userId = :userId", ReviewModel.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(reviews);

        List<ReviewModel> result = reviewRepository.findAllByUserId(userId);
        assertEquals(reviews, result);
    }
}