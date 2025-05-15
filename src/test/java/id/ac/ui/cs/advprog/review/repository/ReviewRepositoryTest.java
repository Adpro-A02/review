package id.ac.ui.cs.advprog.review.repository;

import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewRepositoryTest {

    @InjectMocks
    private ReviewRepository repository;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_persist_whenIdNull() {
        ReviewModel review = ReviewModel.builder().id(null).build();

        ReviewModel result = repository.save(review);

        verify(entityManager).persist(review);
        assertThat(result).isSameAs(review);
    }

    @Test
    void save_merge_whenIdNotNull() {
        ReviewModel review = ReviewModel.builder().id(UUID.randomUUID()).build();
        when(entityManager.merge(review)).thenReturn(review);

        ReviewModel result = repository.save(review);

        verify(entityManager).merge(review);
        assertThat(result).isSameAs(review);
    }

    @Test
    void updateStatus_success() {
        UUID id = UUID.randomUUID();
        ReviewModel review = ReviewModel.builder().id(id).status(ReviewStatus.REJECTED).build();

        when(entityManager.find(ReviewModel.class, id)).thenReturn(review);
        when(entityManager.merge(review)).thenReturn(review);

        ReviewModel updated = repository.updateStatus(id, ReviewStatus.APPROVED);

        assertThat(updated.getStatus()).isEqualTo(ReviewStatus.APPROVED);
        verify(entityManager).merge(review);
    }

    @Test
    void updateStatus_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(ReviewModel.class, id)).thenReturn(null);

        assertThatThrownBy(() -> repository.updateStatus(id, ReviewStatus.APPROVED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Review tidak ditemukan");
    }

    @Test
    void deleteById_found_removes() {
        UUID id = UUID.randomUUID();
        ReviewModel review = ReviewModel.builder().id(id).build();
        when(entityManager.find(ReviewModel.class, id)).thenReturn(review);

        repository.deleteById(id);

        verify(entityManager).remove(review);
    }

    @Test
    void deleteById_notFound_noRemove() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(ReviewModel.class, id)).thenReturn(null);

        repository.deleteById(id);

        verify(entityManager, never()).remove(any());
    }

    @Test
    void findById_returnsEntity() {
        UUID id = UUID.randomUUID();
        ReviewModel review = ReviewModel.builder().id(id).build();
        when(entityManager.find(ReviewModel.class, id)).thenReturn(review);

        ReviewModel result = repository.findById(id);

        assertThat(result).isSameAs(review);
    }

    @Test
    void findAll_returnsList() {
        List<ReviewModel> reviews = List.of(
                ReviewModel.builder().id(UUID.randomUUID()).build(),
                ReviewModel.builder().id(UUID.randomUUID()).build()
        );
        TypedQuery<ReviewModel> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT r FROM ReviewModel r", ReviewModel.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(reviews);

        List<ReviewModel> result = repository.findAll();

        assertThat(result).isEqualTo(reviews);
    }

    @Test
    void findAllByEventId_returnsList() {
        UUID eventId = UUID.randomUUID();
        List<ReviewModel> reviews = List.of(
                ReviewModel.builder().eventId(eventId).build()
        );
        TypedQuery<ReviewModel> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT r FROM ReviewModel r WHERE r.eventId = :eventId", ReviewModel.class))
                .thenReturn(query);
        when(query.setParameter("eventId", eventId)).thenReturn(query);
        when(query.getResultList()).thenReturn(reviews);

        List<ReviewModel> result = repository.findAllByEventId(eventId);

        assertThat(result).isEqualTo(reviews);
    }

    @Test
    void findAllByStatus_returnsList() {
        ReviewStatus status = ReviewStatus.APPROVED;
        List<ReviewModel> reviews = List.of(
                ReviewModel.builder().status(status).build()
        );
        TypedQuery<ReviewModel> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT r FROM ReviewModel r WHERE r.status = :status", ReviewModel.class))
                .thenReturn(query);
        when(query.setParameter("status", status)).thenReturn(query);
        when(query.getResultList()).thenReturn(reviews);

        List<ReviewModel> result = repository.findAllByStatus(status);

        assertThat(result).isEqualTo(reviews);
    }
}
