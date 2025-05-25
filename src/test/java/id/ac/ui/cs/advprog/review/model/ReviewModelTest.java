//package id.ac.ui.cs.advprog.review.model;
//
//import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ReviewModelTest {
//    private ReviewModel reviewModel;
//    private UUID testId;
//    private UUID testEventId;
//    private UUID testUserId;
//    private UUID testOrganizerId;
//    private LocalDateTime testCreatedDate;
//    private LocalDateTime testUpdatedDate;
//
//    @BeforeEach
//    void setUp() {
//        testId = UUID.randomUUID();
//        testEventId = UUID.randomUUID();
//        testUserId = UUID.randomUUID();
//        testOrganizerId = UUID.randomUUID();
//        testCreatedDate = LocalDateTime.now();
//        testUpdatedDate = LocalDateTime.now().plusHours(1);
//        reviewModel = new ReviewModel();
//    }
//
//    @Test
//    @DisplayName("Test NoArgsConstructor creates object with default values")
//    void testNoArgsConstructor() {
//
//        ReviewModel review = new ReviewModel();
//
//        assertNull(review.getId());
//        assertNull(review.getEventId());
//        assertNull(review.getUserId());
//        assertNull(review.getOrganizerId());
//        assertNull(review.getRating());
//        assertNull(review.getComment());
//        assertNull(review.getCreatedDate());
//        assertNull(review.getUpdatedDate());
//        assertEquals(ReviewStatus.APPROVED, review.getStatus());
//        assertNull(review.getVersion());
//    }
//
//    @Test
//    @DisplayName("Test AllArgsConstructor creates object with all parameters")
//    void testAllArgsConstructor() {
//
//        ReviewModel review = new ReviewModel(
//                testId,
//                testEventId,
//                testUserId,
//                testOrganizerId,
//                5,
//                "Great event!",
//                testCreatedDate,
//                testUpdatedDate,
//                ReviewStatus.FLAGGED,
//                1L
//        );
//
//        assertEquals(testId, review.getId());
//        assertEquals(testEventId, review.getEventId());
//        assertEquals(testUserId, review.getUserId());
//        assertEquals(testOrganizerId, review.getOrganizerId());
//        assertEquals(5, review.getRating());
//        assertEquals("Great event!", review.getComment());
//        assertEquals(testCreatedDate, review.getCreatedDate());
//        assertEquals(testUpdatedDate, review.getUpdatedDate());
//        assertEquals(ReviewStatus.FLAGGED, review.getStatus());
//        assertEquals(1L, review.getVersion());
//    }
//
//    @Test
//    @DisplayName("Test Builder pattern creates object correctly")
//    void testBuilder() {
//
//        ReviewModel review = ReviewModel.builder()
//                .id(testId)
//                .eventId(testEventId)
//                .userId(testUserId)
//                .organizerId(testOrganizerId)
//                .rating(4)
//                .comment("Good event")
//                .createdDate(testCreatedDate)
//                .updatedDate(testUpdatedDate)
//                .status(ReviewStatus.FLAGGED)
//                .version(2L)
//                .build();
//
//        assertEquals(testId, review.getId());
//        assertEquals(testEventId, review.getEventId());
//        assertEquals(testUserId, review.getUserId());
//        assertEquals(testOrganizerId, review.getOrganizerId());
//        assertEquals(4, review.getRating());
//        assertEquals("Good event", review.getComment());
//        assertEquals(testCreatedDate, review.getCreatedDate());
//        assertEquals(testUpdatedDate, review.getUpdatedDate());
//        assertEquals(ReviewStatus.FLAGGED, review.getStatus());
//        assertEquals(2L, review.getVersion());
//    }
//
//    @Test
//    @DisplayName("Test getId and setId")
//    void testGetSetId() {
//
//        reviewModel.setId(testId);
//
//        assertEquals(testId, reviewModel.getId());
//    }
//
//    @Test
//    @DisplayName("Test getEventId and setEventId")
//    void testGetSetEventId() {
//
//        reviewModel.setEventId(testEventId);
//
//        assertEquals(testEventId, reviewModel.getEventId());
//    }
//
//    @Test
//    @DisplayName("Test getUserId and setUserId")
//    void testGetSetUserId() {
//
//        reviewModel.setUserId(testUserId);
//
//        assertEquals(testUserId, reviewModel.getUserId());
//    }
//
//    @Test
//    @DisplayName("Test getOrganizerId and setOrganizerId")
//    void testGetSetOrganizerId() {
//
//        reviewModel.setOrganizerId(testOrganizerId);
//
//        assertEquals(testOrganizerId, reviewModel.getOrganizerId());
//    }
//
//    @Test
//    @DisplayName("Test getRating and setRating")
//    void testGetSetRating() {
//
//        reviewModel.setRating(5);
//
//        assertEquals(5, reviewModel.getRating());
//    }
//
//    @Test
//    @DisplayName("Test getComment and setComment")
//    void testGetSetComment() {
//
//        String comment = "This is a test comment";
//
//        reviewModel.setComment(comment);
//
//        assertEquals(comment, reviewModel.getComment());
//    }
//
//    @Test
//    @DisplayName("Test getCreatedDate and setCreatedDate")
//    void testGetSetCreatedDate() {
//
//        reviewModel.setCreatedDate(testCreatedDate);
//
//        assertEquals(testCreatedDate, reviewModel.getCreatedDate());
//    }
//
//    @Test
//    @DisplayName("Test getUpdatedDate and setUpdatedDate")
//    void testGetSetUpdatedDate() {
//
//        reviewModel.setUpdatedDate(testUpdatedDate);
//
//        assertEquals(testUpdatedDate, reviewModel.getUpdatedDate());
//    }
//
//    @Test
//    @DisplayName("Test getStatus and setStatus")
//    void testGetSetStatus() {
//
//        reviewModel.setStatus(ReviewStatus.FLAGGED);
//
//        assertEquals(ReviewStatus.FLAGGED, reviewModel.getStatus());
//    }
//
//    @Test
//    @DisplayName("Test getVersion and setVersion")
//    void testGetSetVersion() {
//
//        reviewModel.setVersion(3L);
//
//        assertEquals(3L, reviewModel.getVersion());
//    }
//
//    @Test
//    @DisplayName("Test default status is APPROVED")
//    void testDefaultStatus() {
//
//        ReviewModel review = new ReviewModel();
//
//        assertEquals(ReviewStatus.APPROVED, review.getStatus());
//    }
//
//    @Test
//    @DisplayName("Test equals with same ID returns true")
//    void testEqualsWithSameId() {
//
//        ReviewModel review1 = new ReviewModel();
//        ReviewModel review2 = new ReviewModel();
//        review1.setId(testId);
//        review2.setId(testId);
//
//        assertEquals(review1, review2);
//        assertTrue(review1.equals(review2));
//        assertTrue(review2.equals(review1));
//    }
//
//    @Test
//    @DisplayName("Test equals with different ID returns false")
//    void testEqualsWithDifferentId() {
//
//        ReviewModel review1 = new ReviewModel();
//        ReviewModel review2 = new ReviewModel();
//        review1.setId(testId);
//        review2.setId(UUID.randomUUID());
//
//        assertNotEquals(review1, review2);
//        assertFalse(review1.equals(review2));
//        assertFalse(review2.equals(review1));
//    }
//
//    @Test
//    @DisplayName("Test equals with null ID")
//    void testEqualsWithNullId() {
//
//        ReviewModel review1 = new ReviewModel();
//        ReviewModel review2 = new ReviewModel();
//        review1.setId(null);
//        review2.setId(null);
//
//        assertEquals(review1, review2);
//    }
//
//    @Test
//    @DisplayName("Test equals with one null ID and one non-null ID")
//    void testEqualsWithOneNullId() {
//
//        ReviewModel review1 = new ReviewModel();
//        ReviewModel review2 = new ReviewModel();
//        review1.setId(null);
//        review2.setId(testId);
//
//        assertNotEquals(review1, review2);
//        assertNotEquals(review2, review1);
//    }
//
//    @Test
//    @DisplayName("Test equals with same object returns true")
//    void testEqualsWithSameObject() {
//
//        reviewModel.setId(testId);
//
//        assertEquals(reviewModel, reviewModel);
//        assertTrue(reviewModel.equals(reviewModel));
//    }
//
//    @Test
//    @DisplayName("Test equals with null returns false")
//    void testEqualsWithNull() {
//
//        reviewModel.setId(testId);
//
//        assertNotEquals(reviewModel, null);
//        assertFalse(reviewModel.equals(null));
//    }
//
//    @Test
//    @DisplayName("Test equals with different class returns false")
//    void testEqualsWithDifferentClass() {
//
//        reviewModel.setId(testId);
//        String differentObject = "not a ReviewModel";
//
//        assertNotEquals(reviewModel, differentObject);
//        assertFalse(reviewModel.equals(differentObject));
//    }
//
//    @Test
//    @DisplayName("Test hashCode consistency")
//    void testHashCodeConsistency() {
//
//        reviewModel.setId(testId);
//
//        int hashCode1 = reviewModel.hashCode();
//        int hashCode2 = reviewModel.hashCode();
//
//        assertEquals(hashCode1, hashCode2);
//    }
//
//    @Test
//    @DisplayName("Test hashCode with same ID produces same hash")
//    void testHashCodeWithSameId() {
//
//        ReviewModel review1 = new ReviewModel();
//        ReviewModel review2 = new ReviewModel();
//        review1.setId(testId);
//        review2.setId(testId);
//
//        assertEquals(review1.hashCode(), review2.hashCode());
//    }
//
//    @Test
//    @DisplayName("Test hashCode with null ID")
//    void testHashCodeWithNullId() {
//
//        ReviewModel review = new ReviewModel();
//        review.setId(null);
//
//        assertDoesNotThrow(() -> review.hashCode());
//    }
//
//    @Test
//    @DisplayName("Test toString method")
//    void testToString() {
//
//        reviewModel.setId(testId);
//        reviewModel.setEventId(testEventId);
//        reviewModel.setUserId(testUserId);
//        reviewModel.setRating(5);
//        reviewModel.setComment("Test comment");
//        reviewModel.setStatus(ReviewStatus.APPROVED);
//
//        String toString = reviewModel.toString();
//
//        assertNotNull(toString);
//        assertTrue(toString.contains("ReviewModel"));
//    }
//
//    @Test
//    @DisplayName("Test all ReviewStatus enum values can be set")
//    void testAllReviewStatusValues() {
//
//        reviewModel.setStatus(ReviewStatus.APPROVED);
//        assertEquals(ReviewStatus.APPROVED, reviewModel.getStatus());
//
//        reviewModel.setStatus(ReviewStatus.FLAGGED);
//        assertEquals(ReviewStatus.FLAGGED, reviewModel.getStatus());
//    }
//
//    @Test
//    @DisplayName("Test setting null values")
//    void testSettingNullValues() {
//
//        reviewModel.setId(testId);
//        reviewModel.setEventId(testEventId);
//        reviewModel.setUserId(testUserId);
//        reviewModel.setOrganizerId(testOrganizerId);
//        reviewModel.setRating(5);
//        reviewModel.setComment("Test");
//        reviewModel.setCreatedDate(testCreatedDate);
//        reviewModel.setUpdatedDate(testUpdatedDate);
//        reviewModel.setVersion(1L);
//
//        reviewModel.setId(null);
//        reviewModel.setEventId(null);
//        reviewModel.setUserId(null);
//        reviewModel.setOrganizerId(null);
//        reviewModel.setRating(null);
//        reviewModel.setComment(null);
//        reviewModel.setCreatedDate(null);
//        reviewModel.setUpdatedDate(null);
//        reviewModel.setVersion(null);
//
//        assertNull(reviewModel.getId());
//        assertNull(reviewModel.getEventId());
//        assertNull(reviewModel.getUserId());
//        assertNull(reviewModel.getOrganizerId());
//        assertNull(reviewModel.getRating());
//        assertNull(reviewModel.getComment());
//        assertNull(reviewModel.getCreatedDate());
//        assertNull(reviewModel.getUpdatedDate());
//        assertNull(reviewModel.getVersion());
//    }
//
//    @Test
//    @DisplayName("Test builder with minimal required fields")
//    void testBuilderWithMinimalFields() {
//
//        ReviewModel review = ReviewModel.builder()
//                .eventId(testEventId)
//                .userId(testUserId)
//                .rating(3)
//                .createdDate(testCreatedDate)
//                .build();
//
//        assertNull(review.getId());
//        assertEquals(testEventId, review.getEventId());
//        assertEquals(testUserId, review.getUserId());
//        assertNull(review.getOrganizerId());
//        assertEquals(3, review.getRating());
//        assertNull(review.getComment());
//        assertEquals(testCreatedDate, review.getCreatedDate());
//        assertNull(review.getUpdatedDate());
//        assertEquals(ReviewStatus.APPROVED, review.getStatus());
//        assertNull(review.getVersion());
//    }
//
//    @Test
//    @DisplayName("Test builder with empty builder")
//    void testBuilderEmpty() {
//
//        ReviewModel review = ReviewModel.builder().build();
//
//        assertNull(review.getId());
//        assertNull(review.getEventId());
//        assertNull(review.getUserId());
//        assertNull(review.getOrganizerId());
//        assertNull(review.getRating());
//        assertNull(review.getComment());
//        assertNull(review.getCreatedDate());
//        assertNull(review.getUpdatedDate());
//        assertEquals(ReviewStatus.APPROVED, review.getStatus());
//        assertNull(review.getVersion());
//    }
//}