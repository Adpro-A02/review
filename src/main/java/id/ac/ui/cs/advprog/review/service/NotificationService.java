package id.ac.ui.cs.advprog.review.service;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void sendReviewCreatedNotification(ReviewModel review) {
        System.out.println("Notifikasi: Review untuk acara " + review.getEventId() + " dibuat.");
    }

    public void sendReviewApprovedNotification(ReviewModel review) {
        System.out.println("Notifikasi: Review untuk acara " + review.getEventId() + " disetujui.");
    }

    public void sendReviewRejectedNotification(ReviewModel review) {
        System.out.println("Notifikasi: Review untuk acara " + review.getEventId() + " ditolak.");
    }
}