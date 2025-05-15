//package id.ac.ui.cs.advprog.review.service;
//
//import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
//import id.ac.ui.cs.advprog.review.model.ReviewModel;
//import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
//import jakarta.persistence.PersistenceException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ReviewServiceImplTest {
//
//    @Mock
//    private ReviewRepository repository;
//
//    @Mock
//    private NotificationService notificationService;
//
//    @InjectMocks
//    private ReviewServiceImpl service;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    private ReviewModel createValidReview() {
//        return ReviewModel.builder()
//                .id(UUID.randomUUID())
//                .eventId(UUID.randomUUID())
//                .userId(UUID.randomUUID())
//                .rating(4)
//                .comment("Good")
//                .createdDate(LocalDateTime.now())
//                .status(ReviewStatus.REJECTED)
//                .build();
//    }
//
//    @Test
//    void createReview_valid_success() {
//        ReviewModel review = createValidReview();
//        when(repository.save(review)).thenReturn(review);
//
//        ReviewModel result = service.createReview(review);
//
//        verify(repository).save(review);
//        verify(notificationService).sendReviewCreatedNotification(review);
//        assertThat(result).isSameAs(review);
//    }
//
//    @Test
//    void createReview_invalid_throws() {
//        ReviewModel review = createValidReview();
//        review.setRating(0);
//
//        assertThatThrownBy(() -> service.createReview(review))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("tidak valid");
//
//        verifyNoInteractions(repository, notificationService);
//    }
//
//    @Test
//    void createReview_persistenceException_throws() {
//        ReviewModel review = createValidReview();
//        when(repository.save(review)).thenThrow(new PersistenceException("DB error"));
//
//        assertThatThrownBy(() -> service.createReview(review))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Error saving review");
//    }
//
//    @Test
//    void updateReview_valid_success() {
//        ReviewModel review = createValidReview();
//        when(repository.save(review)).thenReturn(review);
//
//        ReviewModel result = service.updateReview(review);
//
//        verify(repository).save(review);
//        verify(notificationService).sendReviewApprovedNotification(review);
//        assertThat(result).isSameAs(review);
//    }
//
//    @Test
//    void updateReview_invalid_throws() {
//        ReviewModel review = createValidReview();
//        review.setRating(10);
//
//        assertThatThrownBy(() -> service.updateReview(review))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("tidak valid untuk diperbarui");
//
//        verifyNoInteractions(repository, notificationService);
//    }
//
//    @Test
//    void updateReview_persistenceException_throws() {
//        ReviewModel review = createValidReview();
//        when(repository.save(review)).thenThrow(new PersistenceException("DB error"));
//
//        assertThatThrownBy(() -> service.updateReview(review))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Error updating review");
//    }
//
//    @Test
//    void deleteReview_success() {
//        UUID id = UUID.randomUUID();
//
//        service.deleteReview(id);
//
//        verify(repository).deleteById(id);
//    }
//
//    @Test
//    void deleteReview_persistenceException_throws() {
//        UUID id = UUID.randomUUID();
//        doThrow(new PersistenceException("DB error")).when(repository).deleteById(id);
//
//        assertThatThrownBy(() -> service.deleteReview(id))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Error deleting review");
//    }
//
//    @Test
//    void approveReview_success() {
//        UUID id = UUID.randomUUID();
//        ReviewModel review = createValidReview();
//        review.setRating(5);
//        review.setStatus(ReviewStatus.REJECTED);
//
//        when(repository.findById(id)).thenReturn(review);
//        when(repository.updateStatus(id, ReviewStatus.APPROVED)).thenReturn(review);
//
//        ReviewModel result = service.approveReview(id);
//
//        verify(repository).findById(id);
//        verify(repository).updateStatus(id, ReviewStatus.APPROVED);
//        verify(notificationService).sendReviewApprovedNotification(review);
//        assertThat(result).isSameAs(review);
//    }
//
//    @Test
//    void approveReview_notFound_throws() {
//        UUID id = UUID.randomUUID();
//        when(repository.findById(id)).thenReturn(null);
//
//        assertThatThrownBy(() -> service.approveReview(id))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("tidak ditemukan");
//    }
//
//    @Test
//    void approveReview_invalid_throws() {
//        UUID id = UUID.randomUUID();
//        ReviewModel review = createValidReview();
//        review.setRating(10);
//
//        when(repository.findById(id)).thenReturn(review);
//
//        assertThatThrownBy(() -> service.approveReview(id))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("tidak valid");
//    }
//
//    @Test
//    void approveReview_persistenceException_throws() {
//        UUID id = UUID.randomUUID();
//        ReviewModel review = createValidReview();
//        review.setRating(4);
//
//        when(repository.findById(id)).thenReturn(review);
//        when(repository.updateStatus(id, ReviewStatus.APPROVED))
//                .thenThrow(new PersistenceException("DB error"));
//
//        assertThatThrownBy(() -> service.approveReview(id))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Gagal approve review");
//    }
//
//    @Test
//    void validateReview_valid() {
//        ReviewModel review = createValidReview();
//        assertThat(service.validateReview(review)).isTrue();
//    }
//
//    @Test
//    void validateReview_invalid() {
//        ReviewModel review = createValidReview();
//        review.setRating(0);
//        assertThat(service.validateReview(review)).isFalse();
//
//        review.setRating(6);
//        assertThat(service.validateReview(review)).isFalse();
//
//        review.setRating(null);
//        assertThat(service.validateReview(review)).isFalse();
//    }
//
//    @Test
//    void calculateEventAverageRating_success() {
//        UUID eventId = UUID.randomUUID();
//        ReviewModel r1 = ReviewModel.builder().eventId(eventId).rating(4).status(ReviewStatus.APPROVED).build();
//        ReviewModel r2 = ReviewModel.builder().eventId(eventId).rating(5).status(ReviewStatus.APPROVED).build();
//        ReviewModel r3 = ReviewModel.builder().eventId(UUID.randomUUID()).rating(3).status(ReviewStatus.APPROVED).build();
//
//        when(repository.findAllByStatus(ReviewStatus.APPROVED)).thenReturn(List.of(r1, r2, r3));
//
//        Double avg = service.calculateEventAverageRating(eventId);
//
//        assertThat(avg).isEqualTo(4.5);
//    }
//
//    @Test
//    void calculateEventAverageRating_empty_returnsZero() {
//        UUID eventId = UUID.randomUUID();
//        when(repository.findAllByStatus(ReviewStatus.APPROVED)).thenReturn(Collections.emptyList());
//
//        Double avg = service.calculateEventAverageRating(eventId);
//
//        assertThat(avg).isEqualTo(0.0);
//    }
//
//    @Test
//    void calculateEventAverageRating_persistenceException_throws() {
//        UUID eventId = UUID.randomUUID();
//        when(repository.findAllByStatus(ReviewStatus.APPROVED)).thenThrow(new PersistenceException("DB error"));
//
//        assertThatThrownBy(() -> service.calculateEventAverageRating(eventId))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Error calculating average rating");
//    }
//
//    @Test
//    void getReviewsByEventId_success() {
//        UUID eventId = UUID.randomUUID();
//        List<ReviewModel> reviews = List.of(createValidReview());
//
//        when(repository.findAllByEventId(eventId)).thenReturn(reviews);
//
//        List<ReviewModel> result = service.getReviewsByEventId(eventId);
//
//        assertThat(result).isEqualTo(reviews);
//    }
//
//    @Test
//    void getReviewsByEventId_persistenceException_throws() {
//        UUID eventId = UUID.randomUUID();
//
//        when(repository.findAllByEventId(eventId)).thenThrow(new PersistenceException("DB error"));
//
//        assertThatThrownBy(() -> service.getReviewsByEventId(eventId))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Error retrieving reviews");
//    }
//}