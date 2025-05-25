//package id.ac.ui.cs.advprog.review.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import id.ac.ui.cs.advprog.review.dto.ReviewDTO;
//import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
//import id.ac.ui.cs.advprog.review.model.ReviewModel;
//import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
//import id.ac.ui.cs.advprog.review.service.ReviewService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(ReviewController.class)
//class ReviewControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    // Gunakan @MockBean untuk mock Spring Beans secara otomatis di context test
//    @MockBean
//    private ReviewService reviewService;
//
//    @MockBean
//    private ReviewRepository reviewRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private UUID userId;
//    private UUID eventId;
//    private UUID organizerId;
//    private UUID reviewId;
//    private ReviewModel reviewModel;
//    private ReviewDTO reviewDTO;
//
//    @BeforeEach
//    void setUp() {
//        userId = UUID.randomUUID();
//        eventId = UUID.randomUUID();
//        organizerId = UUID.randomUUID();
//        reviewId = UUID.randomUUID();
//
//        reviewModel = ReviewModel.builder()
//                .id(reviewId)
//                .eventId(eventId)
//                .userId(userId)
//                .organizerId(organizerId)
//                .rating(5)
//                .comment("Great event!")
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .status(ReviewStatus.APPROVED)
//                .build();
//
//        reviewDTO = ReviewDTO.builder()
//                .id(reviewId)
//                .eventId(eventId)
//                .userId(userId)
//                .organizerId(organizerId)
//                .rating(5)
//                .comment("Great event!")
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .status(ReviewStatus.APPROVED)
//                .build();
//    }
//
//    private Authentication createMockAuthentication(String role, String name) {
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn(name);
//
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(role));
//        when(auth.getAuthorities()).thenReturn(authorities);
//
//        return auth;
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void createReview_Success() throws Exception {
//        ReviewDTO requestDTO = ReviewDTO.builder()
//                .eventId(eventId)
//                .organizerId(organizerId)
//                .rating(5)
//                .comment("Great event!")
//                .build();
//
//        when(reviewService.createReview(any(ReviewModel.class)))
//                .thenReturn(CompletableFuture.completedFuture(reviewModel));
//
//        Authentication auth = createMockAuthentication("Attendee", userId.toString());
//
//        mockMvc.perform(post("/api/reviews")
//                        .with(authentication(auth))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil dibuat"))
//                .andExpect(jsonPath("$.data.id").value(reviewId.toString()))
//                .andExpect(jsonPath("$.data.rating").value(5))
//                .andExpect(jsonPath("$.data.comment").value("Great event!"));
//
//        verify(reviewService).createReview(any(ReviewModel.class));
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void createReview_WithNullStatus_SetsApprovedStatus() throws Exception {
//        ReviewDTO requestDTO = ReviewDTO.builder()
//                .eventId(eventId)
//                .organizerId(organizerId)
//                .rating(5)
//                .comment("Great event!")
//                .status(null)
//                .build();
//
//        ReviewModel modelWithApprovedStatus = ReviewModel.builder()
//                .id(reviewId)
//                .eventId(eventId)
//                .userId(userId)
//                .organizerId(organizerId)
//                .rating(5)
//                .comment("Great event!")
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .status(ReviewStatus.APPROVED)
//                .build();
//
//        when(reviewService.createReview(any(ReviewModel.class)))
//                .thenReturn(CompletableFuture.completedFuture(modelWithApprovedStatus));
//
//        Authentication auth = createMockAuthentication("Attendee", userId.toString());
//
//        mockMvc.perform(post("/api/reviews")
//                        .with(authentication(auth))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data.status").value("APPROVED"));
//
//        verify(reviewService).createReview(argThat(model ->
//                model.getStatus() == ReviewStatus.APPROVED));
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void createReview_WithNullRequestDTO_HandlesNull() throws Exception {
//        when(reviewService.createReview(any()))
//                .thenReturn(CompletableFuture.completedFuture(reviewModel));
//
//        Authentication auth = createMockAuthentication("Attendee", userId.toString());
//
//        mockMvc.perform(post("/api/reviews")
//                        .with(authentication(auth))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true));
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void updateReview_Success() throws Exception {
//        ReviewDTO updateRequest = ReviewDTO.builder()
//                .rating(4)
//                .comment("Updated comment")
//                .build();
//
//        when(reviewRepository.findById(reviewId)).thenReturn(reviewModel);
//        when(reviewService.updateReview(any(ReviewModel.class))).thenReturn(reviewModel);
//
//        Authentication auth = createMockAuthentication("Attendee", userId.toString());
//
//        mockMvc.perform(put("/api/reviews/update/{id}", reviewId)
//                        .with(authentication(auth))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil diperbarui"));
//
//        verify(reviewRepository).findById(reviewId);
//        verify(reviewService).updateReview(any(ReviewModel.class));
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void updateReview_ReviewNotFound() throws Exception {
//        ReviewDTO updateRequest = ReviewDTO.builder()
//                .rating(4)
//                .comment("Updated comment")
//                .build();
//
//        when(reviewRepository.findById(reviewId)).thenReturn(null);
//
//        Authentication auth = createMockAuthentication("Attendee", userId.toString());
//
//        mockMvc.perform(put("/api/reviews/update/{id}", reviewId)
//                        .with(authentication(auth))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Review dengan ID " + reviewId + " tidak ditemukan"))
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(reviewRepository).findById(reviewId);
//        verify(reviewService, never()).updateReview(any());
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void updateReview_NotOwner() throws Exception {
//        UUID differentUserId = UUID.randomUUID();
//        ReviewDTO updateRequest = ReviewDTO.builder()
//                .rating(4)
//                .comment("Updated comment")
//                .build();
//
//        when(reviewRepository.findById(reviewId)).thenReturn(reviewModel);
//
//        Authentication auth = createMockAuthentication("Attendee", differentUserId.toString());
//
//        mockMvc.perform(put("/api/reviews/update/{id}", reviewId)
//                        .with(authentication(auth))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Tidak bisa memperbarui review milik orang lain"));
//
//        verify(reviewRepository).findById(reviewId);
//        verify(reviewService, never()).updateReview(any());
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void updateReview_WithNullFields() throws Exception {
//        ReviewDTO updateRequest = ReviewDTO.builder()
//                .rating(null)
//                .comment(null)
//                .build();
//
//        when(reviewRepository.findById(reviewId)).thenReturn(reviewModel);
//        when(reviewService.updateReview(any(ReviewModel.class))).thenReturn(reviewModel);
//
//        Authentication auth = createMockAuthentication("Attendee", userId.toString());
//
//        mockMvc.perform(put("/api/reviews/update/{id}", reviewId)
//                        .with(authentication(auth))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//
//        verify(reviewService).updateReview(argThat(model ->
//                model.getRating().equals(reviewModel.getRating()) &&
//                        model.getComment().equals(reviewModel.getComment())));
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void deleteReview_AsOwner_Success() throws Exception {
//        when(reviewRepository.findById(reviewId)).thenReturn(reviewModel);
//        doNothing().when(reviewService).deleteReview(reviewId);
//
//        Authentication auth = createMockAuthentication("Attendee", userId.toString());
//
//        mockMvc.perform(delete("/api/reviews/delete/{id}", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil dihapus"));
//
//        verify(reviewRepository).findById(reviewId);
//        verify(reviewService).deleteReview(reviewId);
//    }
//
//    @Test
//    @WithMockUser(authorities = "Admin")
//    void deleteReview_AsAdmin_Success() throws Exception {
//        when(reviewRepository.findById(reviewId)).thenReturn(reviewModel);
//        doNothing().when(reviewService).deleteReview(reviewId);
//
//        Authentication auth = createMockAuthentication("Admin", "admin-id");
//
//        mockMvc.perform(delete("/api/reviews/delete/{id}", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil dihapus"));
//
//        verify(reviewRepository).findById(reviewId);
//        verify(reviewService).deleteReview(reviewId);
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void deleteReview_ReviewNotFound() throws Exception {
//        when(reviewRepository.findById(reviewId)).thenReturn(null);
//
//        Authentication auth = createMockAuthentication("Attendee", userId.toString());
//
//        mockMvc.perform(delete("/api/reviews/delete/{id}", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Review dengan ID " + reviewId + " tidak ditemukan"));
//
//        verify(reviewRepository).findById(reviewId);
//        verify(reviewService, never()).deleteReview(any());
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void deleteReview_NotOwnerNotAdmin() throws Exception {
//        UUID differentUserId = UUID.randomUUID();
//        when(reviewRepository.findById(reviewId)).thenReturn(reviewModel);
//
//        Authentication auth = createMockAuthentication("Attendee", differentUserId.toString());
//
//        mockMvc.perform(delete("/api/reviews/delete/{id}", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Tidak bisa menghapus review milik orang lain"));
//
//        verify(reviewRepository).findById(reviewId);
//        verify(reviewService, never()).deleteReview(any());
//    }
//
//    @Test
//    void getReviewsByEventId_Success() throws Exception {
//        List<ReviewModel> reviews = Arrays.asList(reviewModel);
//        when(reviewService.getReviewsByEventId(eventId)).thenReturn(reviews);
//
//        mockMvc.perform(get("/api/reviews/event/{eventId}", eventId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Daftar review untuk event " + eventId))
//                .andExpect(jsonPath("$.data.eventId").value(eventId.toString()))
//                .andExpect(jsonPath("$.data.reviews").isArray())
//                .andExpect(jsonPath("$.data.reviews[0].id").value(reviewId.toString()));
//
//        verify(reviewService).getReviewsByEventId(eventId);
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void getReviewsForOrganizer_Success() throws Exception {
//        List<ReviewModel> reviews = Arrays.asList(reviewModel);
//        when(reviewService.getReviewsForOrganizer(eventId, organizerId)).thenReturn(reviews);
//
//        Authentication auth = createMockAuthentication("Organizer", organizerId.toString());
//
//        mockMvc.perform(get("/api/reviews/event-reviews/my/{eventId}", eventId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Daftar review untuk organizer"))
//                .andExpect(jsonPath("$.data.eventId").value(eventId.toString()))
//                .andExpect(jsonPath("$.data.reviews").isArray());
//
//        verify(reviewService).getReviewsForOrganizer(eventId, organizerId);
//    }
//
//    @Test
//    void getAverageRating_Success() throws Exception {
//        Double averageRating = 4.5;
//        when(reviewService.calculateEventAverageRating(eventId)).thenReturn(averageRating);
//
//        mockMvc.perform(get("/api/reviews/event/{eventId}/average", eventId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Rata-rata rating untuk event " + eventId))
//                .andExpect(jsonPath("$.data.eventId").value(eventId.toString()))
//                .andExpect(jsonPath("$.data.averageRating").value(4.5));
//
//        verify(reviewService).calculateEventAverageRating(eventId);
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void flagReview_Success() throws Exception {
//        ReviewModel flaggedReview = ReviewModel.builder()
//                .id(reviewId)
//                .eventId(eventId)
//                .userId(userId)
//                .organizerId(organizerId)
//                .rating(5)
//                .comment("Great event!")
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .status(ReviewStatus.FLAGGED)
//                .build();
//
//        when(reviewService.flagReview(reviewId, "Organizer")).thenReturn(flaggedReview);
//
//        Authentication auth = createMockAuthentication("Organizer", organizerId.toString());
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil di-flag."))
//                .andExpect(jsonPath("$.data.status").value("FLAGGED"));
//
//        verify(reviewService).flagReview(reviewId, "Organizer");
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void flagReview_SecurityException() throws Exception {
//        when(reviewService.flagReview(reviewId, "Organizer"))
//                .thenThrow(new SecurityException("Security error"));
//
//        Authentication auth = createMockAuthentication("Organizer", organizerId.toString());
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Security error"))
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(reviewService).flagReview(reviewId, "Organizer");
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void flagReview_IllegalArgumentException() throws Exception {
//        when(reviewService.flagReview(reviewId, "Organizer"))
//                .thenThrow(new IllegalArgumentException("Invalid argument"));
//
//        Authentication auth = createMockAuthentication("Organizer", organizerId.toString());
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid argument"))
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(reviewService).flagReview(reviewId, "Organizer");
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void flagReview_IllegalStateException() throws Exception {
//        when(reviewService.flagReview(reviewId, "Organizer"))
//                .thenThrow(new IllegalStateException("Invalid state"));
//
//        Authentication auth = createMockAuthentication("Organizer", organizerId.toString());
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid state"))
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(reviewService).flagReview(reviewId, "Organizer");
//    }
//
//    @Test
//    @WithMockUser(authorities = "Admin")
//    void getFlaggedReviews_Success() throws Exception {
//        ReviewModel flaggedReview = ReviewModel.builder()
//                .id(reviewId)
//                .eventId(eventId)
//                .userId(userId)
//                .organizerId(organizerId)
//                .rating(5)
//                .comment("Flagged comment")
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .status(ReviewStatus.FLAGGED)
//                .build();
//
//        List<ReviewModel> flaggedReviews = Collections.singletonList(flaggedReview);
//        when(reviewService.getReviewsByStatus(ReviewStatus.FLAGGED)).thenReturn(flaggedReviews);
//
//        mockMvc.perform(get("/api/reviews/flagged"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Daftar review dengan status FLAGGED"))
//                .andExpect(jsonPath("$.data").isArray())
//                .andExpect(jsonPath("$.data[0].status").value("FLAGGED"));
//
//        verify(reviewService).getReviewsByStatus(ReviewStatus.FLAGGED);
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void cancelFlagReview_Success() throws Exception {
//        ReviewModel unflaggedReview = ReviewModel.builder()
//                .id(reviewId)
//                .eventId(eventId)
//                .userId(userId)
//                .organizerId(organizerId)
//                .rating(5)
//                .comment("Great event!")
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .status(ReviewStatus.APPROVED)
//                .build();
//
//        when(reviewService.cancelFlag(reviewId, "Organizer")).thenReturn(unflaggedReview);
//
//        Authentication auth = createMockAuthentication("Organizer", organizerId.toString());
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/cancel-flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Flag review berhasil dibatalkan."))
//                .andExpect(jsonPath("$.data.status").value("APPROVED"));
//
//        verify(reviewService).cancelFlag(reviewId, "Organizer");
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void cancelFlagReview_SecurityException() throws Exception {
//        when(reviewService.cancelFlag(reviewId, "Organizer"))
//                .thenThrow(new SecurityException("Security error"));
//
//        Authentication auth = createMockAuthentication("Organizer", organizerId.toString());
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/cancel-flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Security error"))
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(reviewService).cancelFlag(reviewId, "Organizer");
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void cancelFlagReview_IllegalArgumentException() throws Exception {
//        when(reviewService.cancelFlag(reviewId, "Organizer"))
//                .thenThrow(new IllegalArgumentException("Invalid argument"));
//
//        Authentication auth = createMockAuthentication("Organizer", organizerId.toString());
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/cancel-flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid argument"))
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(reviewService).cancelFlag(reviewId, "Organizer");
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void cancelFlagReview_IllegalStateException() throws Exception {
//        when(reviewService.cancelFlag(reviewId, "Organizer"))
//                .thenThrow(new IllegalStateException("Invalid state"));
//
//        Authentication auth = createMockAuthentication("Organizer", organizerId.toString());
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/cancel-flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid state"))
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(reviewService).cancelFlag(reviewId, "Organizer");
//    }
//
//    @Test
//    @WithMockUser(authorities = "Attendee")
//    void toEntity_WithNullDTO() throws Exception {
//        when(reviewService.createReview(isNull()))
//                .thenReturn(CompletableFuture.completedFuture(reviewModel));
//
//        Authentication auth = createMockAuthentication("Attendee", userId.toString());
//
//        mockMvc.perform(post("/api/reviews")
//                        .with(authentication(auth))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("null"))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    void toDTO_WithEmptyList() throws Exception {
//        when(reviewService.getReviewsByEventId(eventId)).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(get("/api/reviews/event/{eventId}", eventId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.reviews").isEmpty());
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void flagReview_EmptyAuthorities() throws Exception {
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn(organizerId.toString());
//        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
//
//        when(reviewService.flagReview(reviewId, "")).thenReturn(reviewModel);
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//
//        verify(reviewService).flagReview(reviewId, "");
//    }
//
//    @Test
//    @WithMockUser(authorities = "Organizer")
//    void cancelFlagReview_EmptyAuthorities() throws Exception {
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn(organizerId.toString());
//        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
//
//        when(reviewService.cancelFlag(reviewId, "")).thenReturn(reviewModel);
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/cancel-flag", reviewId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//
//        verify(reviewService).cancelFlag(reviewId, "");
//    }
//}
