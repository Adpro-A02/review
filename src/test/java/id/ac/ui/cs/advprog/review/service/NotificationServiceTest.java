package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceTest {

    private NotificationService notificationService;
    private ReviewModel review;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        notificationService = new NotificationService();

        UUID reviewId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

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

        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testSendReviewCreatedNotification() {
        notificationService.sendReviewCreatedNotification(review);

        String expectedOutput = "Notifikasi: Review untuk acara " + review.getEventId() + " dibuat.";
        assertTrue(outContent.toString().trim().contains(expectedOutput));
    }

    @Test
    public void testSendReviewApprovedNotification() {
        notificationService.sendReviewApprovedNotification(review);

        String expectedOutput = "Notifikasi: Review untuk acara " + review.getEventId() + " disetujui.";
        assertTrue(outContent.toString().trim().contains(expectedOutput));
    }

    @Test
    public void testSendReviewRejectedNotification() {
        notificationService.sendReviewRejectedNotification(review);

        String expectedOutput = "Notifikasi: Review untuk acara " + review.getEventId() + " ditolak.";
        assertTrue(outContent.toString().trim().contains(expectedOutput));
    }
}