package id.ac.ui.cs.advprog.review.controller;

import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ReviewModel review;
    private UUID reviewId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();

        reviewId = UUID.randomUUID();
        review = ReviewModel.builder()
                .id(reviewId)
                .eventId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .rating(5)
                .comment("Great event!")
                .createdDate(java.time.LocalDateTime.now())
                .updatedDate(java.time.LocalDateTime.now())
                .status(id.ac.ui.cs.advprog.review.enums.ReviewStatus.APPROVED)
                .build();
    }

    @Test
    public void testCreateReview() throws Exception {
        when(reviewService.createReview(any(ReviewModel.class))).thenReturn(review);

        mockMvc.perform(post("/api/reviews")
                        .contentType("application/json")
                        .content("{ \"eventId\": \"" + review.getEventId() + "\", \"userId\": \"" + review.getUserId() + "\", \"rating\": 5, \"comment\": \"Great event!\", \"status\": \"APPROVED\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(review.getId().toString()))
                .andExpect(jsonPath("$.comment").value("Great event!"));

        verify(reviewService, times(1)).createReview(any(ReviewModel.class));
    }

    @Test
    public void testUpdateReview() throws Exception {

        review.setComment("Updated review");

        when(reviewService.updateReview(any(ReviewModel.class))).thenReturn(review);

        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                        .contentType("application/json")
                        .content("{ \"eventId\": \"" + review.getEventId() + "\", \"userId\": \"" + review.getUserId() + "\", \"rating\": 5, \"comment\": \"Updated review\", \"status\": \"APPROVED\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId().toString()))
                .andExpect(jsonPath("$.comment").value("Updated review"));

        verify(reviewService, times(1)).updateReview(any(ReviewModel.class));
    }

    @Test
    public void testDeleteReview() throws Exception {
        doNothing().when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/api/reviews/delete-review/{id}", reviewId))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReview(reviewId);
    }

    @Test
    public void testGetReviewsByEventId() throws Exception {
        List<ReviewModel> reviews = Arrays.asList(review);
        when(reviewService.getReviewsByEventId(review.getEventId())).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/event/{eventId}", review.getEventId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(review.getId().toString()))
                .andExpect(jsonPath("$[0].comment").value("Great event!"));

        verify(reviewService, times(1)).getReviewsByEventId(review.getEventId());
    }

    @Test
    public void testGetAverageRating() throws Exception {
        double averageRating = 4.5;
        when(reviewService.calculateEventAverageRating(review.getEventId())).thenReturn(averageRating);

        mockMvc.perform(get("/api/reviews/event/{eventId}/average", review.getEventId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(averageRating));

        verify(reviewService, times(1)).calculateEventAverageRating(review.getEventId());
    }
}