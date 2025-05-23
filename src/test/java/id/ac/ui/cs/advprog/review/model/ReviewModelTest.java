//package id.ac.ui.cs.advprog.review.model;
//
//import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//
//public class ReviewModelTest {
//
//    @Test
//    void testBuilderAndGetters() {
//        UUID id = UUID.randomUUID();
//        UUID eventId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        Integer rating = 4;
//        String comment = "Nice event!";
//        LocalDateTime createdDate = LocalDateTime.now().withNano(0);
//        LocalDateTime updatedDate = createdDate.plusDays(1);
//        ReviewStatus status = ReviewStatus.APPROVED;
//        Long version = 1L;
//
//        ReviewModel review = ReviewModel.builder()
//                .id(id)
//                .eventId(eventId)
//                .userId(userId)
//                .rating(rating)
//                .comment(comment)
//                .createdDate(createdDate)
//                .updatedDate(updatedDate)
//                .status(status)
//                .version(version)
//                .build();
//
//        assertThat(review.getId()).isEqualTo(id);
//        assertThat(review.getEventId()).isEqualTo(eventId);
//        assertThat(review.getUserId()).isEqualTo(userId);
//        assertThat(review.getRating()).isEqualTo(rating);
//        assertThat(review.getComment()).isEqualTo(comment);
//        assertThat(review.getCreatedDate()).isEqualTo(createdDate);
//        assertThat(review.getUpdatedDate()).isEqualTo(updatedDate);
//        assertThat(review.getStatus()).isEqualTo(status);
//        assertThat(review.getVersion()).isEqualTo(version);
//    }
//
//    @Test
//    void testSetters() {
//        ReviewModel review = new ReviewModel();
//
//        UUID id = UUID.randomUUID();
//        UUID eventId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        Integer rating = 5;
//        String comment = "Awesome!";
//        LocalDateTime createdDate = LocalDateTime.now().withNano(0);
//        LocalDateTime updatedDate = createdDate.plusHours(2);
//        ReviewStatus status = ReviewStatus.REJECTED;
//        Long version = 2L;
//
//        review.setId(id);
//        review.setEventId(eventId);
//        review.setUserId(userId);
//        review.setRating(rating);
//        review.setComment(comment);
//        review.setCreatedDate(createdDate);
//        review.setUpdatedDate(updatedDate);
//        review.setStatus(status);
//        review.setVersion(version);
//
//        assertThat(review.getId()).isEqualTo(id);
//        assertThat(review.getEventId()).isEqualTo(eventId);
//        assertThat(review.getUserId()).isEqualTo(userId);
//        assertThat(review.getRating()).isEqualTo(rating);
//        assertThat(review.getComment()).isEqualTo(comment);
//        assertThat(review.getCreatedDate()).isEqualTo(createdDate);
//        assertThat(review.getUpdatedDate()).isEqualTo(updatedDate);
//        assertThat(review.getStatus()).isEqualTo(status);
//        assertThat(review.getVersion()).isEqualTo(version);
//    }
//
//    @Test
//    void testEqualsAndHashCode() {
//        UUID id = UUID.randomUUID();
//
//        ReviewModel review1 = ReviewModel.builder()
//                .id(id)
//                .build();
//
//        ReviewModel review2 = ReviewModel.builder()
//                .id(id)
//                .build();
//
//        ReviewModel review3 = ReviewModel.builder()
//                .id(UUID.randomUUID())
//                .build();
//
//        assertThat(review1).isEqualTo(review2);
//        assertThat(review1.hashCode()).isEqualTo(review2.hashCode());
//
//        assertThat(review1).isNotEqualTo(review3);
//        assertThat(review1.hashCode()).isNotEqualTo(review3.hashCode());
//
//        assertThat(review1).isEqualTo(review1);
//
//        assertThat(review1).isNotEqualTo(null);
//        assertThat(review1).isNotEqualTo("string");
//    }
//
//    @Test
//    void testReviewStatusEnum() {
//        assertThat(ReviewStatus.APPROVED.name()).isEqualTo("APPROVED");
//        assertThat(ReviewStatus.REJECTED.name()).isEqualTo("REJECTED");
//    }
//}