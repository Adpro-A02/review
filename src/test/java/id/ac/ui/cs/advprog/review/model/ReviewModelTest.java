package id.ac.ui.cs.advprog.review.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewModelTest {

    private ReviewModel review;

    @BeforeEach
    public void setUp() {
        review = ReviewModel.builder()
                .id(UUID.randomUUID())
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
    public void testReviewModelConstructorAndGetters() {
        assertNotNull(review);
        assertNotNull(review.getId());
        assertNotNull(review.getEventId());
        assertNotNull(review.getUserId());
        assertEquals(5, review.getRating());
        assertEquals("Great event!", review.getComment());
        assertNotNull(review.getCreatedDate());
        assertNotNull(review.getUpdatedDate());
        assertEquals(ReviewStatus.APPROVED, review.getStatus());
    }

    @Test
    public void testReviewModelBuilder() {
        ReviewModel reviewBuilt = ReviewModel.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .rating(4)
                .comment("Good event")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .status(ReviewStatus.REJECTED)
                .build();

        assertNotNull(reviewBuilt);
        assertEquals(4, reviewBuilt.getRating());
        assertEquals("Good event", reviewBuilt.getComment());
        assertEquals(ReviewStatus.REJECTED, reviewBuilt.getStatus());
    }

    @Test
    public void testSetAndGetStatus() {
        review.setStatus(ReviewStatus.REJECTED);
        assertEquals(ReviewStatus.REJECTED, review.getStatus());
    }

    @Test
    public void testNullComment() {
        review.setComment(null);
        assertNull(review.getComment());
    }

    @Test
    public void testNullUpdatedDate() {
        review.setUpdatedDate(null);
        assertNull(review.getUpdatedDate());
    }

    @Test
    public void testReviewStatusEnum() {
        assertNotNull(ReviewStatus.valueOf("APPROVED"));
        assertNotNull(ReviewStatus.valueOf("REJECTED"));
    }

    @Test
    public void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID eventId1 = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        LocalDateTime createdDate = LocalDateTime.now();
        LocalDateTime updatedDate = LocalDateTime.now();

        ReviewModel review1 = ReviewModel.builder()
                .id(id)
                .eventId(eventId1)
                .userId(userId1)
                .rating(5)
                .comment("Excellent!")
                .createdDate(createdDate)
                .updatedDate(updatedDate)
                .status(ReviewStatus.APPROVED)
                .build();

        ReviewModel review2 = ReviewModel.builder()
                .id(id)
                .eventId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .rating(3)
                .comment("Average")
                .createdDate(LocalDateTime.now().minusDays(1))
                .updatedDate(LocalDateTime.now().minusDays(1))
                .status(ReviewStatus.REJECTED)
                .build();

        assertEquals(review1, review2);
        assertEquals(review1.hashCode(), review2.hashCode());
    }

    @Test
    public void testUniqueUUID() {
        UUID id1 = review.getId();
        ReviewModel anotherReview = ReviewModel.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .rating(5)
                .comment("Another review")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .status(ReviewStatus.APPROVED)
                .build();
        UUID id2 = anotherReview.getId();

        assertNotEquals(id1, id2, "UUIDs should be unique for different reviews");
    }

    @Test
    public void testSetAndGetEventId() {
        UUID newEventId = UUID.randomUUID();
        review.setEventId(newEventId);
        assertEquals(newEventId, review.getEventId());
    }

    @Test
    public void testSetAndGetUserId() {
        UUID newUserId = UUID.randomUUID();
        review.setUserId(newUserId);
        assertEquals(newUserId, review.getUserId());
    }

    @Test
    public void testSetAndGetRating() {
        review.setRating(3);
        assertEquals(3, review.getRating());
    }

    @Test
    public void testSetAndGetComment() {
        review.setComment("Updated comment");
        assertEquals("Updated comment", review.getComment());
    }

    @Test
    public void testSetAndGetCreatedDate() {
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        review.setCreatedDate(newDate);
        assertEquals(newDate, review.getCreatedDate());
    }

    @Test
    public void testSetAndGetUpdatedDate() {
        LocalDateTime newDate = LocalDateTime.now().plusDays(1);
        review.setUpdatedDate(newDate);
        assertEquals(newDate, review.getUpdatedDate());
    }

    @Test
    public void testNoArgsConstructor() {
        ReviewModel emptyReview = new ReviewModel();
        assertNull(emptyReview.getId());
        assertNull(emptyReview.getEventId());
        assertNull(emptyReview.getUserId());
        assertNull(emptyReview.getRating());
        assertNull(emptyReview.getComment());
        assertNull(emptyReview.getCreatedDate());
        assertNull(emptyReview.getUpdatedDate());
        assertNull(emptyReview.getStatus());
    }

    @Test
    public void testSetAndGetId() {
        UUID newId = UUID.randomUUID();
        review.setId(newId);
        assertEquals(newId, review.getId());
    }

    @Test
    public void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Integer rating = 4;
        String comment = "Test comment";
        LocalDateTime createdDate = LocalDateTime.now();
        LocalDateTime updatedDate = LocalDateTime.now();
        ReviewStatus status = ReviewStatus.APPROVED;

        ReviewModel customReview = new ReviewModel(
                id, eventId, userId, rating, comment,
                createdDate, updatedDate, status
        );

        assertEquals(id, customReview.getId());
        assertEquals(eventId, customReview.getEventId());
        assertEquals(userId, customReview.getUserId());
        assertEquals(rating, customReview.getRating());
        assertEquals(comment, customReview.getComment());
        assertEquals(createdDate, customReview.getCreatedDate());
        assertEquals(updatedDate, customReview.getUpdatedDate());
        assertEquals(status, customReview.getStatus());
    }

    @Test
    public void testEqualsWithDifferentIds() {
        ReviewModel review1 = ReviewModel.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .rating(5)
                .comment("Test")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .status(ReviewStatus.APPROVED)
                .build();

        ReviewModel review2 = ReviewModel.builder()
                .id(UUID.randomUUID())
                .eventId(review1.getEventId())
                .userId(review1.getUserId())
                .rating(review1.getRating())
                .comment(review1.getComment())
                .createdDate(review1.getCreatedDate())
                .updatedDate(review1.getUpdatedDate())
                .status(review1.getStatus())
                .build();

        assertNotEquals(review1, review2);
        assertNotEquals(review1.hashCode(), review2.hashCode());
    }

    @Test
    public void testEqualsWithSameObject() {
        assertEquals(review, review);
        assertEquals(review.hashCode(), review.hashCode());
    }

    @Test
    public void testEqualsWithNull() {
        assertNotEquals(review, null);
    }

    @Test
    public void testEqualsWithDifferentClass() {
        assertNotEquals(review, "Not a ReviewModel");
    }
}