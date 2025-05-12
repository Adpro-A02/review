package id.ac.ui.cs.advprog.review.repository;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewModel, UUID> {
    List<ReviewModel> findAll();

    @Override
    <S extends ReviewModel> S save(S entity);

    void deleteById(UUID id);

    List<ReviewModel> findAllByEventId(UUID eventId);

    List<ReviewModel> findAllByStatus(ReviewStatus status);
}