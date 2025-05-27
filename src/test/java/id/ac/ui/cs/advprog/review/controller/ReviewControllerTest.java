//package id.ac.ui.cs.advprog.review.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import id.ac.ui.cs.advprog.review.dto.*;
//import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
//import id.ac.ui.cs.advprog.review.model.ReviewModel;
//import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
//import id.ac.ui.cs.advprog.review.service.ReviewService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReviewControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private ReviewService reviewService;
//
//    @Mock
//    private ReviewRepository reviewRepository;
//
//    @InjectMocks
//    private ReviewController reviewController;
//
//    private ObjectMapper objectMapper;
//    private UUID testUserId;
//    private UUID testEventId;
//    private UUID testReviewId;
//    private UUID testOrganizerId;
//    private ReviewModel testReviewModel;
//    private ReviewDTO testReviewDTO;
//
//    @BeforeEach
//    void setUp() {
//
//        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
//                .build();
//
//        objectMapper = new ObjectMapper();
//
//        testUserId = UUID.randomUUID();
//        testEventId = UUID.randomUUID();
//        testReviewId = UUID.randomUUID();
//        testOrganizerId = UUID.randomUUID();
//
//        testReviewModel = ReviewModel.builder()
//                .id(testReviewId)
//                .eventId(testEventId)
//                .userId(testUserId)
//                .organizerId(testOrganizerId)
//                .rating(5)
//                .comment("Great event!")
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .status(ReviewStatus.APPROVED)
//                .build();
//
//        testReviewDTO = ReviewDTO.builder()
//                .id(testReviewId)
//                .eventId(testEventId)
//                .userId(testUserId)
//                .organizerId(testOrganizerId)
//                .rating(5)
//                .comment("Great event!")
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .status(ReviewStatus.APPROVED)
//                .build();
//    }
//
//    private Authentication createMockAuth(String userId, String roles) {
//        Authentication auth = mock(Authentication.class);
//        doReturn(userId).when(auth).getName();
//        doReturn(true).when(auth).isAuthenticated();
//
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        if (roles != null && !roles.isBlank()) {
//            String[] splitRoles = roles.split("\\s*,\\s*");
//            for (String role : splitRoles) {
//                grantedAuthorities.add(new SimpleGrantedAuthority(role));
//            }
//        }
//
//        doReturn(grantedAuthorities).when(auth).getAuthorities();
//
//        SecurityContext securityContext = mock(SecurityContext.class);
//        doReturn(auth).when(securityContext).getAuthentication();
//        SecurityContextHolder.setContext(securityContext);
//
//        return auth;
//    }
//
//    private ReviewModel copyWithStatus(ReviewModel original, ReviewStatus newStatus) {
//        return ReviewModel.builder()
//                .id(original.getId())
//                .eventId(original.getEventId())
//                .userId(original.getUserId())
//                .organizerId(original.getOrganizerId())
//                .rating(original.getRating())
//                .comment(original.getComment())
//                .createdDate(original.getCreatedDate())
//                .updatedDate(original.getUpdatedDate())
//                .status(newStatus)
//                .build();
//    }
//
//    @Test
//    void createReview_Success() throws Exception {
//        ReviewDTO requestDTO = ReviewDTO.builder()
//                .eventId(testEventId)
//                .rating(5)
//                .comment("Great event!")
//                .build();
//
//        when(reviewService.createReview(any(ReviewModel.class)))
//                .thenReturn(CompletableFuture.completedFuture(testReviewModel));
//
//        Authentication auth = createMockAuth(testUserId.toString(), "Attendee");
//
//        mockMvc.perform(post("/api/reviews")
//                        .with(authentication(auth))
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil dibuat"))
//                .andExpect(jsonPath("$.data.id").value(testReviewId.toString()))
//                .andExpect(jsonPath("$.data.rating").value(5))
//                .andExpect(jsonPath("$.data.comment").value("Great event!"));
//
//        verify(reviewService).createReview(any(ReviewModel.class));
//    }
//
//    @Test
//    void createReview_WithNullStatus_SetsApproved() throws Exception {
//        ReviewDTO requestDTO = ReviewDTO.builder()
//                .eventId(testEventId)
//                .rating(5)
//                .comment("Great event!")
//                .build();
//
//        ReviewModel savedModel = ReviewModel.builder()
//                .id(testReviewId)
//                .eventId(testEventId)
//                .userId(testUserId)
//                .rating(5)
//                .comment("Great event!")
//                .status(ReviewStatus.APPROVED)
//                .createdDate(LocalDateTime.now())
//                .updatedDate(LocalDateTime.now())
//                .build();
//
//        when(reviewService.createReview(argThat(model ->
//                model.getStatus() == ReviewStatus.APPROVED)))
//                .thenReturn(CompletableFuture.completedFuture(savedModel));
//
//        Authentication auth = createMockAuth(testUserId.toString(), "Attendee");
//
//        mockMvc.perform(post("/api/reviews")
//                        .with(authentication(auth))
//                        .with(csrf())
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
//    void updateReview_Success() throws Exception {
//        ReviewDTO updateDTO = ReviewDTO.builder()
//                .rating(4)
//                .comment("Updated comment")
//                .build();
//
//        when(reviewRepository.findById(testReviewId)).thenReturn(testReviewModel);
//        when(reviewService.updateReview(any(ReviewModel.class))).thenReturn(testReviewModel);
//
//        Authentication auth = createMockAuth(testUserId.toString(), "Attendee");
//
//        mockMvc.perform(put("/api/reviews/update/{id}", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil diperbarui"));
//
//        verify(reviewRepository).findById(testReviewId);
//        verify(reviewService).updateReview(any(ReviewModel.class));
//    }
//
//    @Test
//    void updateReview_ReviewNotFound() throws Exception {
//        ReviewDTO updateDTO = ReviewDTO.builder()
//                .rating(4)
//                .comment("Updated comment")
//                .build();
//
//        when(reviewRepository.findById(testReviewId)).thenReturn(null);
//        Authentication auth = createMockAuth(testUserId.toString(), "Attendee");
//
//        mockMvc.perform(put("/api/reviews/update/{id}", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Review dengan ID " + testReviewId + " tidak ditemukan"));
//
//        verify(reviewRepository).findById(testReviewId);
//        verify(reviewService, never()).updateReview(any());
//    }
//
//    @Test
//    void updateReview_NotOwner() throws Exception {
//        UUID differentUserId = UUID.randomUUID();
//        ReviewDTO updateDTO = ReviewDTO.builder()
//                .rating(4)
//                .comment("Updated comment")
//                .build();
//
//        when(reviewRepository.findById(testReviewId)).thenReturn(testReviewModel);
//        Authentication auth = createMockAuth(differentUserId.toString(), "Attendee");
//
//        mockMvc.perform(put("/api/reviews/update/{id}", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Tidak bisa memperbarui review milik orang lain"));
//
//        verify(reviewService, never()).updateReview(any());
//    }
//
//    @Test
//    void updateReview_WithNullValues() throws Exception {
//        ReviewDTO updateDTO = ReviewDTO.builder()
//                .rating(null)
//                .comment(null)
//                .build();
//
//        when(reviewRepository.findById(testReviewId)).thenReturn(testReviewModel);
//        when(reviewService.updateReview(any(ReviewModel.class))).thenReturn(testReviewModel);
//
//        Authentication auth = createMockAuth(testUserId.toString(), "Attendee");
//
//        mockMvc.perform(put("/api/reviews/update/{id}", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//
//        verify(reviewService).updateReview(any(ReviewModel.class));
//    }
//
//    @Test
//    void deleteReview_AsOwner_Success() throws Exception {
//        when(reviewRepository.findById(testReviewId)).thenReturn(testReviewModel);
//        Authentication auth = createMockAuth(testUserId.toString(), "Attendee");
//
//        mockMvc.perform(delete("/api/reviews/delete/{id}", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil dihapus"));
//
//        verify(reviewService).deleteReview(testReviewId);
//    }
//
//    @Test
//    void deleteReview_AsAdmin_Success() throws Exception {
//        when(reviewRepository.findById(testReviewId)).thenReturn(testReviewModel);
//        Authentication auth = createMockAuth(UUID.randomUUID().toString(), "Admin");
//
//        mockMvc.perform(delete("/api/reviews/delete/{id}", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil dihapus"));
//
//        verify(reviewService).deleteReview(testReviewId);
//    }
//
//    @Test
//    void deleteReview_ReviewNotFound() throws Exception {
//        when(reviewRepository.findById(testReviewId)).thenReturn(null);
//        Authentication auth = createMockAuth(testUserId.toString(), "Attendee");
//
//        mockMvc.perform(delete("/api/reviews/delete/{id}", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Review dengan ID " + testReviewId + " tidak ditemukan"));
//
//        verify(reviewService, never()).deleteReview(any());
//    }
//
//    @Test
//    void deleteReview_NotOwnerNotAdmin() throws Exception {
//        UUID differentUserId = UUID.randomUUID();
//        when(reviewRepository.findById(testReviewId)).thenReturn(testReviewModel);
//        Authentication auth = createMockAuth(differentUserId.toString(), "Attendee");
//
//        mockMvc.perform(delete("/api/reviews/delete/{id}", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Tidak bisa menghapus review milik orang lain"));
//
//        verify(reviewService, never()).deleteReview(any());
//    }
//
//    @Test
//    void getReviewsByEventId_Success() throws Exception {
//        List<ReviewModel> reviews = Collections.singletonList(testReviewModel);
//        when(reviewService.getReviewsByEventId(testEventId)).thenReturn(reviews);
//
//        mockMvc.perform(get("/api/reviews/event/{eventId}", testEventId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Daftar review untuk event " + testEventId))
//                .andExpect(jsonPath("$.data.eventId").value(testEventId.toString()))
//                .andExpect(jsonPath("$.data.reviews").isArray())
//                .andExpect(jsonPath("$.data.reviews[0].id").value(testReviewId.toString()));
//
//        verify(reviewService).getReviewsByEventId(testEventId);
//    }
//
//    @Test
//    void getReviewsForOrganizer_Success() throws Exception {
//        List<ReviewModel> reviews = Collections.singletonList(testReviewModel);
//        when(reviewService.getReviewsForOrganizer(testEventId, testOrganizerId)).thenReturn(reviews);
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer");
//
//        mockMvc.perform(get("/api/reviews/event-reviews/my/{eventId}", testEventId)
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Daftar review untuk organizer"))
//                .andExpect(jsonPath("$.data.eventId").value(testEventId.toString()))
//                .andExpect(jsonPath("$.data.reviews").isArray());
//
//        verify(reviewService).getReviewsForOrganizer(testEventId, testOrganizerId);
//    }
//
//    @Test
//    void getAverageRating_Success() throws Exception {
//        Double averageRating = 4.5;
//        when(reviewService.calculateEventAverageRating(testEventId)).thenReturn(averageRating);
//
//        mockMvc.perform(get("/api/reviews/event/{eventId}/average", testEventId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Rata-rata rating untuk event " + testEventId))
//                .andExpect(jsonPath("$.data.eventId").value(testEventId.toString()))
//                .andExpect(jsonPath("$.data.averageRating").value(4.5));
//
//        verify(reviewService).calculateEventAverageRating(testEventId);
//    }
//
//    @Test
//    void flagReview_Success() throws Exception {
//        ReviewModel flaggedReview = copyWithStatus(testReviewModel, ReviewStatus.FLAGGED);
//
//        when(reviewService.flagReview(testReviewId, "Organizer")).thenReturn(flaggedReview);
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer");
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Review berhasil di-flag."))
//                .andExpect(jsonPath("$.data.status").value("FLAGGED"));
//
//        verify(reviewService).flagReview(testReviewId, "Organizer");
//    }
//
//    @Test
//    void flagReview_SecurityException() throws Exception {
//        when(reviewService.flagReview(testReviewId, "Organizer"))
//                .thenThrow(new SecurityException("Access denied"));
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer");
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Access denied"))
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(reviewService).flagReview(testReviewId, "Organizer");
//    }
//
//    @Test
//    void flagReview_IllegalArgumentException() throws Exception {
//        when(reviewService.flagReview(testReviewId, "Organizer"))
//                .thenThrow(new IllegalArgumentException("Invalid argument"));
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer");
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid argument"));
//
//        verify(reviewService).flagReview(testReviewId, "Organizer");
//    }
//
//    @Test
//    void flagReview_IllegalStateException() throws Exception {
//        when(reviewService.flagReview(testReviewId, "Organizer"))
//                .thenThrow(new IllegalStateException("Invalid state"));
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer");
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid state"));
//
//        verify(reviewService).flagReview(testReviewId, "Organizer");
//    }
//
//    @Test
//    void getFlaggedReviews_Success() throws Exception {
//        ReviewModel flaggedReview = copyWithStatus(testReviewModel, ReviewStatus.FLAGGED);
//        List<ReviewModel> flaggedReviews = Collections.singletonList(flaggedReview);
//
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
//    void cancelFlagReview_Success() throws Exception {
//        ReviewModel unflaggedReview = copyWithStatus(testReviewModel, ReviewStatus.APPROVED);
//
//        when(reviewService.cancelFlag(testReviewId, "Organizer")).thenReturn(unflaggedReview);
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer");
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/cancel-flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Flag review berhasil dibatalkan."))
//                .andExpect(jsonPath("$.data.status").value("APPROVED"));
//
//        verify(reviewService).cancelFlag(testReviewId, "Organizer");
//    }
//
//    @Test
//    void cancelFlagReview_SecurityException() throws Exception {
//        when(reviewService.cancelFlag(testReviewId, "Organizer"))
//                .thenThrow(new SecurityException("Access denied"));
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer");
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/cancel-flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Access denied"));
//
//        verify(reviewService).cancelFlag(testReviewId, "Organizer");
//    }
//
//    @Test
//    void cancelFlagReview_IllegalArgumentException() throws Exception {
//        when(reviewService.cancelFlag(testReviewId, "Organizer"))
//                .thenThrow(new IllegalArgumentException("Invalid argument"));
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer");
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/cancel-flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid argument"));
//
//        verify(reviewService).cancelFlag(testReviewId, "Organizer");
//    }
//
//    @Test
//    void cancelFlagReview_IllegalStateException() throws Exception {
//        when(reviewService.cancelFlag(testReviewId, "Organizer"))
//                .thenThrow(new IllegalStateException("Invalid state"));
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer");
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/cancel-flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid state"));
//
//        verify(reviewService).cancelFlag(testReviewId, "Organizer");
//    }
//
//    @Test
//    void deleteReview_MultipleAuthorities() throws Exception {
//        when(reviewRepository.findById(testReviewId)).thenReturn(testReviewModel);
//
//        Authentication auth = createMockAuth(UUID.randomUUID().toString(), "Organizer,Admin");
//
//        mockMvc.perform(delete("/api/reviews/delete/{id}", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isNoContent())
//                .andExpect(jsonPath("$.success").value(true));
//
//        verify(reviewService).deleteReview(testReviewId);
//    }
//
//    @Test
//    void flagReview_MultipleAuthorities() throws Exception {
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "Organizer,Admin");
//
//        when(reviewService.flagReview(testReviewId, "Organizer")).thenReturn(testReviewModel);
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//
//        verify(reviewService).flagReview(testReviewId, "Organizer");
//    }
//
//    @Test
//    void flagReview_NoAuthorities() throws Exception {
//        Authentication auth = createMockAuth(testOrganizerId.toString(), "");
//
//        when(reviewService.flagReview(testReviewId, "")).thenReturn(testReviewModel);
//
//        mockMvc.perform(put("/api/reviews/{reviewId}/flag", testReviewId)
//                        .with(authentication(auth))
//                        .with(csrf()))
//                .andExpect(status().isOk());
//
//        verify(reviewService).flagReview(testReviewId, "");
//    }
//}
//
