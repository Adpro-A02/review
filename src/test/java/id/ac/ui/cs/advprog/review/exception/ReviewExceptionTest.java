package id.ac.ui.cs.advprog.review.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReviewExceptionTest {

    @Test
    void testReviewExceptionWithMessage() {

        String errorMessage = "Pesan error kustom untuk ReviewException";

        ReviewException reviewException = new ReviewException(errorMessage);

        assertNotNull(reviewException);
        assertEquals(errorMessage, reviewException.getMessage());
    }
}