package id.ac.ui.cs.advprog.review.exception;

import id.ac.ui.cs.advprog.review.dto.ReviewResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ReviewResponseDTO<?>> handleReviewException(ReviewException ex) {
        ReviewResponseDTO<?> response = ReviewResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ReviewResponseDTO<?>> handleAccessDeniedException(AccessDeniedException ex) {
        ReviewResponseDTO<?> response = ReviewResponseDTO.builder()
                .success(false)
                .message("Akses ditolak: " + ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ReviewResponseDTO<?>> handleGeneralException(Exception ex) {
        ReviewResponseDTO<?> response = ReviewResponseDTO.builder()
                .success(false)
                .message("Terjadi kesalahan: " + ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
