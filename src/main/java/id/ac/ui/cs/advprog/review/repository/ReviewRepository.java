package id.ac.ui.cs.advprog.review.repository;

import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
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

    @Transactional
    public ReviewModel updateStatus(UUID reviewId, ReviewStatus newStatus) {
        int updatedCount = entityManager.createQuery(
                        "UPDATE ReviewModel r SET r.status = :status WHERE r.id = :id")
                .setParameter("status", newStatus)
                .setParameter("id", reviewId)
                .executeUpdate();

        if (updatedCount == 0) {
            throw new IllegalArgumentException("Review tidak ditemukan dengan id " + reviewId);
        }

        return entityManager.find(ReviewModel.class, reviewId);
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

    public List<ReviewModel> findAllByEventIdAndOrganizerId(UUID eventId, UUID organizerId) {
        TypedQuery<ReviewModel> query = entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.eventId = :eventId AND r.organizerId = :organizerId",
                ReviewModel.class);
        query.setParameter("eventId", eventId);
        query.setParameter("organizerId", organizerId);
        return query.getResultList();
    }

    public List<ReviewModel> findAllByStatus(ReviewStatus status) {
        TypedQuery<ReviewModel> query = entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.status = :status", ReviewModel.class);
        query.setParameter("status", status);
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

    public Optional<ReviewModel> findByUserIdAndEventId(UUID userId, UUID eventId) {
        TypedQuery<ReviewModel> query = entityManager.createQuery(
                "SELECT r FROM ReviewModel r WHERE r.userId = :userId AND r.eventId = :eventId",
                ReviewModel.class);
        query.setParameter("userId", userId);
        query.setParameter("eventId", eventId);

        List<ReviewModel> results = query.getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

}