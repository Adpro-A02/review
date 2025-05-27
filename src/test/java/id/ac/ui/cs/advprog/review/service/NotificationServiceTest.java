package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class NotificationServiceTest {

    @Test
    void testSendReviewCreatedNotification() {
        NotificationService service = new NotificationService();
        ReviewModel review = ReviewModel.builder().eventId(UUID.randomUUID()).build();

        service.sendReviewCreatedNotification(review);

    }

    @Test
    void testSendReviewApprovedNotification() {
        NotificationService service = new NotificationService();
        ReviewModel review = ReviewModel.builder().eventId(UUID.randomUUID()).build();

        service.sendReviewApprovedNotification(review);
    }

    @Test
    void testSendReviewRejectedNotification() {
        NotificationService service = new NotificationService();
        ReviewModel review = ReviewModel.builder().eventId(UUID.randomUUID()).build();

        service.sendReviewRejectedNotification(review);
    }
}