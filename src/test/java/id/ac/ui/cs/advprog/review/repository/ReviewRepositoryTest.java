//package id.ac.ui.cs.advprog.review.repository;
//
//import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
//import id.ac.ui.cs.advprog.review.model.ReviewModel;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.Query;
//import jakarta.persistence.TypedQuery;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReviewRepositoryTest {
//
//    @Mock
//    private EntityManager entityManager;
//
//    @Mock
//    private TypedQuery<ReviewModel> typedQuery;
//
//    @Mock
//    private Query query;
//
//    @InjectMocks
//    private ReviewRepository reviewRepository;
//
//    private ReviewModel reviewModel;
//    private UUID reviewId;
//    private UUID eventId;
//    private UUID organizerId;
//    private UUID userId;
//
//    @BeforeEach
//    void setUp() {
//        reviewId = UUID.randomUUID();
//        eventId = UUID.randomUUID();
//        organizerId = UUID.randomUUID();
//        userId = UUID.randomUUID();
//
//        reviewModel = new ReviewModel();
//        reviewModel.setId(reviewId);
//        reviewModel.setEventId(eventId);
//        reviewModel.setOrganizerId(organizerId);
//        reviewModel.setUserId(userId);
//        reviewModel.setStatus(ReviewStatus.FLAGGED);
//    }
//
//    @Test
//    void testSave_WhenIdIsNull_ShouldPersistAndReturnReview() {
//
//        ReviewModel newReview = new ReviewModel();
//        newReview.setId(null);
//
//        ReviewModel result = reviewRepository.save(newReview);
//
//        verify(entityManager).persist(newReview);
//        verify(entityManager, never()).merge(any());
//        assertEquals(newReview, result);
//    }
//
//    @Test
//    void testSave_WhenIdExists_ShouldMergeAndReturnReview() {
//
//        ReviewModel existingReview = new ReviewModel();
//        existingReview.setId(reviewId);
//        ReviewModel mergedReview = new ReviewModel();
//
//        when(entityManager.merge(existingReview)).thenReturn(mergedReview);
//
//        ReviewModel result = reviewRepository.save(existingReview);
//
//        verify(entityManager).merge(existingReview);
//        verify(entityManager, never()).persist(any());
//        assertEquals(mergedReview, result);
//    }
//
//    @Test
//    void testUpdateStatus_WhenReviewExists_ShouldUpdateAndReturnReview() {
//
//        ReviewStatus newStatus = ReviewStatus.APPROVED;
//        when(entityManager.createQuery(anyString())).thenReturn(query);
//        when(query.setParameter(eq("status"), eq(newStatus))).thenReturn(query);
//        when(query.setParameter(eq("id"), eq(reviewId))).thenReturn(query);
//        when(query.executeUpdate()).thenReturn(1);
//        when(entityManager.find(ReviewModel.class, reviewId)).thenReturn(reviewModel);
//
//        ReviewModel result = reviewRepository.updateStatus(reviewId, newStatus);
//
//        verify(entityManager).createQuery("UPDATE ReviewModel r SET r.status = :status WHERE r.id = :id");
//        verify(query).setParameter("status", newStatus);
//        verify(query).setParameter("id", reviewId);
//        verify(query).executeUpdate();
//        verify(entityManager).find(ReviewModel.class, reviewId);
//        assertEquals(reviewModel, result);
//    }
//
//    @Test
//    void testUpdateStatus_WhenReviewNotFound_ShouldThrowException() {
//
//        ReviewStatus newStatus = ReviewStatus.APPROVED;
//        when(entityManager.createQuery(anyString())).thenReturn(query);
//        when(query.setParameter(eq("status"), eq(newStatus))).thenReturn(query);
//        when(query.setParameter(eq("id"), eq(reviewId))).thenReturn(query);
//        when(query.executeUpdate()).thenReturn(0);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            reviewRepository.updateStatus(reviewId, newStatus);
//        });
//
//        assertEquals("Review tidak ditemukan dengan id " + reviewId, exception.getMessage());
//        verify(entityManager, never()).find(any(), any());
//    }
//
//    @Test
//    void testDeleteById_WhenReviewExists_ShouldRemoveReview() {
//
//        when(entityManager.find(ReviewModel.class, reviewId)).thenReturn(reviewModel);
//
//        reviewRepository.deleteById(reviewId);
//
//        verify(entityManager).find(ReviewModel.class, reviewId);
//        verify(entityManager).remove(reviewModel);
//    }
//
//    @Test
//    void testDeleteById_WhenReviewNotExists_ShouldNotRemove() {
//
//        when(entityManager.find(ReviewModel.class, reviewId)).thenReturn(null);
//
//        reviewRepository.deleteById(reviewId);
//
//        verify(entityManager).find(ReviewModel.class, reviewId);
//        verify(entityManager, never()).remove(any());
//    }
//
//    @Test
//    void testFindById_ShouldReturnReview() {
//
//        when(entityManager.find(ReviewModel.class, reviewId)).thenReturn(reviewModel);
//
//        ReviewModel result = reviewRepository.findById(reviewId);
//
//        verify(entityManager).find(ReviewModel.class, reviewId);
//        assertEquals(reviewModel, result);
//    }
//
//    @Test
//    void testFindAll_ShouldReturnAllReviews() {
//
//        List<ReviewModel> expectedReviews = Arrays.asList(reviewModel, new ReviewModel());
//        when(entityManager.createQuery("SELECT r FROM ReviewModel r", ReviewModel.class))
//                .thenReturn(typedQuery);
//        when(typedQuery.getResultList()).thenReturn(expectedReviews);
//
//        List<ReviewModel> result = reviewRepository.findAll();
//
//        verify(entityManager).createQuery("SELECT r FROM ReviewModel r", ReviewModel.class);
//        verify(typedQuery).getResultList();
//        assertEquals(expectedReviews, result);
//    }
//
//    @Test
//    void testFindAllByEventId_ShouldReturnReviewsForEvent() {
//
//        List<ReviewModel> expectedReviews = Arrays.asList(reviewModel);
//        when(entityManager.createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId", ReviewModel.class))
//                .thenReturn(typedQuery);
//        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
//        when(typedQuery.getResultList()).thenReturn(expectedReviews);
//
//        List<ReviewModel> result = reviewRepository.findAllByEventId(eventId);
//
//        verify(entityManager).createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId", ReviewModel.class);
//        verify(typedQuery).setParameter("eventId", eventId);
//        verify(typedQuery).getResultList();
//        assertEquals(expectedReviews, result);
//    }
//
//    @Test
//    void testFindAllByEventIdAndOrganizerId_ShouldReturnFilteredReviews() {
//
//        List<ReviewModel> expectedReviews = Arrays.asList(reviewModel);
//        when(entityManager.createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId AND r.organizerId = :organizerId",
//                ReviewModel.class))
//                .thenReturn(typedQuery);
//        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
//        when(typedQuery.setParameter("organizerId", organizerId)).thenReturn(typedQuery);
//        when(typedQuery.getResultList()).thenReturn(expectedReviews);
//
//        List<ReviewModel> result = reviewRepository.findAllByEventIdAndOrganizerId(eventId, organizerId);
//
//        verify(entityManager).createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId AND r.organizerId = :organizerId",
//                ReviewModel.class);
//        verify(typedQuery).setParameter("eventId", eventId);
//        verify(typedQuery).setParameter("organizerId", organizerId);
//        verify(typedQuery).getResultList();
//        assertEquals(expectedReviews, result);
//    }
//
//    @Test
//    void testFindAllByStatus_ShouldReturnReviewsWithStatus() {
//
//        ReviewStatus status = ReviewStatus.APPROVED;
//        List<ReviewModel> expectedReviews = Arrays.asList(reviewModel);
//        when(entityManager.createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.status = :status", ReviewModel.class))
//                .thenReturn(typedQuery);
//        when(typedQuery.setParameter("status", status)).thenReturn(typedQuery);
//        when(typedQuery.getResultList()).thenReturn(expectedReviews);
//
//        List<ReviewModel> result = reviewRepository.findAllByStatus(status);
//
//        verify(entityManager).createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.status = :status", ReviewModel.class);
//        verify(typedQuery).setParameter("status", status);
//        verify(typedQuery).getResultList();
//        assertEquals(expectedReviews, result);
//    }
//
//    @Test
//    void testFindAllByEventIdAndStatus_ShouldReturnFilteredReviews() {
//
//        ReviewStatus status = ReviewStatus.APPROVED;
//        List<ReviewModel> expectedReviews = Arrays.asList(reviewModel);
//        when(entityManager.createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId AND r.status = :status",
//                ReviewModel.class))
//                .thenReturn(typedQuery);
//        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
//        when(typedQuery.setParameter("status", status)).thenReturn(typedQuery);
//        when(typedQuery.getResultList()).thenReturn(expectedReviews);
//
//        List<ReviewModel> result = reviewRepository.findAllByEventIdAndStatus(eventId, status);
//
//        verify(entityManager).createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId AND r.status = :status",
//                ReviewModel.class);
//        verify(typedQuery).setParameter("eventId", eventId);
//        verify(typedQuery).setParameter("status", status);
//        verify(typedQuery).getResultList();
//        assertEquals(expectedReviews, result);
//    }
//
//    @Test
//    void testFindByUserIdAndEventId_WhenReviewExists_ShouldReturnOptionalWithReview() {
//
//        List<ReviewModel> results = Arrays.asList(reviewModel);
//        when(entityManager.createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.userId = :userId AND r.eventId = :eventId",
//                ReviewModel.class))
//                .thenReturn(typedQuery);
//        when(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery);
//        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
//        when(typedQuery.getResultList()).thenReturn(results);
//
//        Optional<ReviewModel> result = reviewRepository.findByUserIdAndEventId(userId, eventId);
//
//        verify(entityManager).createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.userId = :userId AND r.eventId = :eventId",
//                ReviewModel.class);
//        verify(typedQuery).setParameter("userId", userId);
//        verify(typedQuery).setParameter("eventId", eventId);
//        verify(typedQuery).getResultList();
//        assertTrue(result.isPresent());
//        assertEquals(reviewModel, result.get());
//    }
//
//    @Test
//    void testFindByUserIdAndEventId_WhenReviewNotExists_ShouldReturnEmptyOptional() {
//
//        List<ReviewModel> results = Arrays.asList();
//        when(entityManager.createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.userId = :userId AND r.eventId = :eventId",
//                ReviewModel.class))
//                .thenReturn(typedQuery);
//        when(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery);
//        when(typedQuery.setParameter("eventId", eventId)).thenReturn(typedQuery);
//        when(typedQuery.getResultList()).thenReturn(results);
//
//        Optional<ReviewModel> result = reviewRepository.findByUserIdAndEventId(userId, eventId);
//
//        verify(entityManager).createQuery(
//                "SELECT r FROM ReviewModel r WHERE r.userId = :userId AND r.eventId = :eventId",
//                ReviewModel.class);
//        verify(typedQuery).setParameter("userId", userId);
//        verify(typedQuery).setParameter("eventId", eventId);
//        verify(typedQuery).getResultList();
//        assertTrue(result.isEmpty());
//    }
//}