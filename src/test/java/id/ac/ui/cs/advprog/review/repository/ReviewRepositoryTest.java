package id.ac.ui.cs.advprog.review.repository;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ReviewRepositoryTest {

    @Mock
    private ReviewRepository reviewRepository;

    private ReviewModel review;
    private UUID reviewId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewId = UUID.randomUUID();
        review = ReviewModel.builder()
                .id(reviewId)
                .eventId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .rating(5)
                .comment("Great event!")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .status(ReviewStatus.APPROVED)
                .build();
    }

    @Test
    public void testFindById() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Optional<ReviewModel> foundReview = reviewRepository.findById(reviewId);
        assertTrue(foundReview.isPresent());
        assertEquals(reviewId, foundReview.get().getId());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    public void testFindAll() {
        List<ReviewModel> reviews = Arrays.asList(review);
        when(reviewRepository.findAll()).thenReturn(reviews);
        List<ReviewModel> foundReviews = reviewRepository.findAll();

        assertNotNull(foundReviews);
        assertEquals(1, foundReviews.size());
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    public void testSave() {
        when(reviewRepository.save(review)).thenReturn(review);
        ReviewModel savedReview = reviewRepository.save(review);
        assertNotNull(savedReview);
        assertEquals(review.getId(), savedReview.getId());
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    public void testDelete() {
        doNothing().when(reviewRepository).deleteById(reviewId);
        reviewRepository.deleteById(reviewId);
        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    public void testFindAllByStatus() {
        List<ReviewModel> activeReviews = Arrays.asList(review);
        when(reviewRepository.findAllByStatus(ReviewStatus.APPROVED)).thenReturn(activeReviews);
        List<ReviewModel> foundActiveReviews = reviewRepository.findAllByStatus(ReviewStatus.APPROVED);

        assertNotNull(foundActiveReviews);
        assertEquals(1, foundActiveReviews.size());
        verify(reviewRepository, times(1)).findAllByStatus(ReviewStatus.APPROVED);
    }
}