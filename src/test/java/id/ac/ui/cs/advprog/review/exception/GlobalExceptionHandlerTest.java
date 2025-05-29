package id.ac.ui.cs.advprog.review.exception;

import id.ac.ui.cs.advprog.review.dto.ReviewResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleReviewException() {

        String errorMessage = "Ini adalah pesan error ReviewException";
        ReviewException reviewException = new ReviewException(errorMessage);

        ResponseEntity<ReviewResponseDTO<?>> responseEntity = globalExceptionHandler.handleReviewException(reviewException);
        ReviewResponseDTO<?> responseBody = responseEntity.getBody();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals(errorMessage, responseBody.getMessage());
        assertNull(responseBody.getData());
    }

    @Test
    void handleAccessDeniedException() {

        String accessDeniedMessage = "Tidak memiliki izin yang cukup";
        AccessDeniedException accessDeniedException = new AccessDeniedException(accessDeniedMessage);

        ResponseEntity<ReviewResponseDTO<?>> responseEntity = globalExceptionHandler.handleAccessDeniedException(accessDeniedException);
        ReviewResponseDTO<?> responseBody = responseEntity.getBody();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("Akses ditolak: " + accessDeniedMessage, responseBody.getMessage());
        assertNull(responseBody.getData());
    }

    @Test
    void handleGeneralException() {

        String generalErrorMessage = "Terjadi kesalahan umum";
        Exception generalException = new Exception(generalErrorMessage);

        ResponseEntity<ReviewResponseDTO<?>> responseEntity = globalExceptionHandler.handleGeneralException(generalException);
        ReviewResponseDTO<?> responseBody = responseEntity.getBody();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("Terjadi kesalahan: " + generalErrorMessage, responseBody.getMessage());
        assertNull(responseBody.getData());
    }
}