package id.ac.ui.cs.advprog.review.controller;

import id.ac.ui.cs.advprog.review.dto.*;
import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
import id.ac.ui.cs.advprog.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private ReviewRepository repository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReviewController reviewController;

    private UUID userId;
    private UUID eventId;
    private UUID reviewId;
    private ReviewModel reviewModel;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        reviewModel = ReviewModel.builder()
                .id(reviewId)
                .eventId(eventId)
                .userId(userId)
                .organizerId(UUID.randomUUID())
                .rating(5)
                .comment("Great!")
                .createdDate(LocalDateTime.now())
                .status(ReviewStatus.APPROVED)
                .build();

        lenient().when(authentication.getName()).thenReturn(userId.toString());
    }

    @SuppressWarnings("unchecked")
    private void mockAuthorities(String... authorities) {
        List<GrantedAuthority> grantedAuthorities = Stream.of(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        lenient().when(authentication.getAuthorities()).thenReturn((List) grantedAuthorities);
    }

    @Test
    void testToEntity_nullDTO_shouldReturnNull() {

        try {
            Method toEntityMethod = ReviewController.class.getDeclaredMethod("toEntity", ReviewDTO.class);
            toEntityMethod.setAccessible(true);

            ReviewModel result = (ReviewModel) toEntityMethod.invoke(reviewController, (ReviewDTO) null);

            assertNull(result);
        } catch (Exception e) {
            fail("Failed to test toEntity method: " + e.getMessage());
        }
    }


    @Test
    void testCreateReview_success_defaultStatus() {
        ReviewDTO requestDTO = ReviewDTO.builder().eventId(eventId).rating(5).comment("Good").build();
        mockAuthorities("Attendee");

        ReviewModel modelFromDTO = ReviewModel.builder()
                .eventId(eventId)
                .userId(userId)
                .rating(5)
                .comment("Good")
                .status(ReviewStatus.APPROVED)
                .build();

        ReviewModel savedModel = ReviewModel.builder()
                .id(reviewId)
                .eventId(eventId)
                .userId(userId)
                .rating(5)
                .comment("Good")
                .createdDate(LocalDateTime.now())
                .status(ReviewStatus.APPROVED)
                .build();

        when(reviewService.createReview(argThat(model ->
                model.getEventId().equals(modelFromDTO.getEventId()) &&
                        model.getUserId().equals(modelFromDTO.getUserId()) &&
                        model.getRating().equals(modelFromDTO.getRating()) &&
                        model.getComment().equals(modelFromDTO.getComment()) &&
                        model.getStatus().equals(modelFromDTO.getStatus())
        ))).thenReturn(CompletableFuture.completedFuture(savedModel));

        ReviewResponseDTO<ReviewDTO> response = reviewController.createReview(requestDTO, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Review berhasil dibuat", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(savedModel.getId(), response.getData().getId());
        assertEquals(userId, response.getData().getUserId());
        assertEquals(ReviewStatus.APPROVED, response.getData().getStatus());

        verify(authentication).getName();

    }

    @Test
    void testCreateReview_success_withProvidedStatus() {
        ReviewDTO requestDTO = ReviewDTO.builder()
                .eventId(eventId).rating(4).comment("Okay")
                .status(ReviewStatus.FLAGGED)
                .build();
        mockAuthorities("Attendee");

        ReviewModel modelFromDTO = ReviewModel.builder()
                .eventId(eventId)
                .userId(userId)
                .rating(4)
                .comment("Okay")
                .status(ReviewStatus.FLAGGED)
                .build();

        ReviewModel savedModel = ReviewModel.builder()
                .id(reviewId)
                .eventId(eventId)
                .userId(userId)
                .rating(4)
                .comment("Okay")
                .createdDate(LocalDateTime.now())
                .status(ReviewStatus.FLAGGED)
                .build();

        when(reviewService.createReview(argThat(model ->
                model.getEventId().equals(modelFromDTO.getEventId()) &&
                        model.getUserId().equals(modelFromDTO.getUserId()) &&
                        model.getRating().equals(modelFromDTO.getRating()) &&
                        model.getComment().equals(modelFromDTO.getComment()) &&
                        model.getStatus().equals(modelFromDTO.getStatus())
        ))).thenReturn(CompletableFuture.completedFuture(savedModel));

        ReviewResponseDTO<ReviewDTO> response = reviewController.createReview(requestDTO, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Review berhasil dibuat", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(ReviewStatus.FLAGGED, response.getData().getStatus());

        verify(authentication).getName();
    }

    @Test
    void testCreateReview_serviceThrowsException() {
        ReviewDTO requestDTO = ReviewDTO.builder().eventId(eventId).rating(5).comment("Good").build();
        mockAuthorities("Attendee");

        CompletableFuture<ReviewModel> exceptionallyFuture = new CompletableFuture<>();
        exceptionallyFuture.completeExceptionally(new RuntimeException("Service error"));
        when(reviewService.createReview(any(ReviewModel.class))).thenReturn(exceptionallyFuture);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            reviewController.createReview(requestDTO, authentication);
        });
        assertEquals("java.lang.RuntimeException: Service error", thrown.getMessage());
        verify(authentication).getName();
    }

    @Test
    void testDeleteReview_asAdmin_success() {
        when(repository.findById(reviewId)).thenReturn(reviewModel);
        doNothing().when(reviewService).deleteReview(reviewId);
        mockAuthorities("Admin");

        ReviewResponseDTO<Void> response = reviewController.deleteReview(reviewId, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Review berhasil dihapus", response.getMessage());
        verify(reviewService).deleteReview(reviewId);
        verify(authentication).getAuthorities();
    }

    @Test
    void testDeleteReview_asOwner_success() {
        when(repository.findById(reviewId)).thenReturn(reviewModel);
        doNothing().when(reviewService).deleteReview(reviewId);
        mockAuthorities("Attendee");

        ReviewResponseDTO<Void> response = reviewController.deleteReview(reviewId, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Review berhasil dihapus", response.getMessage());
        verify(reviewService).deleteReview(reviewId);
        verify(authentication).getAuthorities();
        verify(authentication).getName();
    }

    @Test
    void testDeleteReview_asAttendee_notOwner_forbidden() {
        UUID otherUserId = UUID.randomUUID();
        ReviewModel otherUserReview = ReviewModel.builder().id(reviewId).userId(otherUserId).build();
        when(repository.findById(reviewId)).thenReturn(otherUserReview);

        mockAuthorities("Attendee");

        ReviewResponseDTO<Void> response = reviewController.deleteReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Tidak bisa menghapus review milik orang lain", response.getMessage());
        verify(reviewService, never()).deleteReview(reviewId);
        verify(authentication).getAuthorities();
        verify(authentication).getName();
    }

    @Test
    void testDeleteReview_notFound() {
        when(repository.findById(reviewId)).thenReturn(null);

        mockAuthorities("Admin");

        ReviewResponseDTO<Void> response = reviewController.deleteReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Review dengan ID " + reviewId + " tidak ditemukan", response.getMessage());
        verify(reviewService, never()).deleteReview(reviewId);

    }

    @Test
    void testGetMyReviewForEvent_success() {
        when(reviewService.getReviewByUserIdAndEventId(userId, eventId)).thenReturn(reviewModel);
        mockAuthorities("Attendee");

        ReviewResponseDTO<ReviewDTO> response = reviewController.getMyReviewForEvent(eventId, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Review Anda untuk event " + eventId, response.getMessage());
        assertNotNull(response.getData());
        assertEquals(reviewId, response.getData().getId());
        verify(authentication).getName();
    }

    @Test
    void testGetMyReviewForEvent_serviceReturnsNull_toDTOCalledWithNull() {
        when(reviewService.getReviewByUserIdAndEventId(userId, eventId)).thenReturn(null);
        mockAuthorities("Attendee");

        ReviewResponseDTO<ReviewDTO> response = reviewController.getMyReviewForEvent(eventId, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Review Anda untuk event " + eventId, response.getMessage());
        assertNull(response.getData());
        verify(authentication).getName();
    }

    @Test
    void testGetMyReviewForEvent_notFound_runtimeException() {
        when(reviewService.getReviewByUserIdAndEventId(userId, eventId)).thenThrow(new RuntimeException("Review not found"));
        mockAuthorities("Attendee");

        ReviewResponseDTO<ReviewDTO> response = reviewController.getMyReviewForEvent(eventId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Review not found", response.getMessage());
        assertNull(response.getData());
        verify(authentication).getName();
    }

    @Test
    void testUpdateMyReviewForEvent_success() {
        ReviewDTO updateRequest = ReviewDTO.builder().rating(4).comment("Okayish").build();
        ReviewModel updatedModel = ReviewModel.builder()
                .id(reviewId).userId(userId).eventId(eventId).rating(4).comment("Okayish")
                .status(ReviewStatus.APPROVED).updatedDate(LocalDateTime.now()).build();

        when(reviewService.updateReviewByUserIdAndEventId(eq(userId), eq(eventId), argThat(model ->
                model.getRating().equals(4) && model.getComment().equals("Okayish")
        ))).thenReturn(updatedModel);
        mockAuthorities("Attendee");

        ReviewResponseDTO<ReviewDTO> response = reviewController.updateMyReviewForEvent(eventId, updateRequest, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Review berhasil diperbarui", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(4, response.getData().getRating());
        assertEquals("Okayish", response.getData().getComment());
        verify(authentication).getName();
    }

    @Test
    void testUpdateMyReviewForEvent_runtimeExceptionFromService() {
        ReviewDTO updateRequest = ReviewDTO.builder().rating(4).comment("Okayish").build();
        when(reviewService.updateReviewByUserIdAndEventId(eq(userId), eq(eventId), any(ReviewModel.class)))
                .thenThrow(new RuntimeException("Update failed"));
        mockAuthorities("Attendee");

        ReviewResponseDTO<ReviewDTO> response = reviewController.updateMyReviewForEvent(eventId, updateRequest, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Update failed", response.getMessage());
        assertNull(response.getData());
        verify(authentication).getName();
    }

    @Test
    void testDeleteMyReviewForEvent_success() {
        doNothing().when(reviewService).deleteReviewByUserIdAndEventId(userId, eventId);
        mockAuthorities("Attendee");

        ReviewResponseDTO<Void> response = reviewController.deleteMyReviewForEvent(eventId, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Review berhasil dihapus", response.getMessage());
        verify(authentication).getName();
    }

    @Test
    void testDeleteMyReviewForEvent_runtimeExceptionFromService() {
        doThrow(new RuntimeException("Delete failed")).when(reviewService).deleteReviewByUserIdAndEventId(userId, eventId);
        mockAuthorities("Attendee");

        ReviewResponseDTO<Void> response = reviewController.deleteMyReviewForEvent(eventId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Delete failed", response.getMessage());
        verify(authentication).getName();
    }

    @Test
    void testGetMyReviews_success() {
        List<ReviewModel> myReviewsList = Collections.singletonList(reviewModel);
        when(reviewService.getReviewsByUserId(userId)).thenReturn(myReviewsList);
        mockAuthorities("Attendee");

        ReviewResponseDTO<List<ReviewDTO>> response = reviewController.getMyReviews(authentication);

        assertTrue(response.isSuccess());
        assertEquals("Daftar semua review Anda", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(reviewId, response.getData().get(0).getId());
        verify(authentication).getName();
    }

    @Test
    void testGetMyReviews_emptyList() {
        when(reviewService.getReviewsByUserId(userId)).thenReturn(Collections.emptyList());
        mockAuthorities("Attendee");

        ReviewResponseDTO<List<ReviewDTO>> response = reviewController.getMyReviews(authentication);

        assertTrue(response.isSuccess());
        assertEquals("Daftar semua review Anda", response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
        verify(authentication).getName();
    }

    @Test
    void testGetReviewsByEventId_success() {

        List<ReviewModel> eventReviewsList = Collections.singletonList(reviewModel);
        when(reviewService.getReviewsByEventId(eventId)).thenReturn(eventReviewsList);

        ReviewResponseDTO<ReviewsByEventResponseDTO> response = reviewController.getReviewsByEventId(eventId);

        assertTrue(response.isSuccess());
        assertEquals("Daftar review untuk event " + eventId, response.getMessage());
        assertNotNull(response.getData());
        assertEquals(eventId, response.getData().getEventId());
        assertEquals(1, response.getData().getReviews().size());
        assertEquals(reviewId, response.getData().getReviews().get(0).getId());
    }

    @Test
    void testGetReviewsByEventId_emptyList() {
        when(reviewService.getReviewsByEventId(eventId)).thenReturn(Collections.emptyList());

        ReviewResponseDTO<ReviewsByEventResponseDTO> response = reviewController.getReviewsByEventId(eventId);

        assertTrue(response.isSuccess());
        assertEquals("Daftar review untuk event " + eventId, response.getMessage());
        assertNotNull(response.getData());
        assertEquals(eventId, response.getData().getEventId());
        assertTrue(response.getData().getReviews().isEmpty());
    }

    @Test
    void testGetReviewsForOrganizer_success() {
        List<ReviewModel> organizerReviewsList = Collections.singletonList(reviewModel);
        when(reviewService.getReviewsForOrganizer(eventId, userId)).thenReturn(organizerReviewsList);
        mockAuthorities("Organizer");

        ReviewResponseDTO<ReviewsByEventResponseDTO> response = reviewController.getReviewsForOrganizer(eventId, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Daftar review untuk organizer", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(eventId, response.getData().getEventId());
        assertEquals(1, response.getData().getReviews().size());
        verify(authentication).getName();
    }

    @Test
    void testGetReviewsForOrganizer_emptyList() {
        when(reviewService.getReviewsForOrganizer(eventId, userId)).thenReturn(Collections.emptyList());
        mockAuthorities("Organizer");

        ReviewResponseDTO<ReviewsByEventResponseDTO> response = reviewController.getReviewsForOrganizer(eventId, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Daftar review untuk organizer", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(eventId, response.getData().getEventId());
        assertTrue(response.getData().getReviews().isEmpty());
        verify(authentication).getName();
    }

    @Test
    void testGetAverageRating_success() {

        Double avgRating = 4.5;
        when(reviewService.calculateEventAverageRating(eventId)).thenReturn(avgRating);

        ReviewResponseDTO<AverageRatingResponseDTO> response = reviewController.getAverageRating(eventId);

        assertTrue(response.isSuccess());
        assertEquals("Rata-rata rating untuk event " + eventId, response.getMessage());
        assertNotNull(response.getData());
        assertEquals(eventId, response.getData().getEventId());
        assertEquals(avgRating, response.getData().getAverageRating());
    }

    @Test
    void testFlagReview_success() {
        ReviewModel flaggedModel = ReviewModel.builder().id(reviewId).status(ReviewStatus.FLAGGED).build();
        mockAuthorities("Organizer");
        when(reviewService.flagReview(reviewId, "Organizer")).thenReturn(flaggedModel);

        ReviewResponseDTO<ReviewDTO> response = reviewController.flagReview(reviewId, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Review berhasil di-flag.", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(ReviewStatus.FLAGGED, response.getData().getStatus());
        verify(authentication).getAuthorities();
    }

    @Test
    void testFlagReview_securityException() {
        mockAuthorities("Organizer");
        when(reviewService.flagReview(reviewId, "Organizer")).thenThrow(new SecurityException("Auth error"));

        ReviewResponseDTO<ReviewDTO> response = reviewController.flagReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Auth error", response.getMessage());
        assertNull(response.getData());
        verify(authentication).getAuthorities();
    }

    @Test
    void testFlagReview_illegalArgumentException() {
        mockAuthorities("Organizer");
        when(reviewService.flagReview(reviewId, "Organizer")).thenThrow(new IllegalArgumentException("Not found"));

        ReviewResponseDTO<ReviewDTO> response = reviewController.flagReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Not found", response.getMessage());
        assertNull(response.getData());
        verify(authentication).getAuthorities();
    }

    @Test
    void testFlagReview_illegalStateException() {
        mockAuthorities("Organizer");
        when(reviewService.flagReview(reviewId, "Organizer")).thenThrow(new IllegalStateException("Wrong state"));

        ReviewResponseDTO<ReviewDTO> response = reviewController.flagReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Wrong state", response.getMessage());
        assertNull(response.getData());
        verify(authentication).getAuthorities();
    }

    @Test
    void testFlagReview_noAuthorities_orElseEmptyRole() {
        mockAuthorities();
        when(reviewService.flagReview(reviewId, "")).thenThrow(new SecurityException("Role is required"));

        ReviewResponseDTO<ReviewDTO> response = reviewController.flagReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Role is required", response.getMessage());
        verify(authentication).getAuthorities();
        verify(reviewService).flagReview(reviewId, "");
    }

    @Test
    void testGetFlaggedReviews_success() {
        ReviewModel flaggedReviewModel = ReviewModel.builder().id(UUID.randomUUID()).status(ReviewStatus.FLAGGED).build();
        List<ReviewModel> flaggedList = Collections.singletonList(flaggedReviewModel);
        when(reviewService.getReviewsByStatus(ReviewStatus.FLAGGED)).thenReturn(flaggedList);
        mockAuthorities("Admin");

        ReviewResponseDTO<List<ReviewDTO>> response = reviewController.getFlaggedReviews();

        assertTrue(response.isSuccess());
        assertEquals("Daftar review dengan status FLAGGED", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(ReviewStatus.FLAGGED, response.getData().get(0).getStatus());

    }

    @Test
    void testGetFlaggedReviews_emptyList() {
        when(reviewService.getReviewsByStatus(ReviewStatus.FLAGGED)).thenReturn(Collections.emptyList());
        mockAuthorities("Admin");

        ReviewResponseDTO<List<ReviewDTO>> response = reviewController.getFlaggedReviews();

        assertTrue(response.isSuccess());
        assertEquals("Daftar review dengan status FLAGGED", response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testCancelFlagReview_success() {
        ReviewModel approvedModel = ReviewModel.builder().id(reviewId).status(ReviewStatus.APPROVED).build();
        mockAuthorities("Organizer");
        when(reviewService.cancelFlag(reviewId, "Organizer")).thenReturn(approvedModel);

        ReviewResponseDTO<ReviewDTO> response = reviewController.cancelFlagReview(reviewId, authentication);

        assertTrue(response.isSuccess());
        assertEquals("Flag review berhasil dibatalkan.", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(ReviewStatus.APPROVED, response.getData().getStatus());
        verify(authentication).getAuthorities();
    }

    @Test
    void testCancelFlagReview_securityException() {
        mockAuthorities("Organizer");
        when(reviewService.cancelFlag(reviewId, "Organizer")).thenThrow(new SecurityException("Auth error"));

        ReviewResponseDTO<ReviewDTO> response = reviewController.cancelFlagReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Auth error", response.getMessage());
        assertNull(response.getData());
        verify(authentication).getAuthorities();
    }

    @Test
    void testCancelFlagReview_illegalArgumentException() {
        mockAuthorities("Organizer");
        when(reviewService.cancelFlag(reviewId, "Organizer")).thenThrow(new IllegalArgumentException("Not found"));

        ReviewResponseDTO<ReviewDTO> response = reviewController.cancelFlagReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Not found", response.getMessage());
        assertNull(response.getData());
        verify(authentication).getAuthorities();
    }

    @Test
    void testCancelFlagReview_illegalStateException() {
        mockAuthorities("Organizer");
        when(reviewService.cancelFlag(reviewId, "Organizer")).thenThrow(new IllegalStateException("Wrong state"));

        ReviewResponseDTO<ReviewDTO> response = reviewController.cancelFlagReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Wrong state", response.getMessage());
        assertNull(response.getData());
        verify(authentication).getAuthorities();
    }

    @Test
    void testCancelFlagReview_noAuthorities_orElseEmptyRole() {
        mockAuthorities();
        when(reviewService.cancelFlag(reviewId, "")).thenThrow(new SecurityException("Role is required"));

        ReviewResponseDTO<ReviewDTO> response = reviewController.cancelFlagReview(reviewId, authentication);

        assertFalse(response.isSuccess());
        assertEquals("Role is required", response.getMessage());
        verify(authentication).getAuthorities();
        verify(reviewService).cancelFlag(reviewId, "");
    }


    @Test
    void testCreateReview_dtoWithNullFields_shouldHandlePartialData() {
        ReviewDTO requestDTO = ReviewDTO.builder()
                .eventId(eventId)
                .rating(null)
                .comment(null)
                .build();
        mockAuthorities("Attendee");

        ReviewModel modelFromDTO = ReviewModel.builder()
                .eventId(eventId)
                .userId(userId)
                .rating(null)
                .comment(null)
                .status(ReviewStatus.APPROVED)
                .build();

        ReviewModel savedModel = ReviewModel.builder()
                .id(reviewId)
                .eventId(eventId)
                .userId(userId)
                .rating(null)
                .comment(null)
                .createdDate(LocalDateTime.now())
                .status(ReviewStatus.APPROVED)
                .build();

        when(reviewService.createReview(any(ReviewModel.class)))
                .thenReturn(CompletableFuture.completedFuture(savedModel));

        ReviewResponseDTO<ReviewDTO> response = reviewController.createReview(requestDTO, authentication);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
    }
}