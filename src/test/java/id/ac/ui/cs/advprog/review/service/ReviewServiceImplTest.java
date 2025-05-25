//package id.ac.ui.cs.advprog.review.service;
//
//import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
//import id.ac.ui.cs.advprog.review.model.ReviewModel;
//import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
//import jakarta.persistence.PersistenceException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReviewServiceImplTest {
//
//    @Mock
//    private ReviewRepository repository;
//
//    @Mock
//    private NotificationService notificationService;
//
//    @InjectMocks
//    private ReviewServiceImpl reviewService;
//
//    private ReviewModel testReview;
//    private UUID testUserId;
//    private UUID testEventId;
//    private UUID testReviewId;
//    private UUID testOrganizerId;
//
//    @BeforeEach
//    void setUp() {
//        testUserId = UUID.randomUUID();
//        testEventId = UUID.randomUUID();
//        testReviewId = UUID.randomUUID();
//        testOrganizerId = UUID.randomUUID();
//
//        testReview = new ReviewModel();
//        testReview.setId(testReviewId);
//        testReview.setUserId(testUserId);
//        testReview.setEventId(testEventId);
//        testReview.setRating(4);
//        testReview.setComment("Great event!");
//        testReview.setStatus(ReviewStatus.FLAGGED);
//    }
//
//    @Test
//    void createReview_Success() throws ExecutionException, InterruptedException {
//        when(repository.findByUserIdAndEventId(testUserId, testEventId))
//                .thenReturn(Optional.empty());
//        when(repository.save(testReview)).thenReturn(testReview);
//
//        CompletableFuture<ReviewModel> result = reviewService.createReview(testReview);
//
//        assertNotNull(result);
//        assertEquals(testReview, result.get());
//        verify(repository).findByUserIdAndEventId(testUserId, testEventId);
//        verify(repository).save(testReview);
//    }
//
//    @Test
//    void createReview_ThrowsException_WhenUserAlreadyReviewed() {
//        when(repository.findByUserIdAndEventId(testUserId, testEventId))
//                .thenReturn(Optional.of(testReview));
//
//        IllegalStateException exception = assertThrows(IllegalStateException.class,
//                () -> reviewService.createReview(testReview));
//
//        assertEquals("User sudah pernah membuat review untuk event ini.", exception.getMessage());
//        verify(repository).findByUserIdAndEventId(testUserId, testEventId);
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    void updateReview_Success() {
//        when(repository.save(testReview)).thenReturn(testReview);
//        doNothing().when(notificationService).sendReviewApprovedNotification(testReview);
//
//        ReviewModel result = reviewService.updateReview(testReview);
//
//        assertEquals(testReview, result);
//        verify(repository).save(testReview);
//        verify(notificationService).sendReviewApprovedNotification(testReview);
//    }
//
//    @Test
//    void updateReview_ThrowsException_WhenReviewInvalid() {
//        testReview.setRating(null);
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.updateReview(testReview));
//
//        assertEquals("Review tidak valid untuk diperbarui", exception.getMessage());
//        verify(repository, never()).save(any());
//        verify(notificationService, never()).sendReviewApprovedNotification(any());
//    }
//
//    @Test
//    void updateReview_ThrowsException_WhenPersistenceException() {
//        when(repository.save(testReview)).thenThrow(new PersistenceException("DB Error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.updateReview(testReview));
//
//        assertTrue(exception.getMessage().contains("Error updating review"));
//        assertTrue(exception.getCause() instanceof PersistenceException);
//    }
//
//    @Test
//    void deleteReview_Success() {
//        doNothing().when(repository).deleteById(testReviewId);
//
//        assertDoesNotThrow(() -> reviewService.deleteReview(testReviewId));
//
//        verify(repository).deleteById(testReviewId);
//    }
//
//    @Test
//    void deleteReview_ThrowsException_WhenPersistenceException() {
//        doThrow(new PersistenceException("DB Error")).when(repository).deleteById(testReviewId);
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.deleteReview(testReviewId));
//
//        assertTrue(exception.getMessage().contains("Error deleting review"));
//        assertTrue(exception.getCause() instanceof PersistenceException);
//    }
//
//    @Test
//    void approveReview_Success() {
//        testReview.setStatus(ReviewStatus.APPROVED);
//        when(repository.findById(testReviewId)).thenReturn(testReview);
//        when(repository.updateStatus(testReviewId, ReviewStatus.APPROVED)).thenReturn(testReview);
//        doNothing().when(notificationService).sendReviewApprovedNotification(testReview);
//
//        ReviewModel result = reviewService.approveReview(testReviewId);
//
//        assertEquals(testReview, result);
//        verify(repository).findById(testReviewId);
//        verify(repository).updateStatus(testReviewId, ReviewStatus.APPROVED);
//        verify(notificationService).sendReviewApprovedNotification(testReview);
//    }
//
//    @Test
//    void approveReview_ThrowsException_WhenReviewNotFound() {
//        when(repository.findById(testReviewId)).thenReturn(null);
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.approveReview(testReviewId));
//
//        assertEquals("Review tidak ditemukan dengan id: " + testReviewId, exception.getMessage());
//        verify(repository).findById(testReviewId);
//        verify(repository, never()).updateStatus(any(), any());
//    }
//
//    @Test
//    void approveReview_ThrowsException_WhenReviewInvalid() {
//        testReview.setRating(null);
//        when(repository.findById(testReviewId)).thenReturn(testReview);
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.approveReview(testReviewId));
//
//        assertEquals("Review tidak valid dan tidak bisa di-approve.", exception.getMessage());
//        verify(repository, never()).updateStatus(any(), any());
//    }
//
//    @Test
//    void approveReview_ThrowsException_WhenPersistenceException() {
//        when(repository.findById(testReviewId)).thenReturn(testReview);
//        when(repository.updateStatus(testReviewId, ReviewStatus.APPROVED))
//                .thenThrow(new PersistenceException("DB Error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.approveReview(testReviewId));
//
//        assertTrue(exception.getMessage().contains("Gagal approve review"));
//        assertTrue(exception.getCause() instanceof PersistenceException);
//    }
//
//    @Test
//    void validateReview_Success_ValidReview() {
//        boolean result = reviewService.validateReview(testReview);
//        assertTrue(result);
//    }
//
//    @Test
//    void validateReview_False_NullRating() {
//        testReview.setRating(null);
//        boolean result = reviewService.validateReview(testReview);
//        assertFalse(result);
//    }
//
//    @Test
//    void validateReview_False_RatingTooLow() {
//        testReview.setRating(0);
//        boolean result = reviewService.validateReview(testReview);
//        assertFalse(result);
//    }
//
//    @Test
//    void validateReview_False_RatingTooHigh() {
//        testReview.setRating(6);
//        boolean result = reviewService.validateReview(testReview);
//        assertFalse(result);
//    }
//
//    @Test
//    void validateReview_False_NullComment() {
//        testReview.setComment(null);
//        boolean result = reviewService.validateReview(testReview);
//        assertFalse(result);
//    }
//
//    @Test
//    void validateReview_False_EmptyComment() {
//        testReview.setComment("");
//        boolean result = reviewService.validateReview(testReview);
//        assertFalse(result);
//    }
//
//    @Test
//    void validateReview_False_BlankComment() {
//        testReview.setComment("   ");
//        boolean result = reviewService.validateReview(testReview);
//        assertFalse(result);
//    }
//
//    @Test
//    void validateReview_True_MinimumRating() {
//        testReview.setRating(1);
//        boolean result = reviewService.validateReview(testReview);
//        assertTrue(result);
//    }
//
//    @Test
//    void validateReview_True_MaximumRating() {
//        testReview.setRating(5);
//        boolean result = reviewService.validateReview(testReview);
//        assertTrue(result);
//    }
//
//    @Test
//    void calculateEventAverageRating_Success_WithReviews() {
//        ReviewModel review1 = createReviewWithRating(4);
//        ReviewModel review2 = createReviewWithRating(5);
//        ReviewModel review3 = createReviewWithRating(3);
//        List<ReviewModel> reviews = Arrays.asList(review1, review2, review3);
//
//        when(repository.findAllByEventIdAndStatus(testEventId, ReviewStatus.APPROVED))
//                .thenReturn(reviews);
//
//        Double result = reviewService.calculateEventAverageRating(testEventId);
//
//        assertEquals(4.0, result);
//        verify(repository).findAllByEventIdAndStatus(testEventId, ReviewStatus.APPROVED);
//    }
//
//    @Test
//    void calculateEventAverageRating_ReturnsZero_WhenNoReviews() {
//        when(repository.findAllByEventIdAndStatus(testEventId, ReviewStatus.APPROVED))
//                .thenReturn(Collections.emptyList());
//
//        Double result = reviewService.calculateEventAverageRating(testEventId);
//
//        assertEquals(0.0, result);
//    }
//
//    @Test
//    void calculateEventAverageRating_ThrowsException_WhenPersistenceException() {
//        when(repository.findAllByEventIdAndStatus(testEventId, ReviewStatus.APPROVED))
//                .thenThrow(new PersistenceException("DB Error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.calculateEventAverageRating(testEventId));
//
//        assertTrue(exception.getMessage().contains("Error saat menghitung rata-rata rating"));
//        assertTrue(exception.getCause() instanceof PersistenceException);
//    }
//
//    @Test
//    void getReviewsByEventId_Success() {
//        List<ReviewModel> expectedReviews = Arrays.asList(testReview);
//        when(repository.findAllByEventId(testEventId)).thenReturn(expectedReviews);
//
//        List<ReviewModel> result = reviewService.getReviewsByEventId(testEventId);
//
//        assertEquals(expectedReviews, result);
//        verify(repository).findAllByEventId(testEventId);
//    }
//
//    @Test
//    void getReviewsByEventId_ThrowsException_WhenPersistenceException() {
//        when(repository.findAllByEventId(testEventId))
//                .thenThrow(new PersistenceException("DB Error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.getReviewsByEventId(testEventId));
//
//        assertTrue(exception.getMessage().contains("Error saat mengambil reviews"));
//        assertTrue(exception.getCause() instanceof PersistenceException);
//    }
//
//    @Test
//    void getReviewsForOrganizer_Success() {
//        List<ReviewModel> expectedReviews = Arrays.asList(testReview);
//        when(repository.findAllByEventIdAndOrganizerId(testEventId, testOrganizerId))
//                .thenReturn(expectedReviews);
//
//        List<ReviewModel> result = reviewService.getReviewsForOrganizer(testEventId, testOrganizerId);
//
//        assertEquals(expectedReviews, result);
//        verify(repository).findAllByEventIdAndOrganizerId(testEventId, testOrganizerId);
//    }
//
//    @Test
//    void getReviewsForOrganizer_ThrowsException_WhenNoReviews() {
//        when(repository.findAllByEventIdAndOrganizerId(testEventId, testOrganizerId))
//                .thenReturn(Collections.emptyList());
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.getReviewsForOrganizer(testEventId, testOrganizerId));
//
//        assertEquals("Tidak ada review untuk event yang dikelola oleh organizer ini.",
//                exception.getMessage());
//    }
//
//    @Test
//    void getReviewsForOrganizer_ThrowsException_WhenPersistenceException() {
//        when(repository.findAllByEventIdAndOrganizerId(testEventId, testOrganizerId))
//                .thenThrow(new PersistenceException("DB Error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.getReviewsForOrganizer(testEventId, testOrganizerId));
//
//        assertTrue(exception.getMessage().contains("Error saat mengambil review untuk organizer"));
//        assertTrue(exception.getCause() instanceof PersistenceException);
//    }
//
//    @Test
//    void flagReview_Success() {
//        testReview.setStatus(ReviewStatus.APPROVED);
//        when(repository.findById(testReviewId)).thenReturn(testReview);
//        when(repository.updateStatus(testReviewId, ReviewStatus.FLAGGED)).thenReturn(testReview);
//
//        ReviewModel result = reviewService.flagReview(testReviewId, "Organizer");
//
//        assertEquals(testReview, result);
//        verify(repository).findById(testReviewId);
//        verify(repository).updateStatus(testReviewId, ReviewStatus.FLAGGED);
//    }
//
//    @Test
//    void flagReview_ThrowsSecurityException_WhenNotOrganizer() {
//        SecurityException exception = assertThrows(SecurityException.class,
//                () -> reviewService.flagReview(testReviewId, "User"));
//
//        assertEquals("Hanya organizer yang dapat melakukan flag review.", exception.getMessage());
//        verify(repository, never()).findById(any());
//    }
//
//    @Test
//    void flagReview_ThrowsSecurityException_WhenRoleIsNull() {
//        SecurityException exception = assertThrows(SecurityException.class,
//                () -> reviewService.flagReview(testReviewId, null));
//
//        assertEquals("Hanya organizer yang dapat melakukan flag review.", exception.getMessage());
//    }
//
//    @Test
//    void flagReview_ThrowsIllegalArgumentException_WhenReviewNotFound() {
//        when(repository.findById(testReviewId)).thenReturn(null);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> reviewService.flagReview(testReviewId, "Organizer"));
//
//        assertEquals("Review tidak ditemukan dengan id " + testReviewId, exception.getMessage());
//    }
//
//    @Test
//    void flagReview_ThrowsIllegalStateException_WhenReviewNotApproved() {
//        testReview.setStatus(ReviewStatus.FLAGGED);
//        when(repository.findById(testReviewId)).thenReturn(testReview);
//
//        IllegalStateException exception = assertThrows(IllegalStateException.class,
//                () -> reviewService.flagReview(testReviewId, "Organizer"));
//
//        assertEquals("Review hanya bisa di-flag jika statusnya APPROVED.", exception.getMessage());
//        verify(repository, never()).updateStatus(any(), any());
//    }
//
//    @Test
//    void getReviewsByStatus_Success() {
//        List<ReviewModel> expectedReviews = Arrays.asList(testReview);
//        when(repository.findAllByStatus(ReviewStatus.APPROVED)).thenReturn(expectedReviews);
//
//        List<ReviewModel> result = reviewService.getReviewsByStatus(ReviewStatus.APPROVED);
//
//        assertEquals(expectedReviews, result);
//        verify(repository).findAllByStatus(ReviewStatus.APPROVED);
//    }
//
//    @Test
//    void getReviewsByStatus_ThrowsException_WhenPersistenceException() {
//        when(repository.findAllByStatus(ReviewStatus.APPROVED))
//                .thenThrow(new PersistenceException("DB Error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> reviewService.getReviewsByStatus(ReviewStatus.APPROVED));
//
//        assertTrue(exception.getMessage().contains("Error saat mengambil review berdasarkan status"));
//        assertTrue(exception.getCause() instanceof PersistenceException);
//    }
//
//    @Test
//    void cancelFlag_Success() {
//        testReview.setStatus(ReviewStatus.FLAGGED);
//        when(repository.findById(testReviewId)).thenReturn(testReview);
//        when(repository.updateStatus(testReviewId, ReviewStatus.APPROVED)).thenReturn(testReview);
//
//        ReviewModel result = reviewService.cancelFlag(testReviewId, "Organizer");
//
//        assertEquals(testReview, result);
//        verify(repository).findById(testReviewId);
//        verify(repository).updateStatus(testReviewId, ReviewStatus.APPROVED);
//    }
//
//    @Test
//    void cancelFlag_ThrowsSecurityException_WhenNotOrganizer() {
//        SecurityException exception = assertThrows(SecurityException.class,
//                () -> reviewService.cancelFlag(testReviewId, "User"));
//
//        assertEquals("Hanya organizer yang dapat membatalkan flag review.", exception.getMessage());
//        verify(repository, never()).findById(any());
//    }
//
//    @Test
//    void cancelFlag_ThrowsSecurityException_WhenRoleIsNull() {
//        SecurityException exception = assertThrows(SecurityException.class,
//                () -> reviewService.cancelFlag(testReviewId, null));
//
//        assertEquals("Hanya organizer yang dapat membatalkan flag review.", exception.getMessage());
//    }
//
//    @Test
//    void cancelFlag_ThrowsIllegalArgumentException_WhenReviewNotFound() {
//        when(repository.findById(testReviewId)).thenReturn(null);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> reviewService.cancelFlag(testReviewId, "Organizer"));
//
//        assertEquals("Review tidak ditemukan dengan id " + testReviewId, exception.getMessage());
//    }
//
//    @Test
//    void cancelFlag_ThrowsIllegalStateException_WhenReviewNotFlagged() {
//        testReview.setStatus(ReviewStatus.APPROVED);
//        when(repository.findById(testReviewId)).thenReturn(testReview);
//
//        IllegalStateException exception = assertThrows(IllegalStateException.class,
//                () -> reviewService.cancelFlag(testReviewId, "Organizer"));
//
//        assertEquals("Review hanya bisa dibatalkan flag-nya jika statusnya FLAGGED.",
//                exception.getMessage());
//        verify(repository, never()).updateStatus(any(), any());
//    }
//
//    private ReviewModel createReviewWithRating(int rating) {
//        ReviewModel review = new ReviewModel();
//        review.setRating(rating);
//        review.setEventId(testEventId);
//        return review;
//    }
//}