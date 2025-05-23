package id.ac.ui.cs.advprog.review.controller;

import id.ac.ui.cs.advprog.review.dto.*;
import id.ac.ui.cs.advprog.review.enums.ReviewStatus;
import id.ac.ui.cs.advprog.review.model.ReviewModel;
import id.ac.ui.cs.advprog.review.repository.ReviewRepository;
import id.ac.ui.cs.advprog.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewRepository repository;

    private ReviewModel toEntity(ReviewDTO dto) {
        if (dto == null) return null;
        return ReviewModel.builder()
                .id(dto.getId())
                .eventId(dto.getEventId())
                .userId(dto.getUserId())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdDate(dto.getCreatedDate())
                .updatedDate(dto.getUpdatedDate())
                .status(dto.getStatus())
                .build();
    }

    private ReviewDTO toDTO(ReviewModel model) {
        if (model == null) return null;
        return ReviewDTO.builder()
                .id(model.getId())
                .eventId(model.getEventId())
                .userId(model.getUserId())
                .rating(model.getRating())
                .comment(model.getComment())
                .createdDate(model.getCreatedDate())
                .updatedDate(model.getUpdatedDate())
                .status(model.getStatus())
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('User')")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponseDTO<ReviewDTO> createReview(@RequestBody ReviewDTO request, Authentication auth) {
        request.setUserId(UUID.fromString(auth.getName()));
        ReviewModel model = toEntity(request);
        if (model.getStatus() == null) {
            model.setStatus(ReviewStatus.APPROVED);
        }

        ReviewModel saved = reviewService.createReview(model).join();
        ReviewDTO dto = toDTO(saved);

        return ReviewResponseDTO.<ReviewDTO>builder()
                .success(true)
                .message("Review berhasil dibuat")
                .data(dto)
                .build();
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasAuthority('User')")
    public ReviewResponseDTO<ReviewDTO> updateReview(@PathVariable UUID id,
                                                     @RequestBody ReviewDTO request,
                                                     Authentication auth) {
        ReviewModel existing = repository.findById(id);
        if (existing == null) {
            return ReviewResponseDTO.<ReviewDTO>builder()
                    .success(false)
                    .message("Review dengan ID " + id + " tidak ditemukan")
                    .data(null)
                    .build();
        }
        if (!existing.getUserId().toString().equals(auth.getName())) {
            return ReviewResponseDTO.<ReviewDTO>builder()
                    .success(false)
                    .message("Tidak bisa memperbarui review milik orang lain")
                    .build();
        }

        if (request.getRating() != null) existing.setRating(request.getRating());
        if (request.getComment() != null) existing.setComment(request.getComment());

        ReviewModel updated = reviewService.updateReview(existing);
        ReviewDTO dto = toDTO(updated);
        return ReviewResponseDTO.<ReviewDTO>builder()
                .success(true)
                .message("Review berhasil diperbarui")
                .data(dto)
                .build();
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAuthority('User') or hasAuthority('Admin')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ReviewResponseDTO<Void> deleteReview(@PathVariable UUID id, Authentication auth) {
        ReviewModel existing = repository.findById(id);
        if (existing == null) {
            return ReviewResponseDTO.<Void>builder()
                    .success(false)
                    .message("Review dengan ID " + id + " tidak ditemukan")
                    .build();
        }
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Admin"));
        boolean isOwner = existing.getUserId().toString().equals(auth.getName());
        if (!isAdmin && !isOwner) {
            return ReviewResponseDTO.<Void>builder()
                    .success(false)
                    .message("Tidak bisa menghapus review milik orang lain")
                    .build();
        }
        reviewService.deleteReview(id);
        return ReviewResponseDTO.<Void>builder()
                .success(true)
                .message("Review berhasil dihapus")
                .build();
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAuthority('User') or hasAuthority('Admin') or permitAll()")
    public ReviewResponseDTO<ReviewsByEventResponseDTO> getReviewsByEventId(@PathVariable UUID eventId) {
        List<ReviewDTO> list = reviewService.getReviewsByEventId(eventId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        ReviewsByEventResponseDTO payload = ReviewsByEventResponseDTO.builder()
                .eventId(eventId)
                .reviews(list)
                .build();
        return ReviewResponseDTO.<ReviewsByEventResponseDTO>builder()
                .success(true)
                .message("Daftar review untuk event " + eventId)
                .data(payload)
                .build();
    }

    @GetMapping("/event/{eventId}/organizer")
    @PreAuthorize("hasAuthority('Organizer')")
    public ReviewResponseDTO<ReviewsByEventResponseDTO> getReviewsForOrganizer(@PathVariable UUID eventId, Authentication auth) {
        UUID organizerId = UUID.fromString(auth.getName());
        List<ReviewDTO> list = reviewService.getReviewsForOrganizer(eventId, organizerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        ReviewsByEventResponseDTO payload = ReviewsByEventResponseDTO.builder()
                .eventId(eventId)
                .reviews(list)
                .build();

        return ReviewResponseDTO.<ReviewsByEventResponseDTO>builder()
                .success(true)
                .message("Daftar review untuk organizer")
                .data(payload)
                .build();
    }

    @GetMapping("/event/{eventId}/average")
    // @PreAuthorize("hasAuthority('Admin')")
    public ReviewResponseDTO<AverageRatingResponseDTO> getAverageRating(@PathVariable UUID eventId) {
        Double avg = reviewService.calculateEventAverageRating(eventId);
        AverageRatingResponseDTO payload = AverageRatingResponseDTO.builder()
                .eventId(eventId)
                .averageRating(avg)
                .build();
        return ReviewResponseDTO.<AverageRatingResponseDTO>builder()
                .success(true)
                .message("Rata-rata rating untuk event " + eventId)
                .data(payload)
                .build();
    }
}