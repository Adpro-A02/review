package id.ac.ui.cs.advprog.review.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AverageRatingResponseDTO {
    private UUID eventId;
    private Double averageRating;
}
