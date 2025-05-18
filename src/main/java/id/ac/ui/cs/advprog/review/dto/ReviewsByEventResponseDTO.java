package id.ac.ui.cs.advprog.review.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewsByEventResponseDTO {
    private UUID eventId;
    private List<ReviewDTO> reviews;
}
