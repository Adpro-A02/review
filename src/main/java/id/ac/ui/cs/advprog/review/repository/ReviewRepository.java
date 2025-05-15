package id.ac.ui.cs.advprog.review.repository;

import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class ReviewRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public ReviewModel save(ReviewModel review) {
        if (review.getId() == null) {
            entityManager.persist(review);
            return review;
        } else {
            return entityManager.merge(review);
        }
    }

    public ReviewModel updateStatus(UUID reviewId, ReviewStatus newStatus) {
        ReviewModel review = entityManager.find(ReviewModel.class, reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review tidak ditemukan dengan id " + reviewId);
        }
        review.setStatus(newStatus);
        return entityManager.merge(review);
    }


    public void deleteById(UUID id) {
        ReviewModel review = entityManager.find(ReviewModel.class, id);
        if (review != null) {
            entityManager.remove(review);
        }
    }

    public ReviewModel findById(UUID id) {
        return entityManager.find(ReviewModel.class, id);
    }

    public List<ReviewModel> findAll() {
        TypedQuery<ReviewModel> query = entityManager.createQuery("SELECT r FROM ReviewModel r", ReviewModel.class);
        return query.getResultList();
    }

    public List<ReviewModel> findAllByEventId(UUID eventId) {
        TypedQuery<ReviewModel> query = entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId", ReviewModel.class);
        query.setParameter("eventId", eventId);
        return query.getResultList();
    }

    public List<ReviewModel> findAllByStatus(ReviewStatus status) {
        TypedQuery<ReviewModel> query = entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.status = :status", ReviewModel.class);
        query.setParameter("status", status.name());
        return query.getResultList();
    }

    public List<ReviewModel> findAllByEventIdAndStatus(UUID eventId, ReviewStatus status) {
        TypedQuery<ReviewModel> query = entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId AND r.status = :status",
                ReviewModel.class);
        query.setParameter("eventId", eventId);
        query.setParameter("status", status);
        return query.getResultList();
    }
}
