package id.ac.ui.cs.advprog.review.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @InjectMocks
    private WebConfig webConfig;

    @Mock
    private CorsRegistry corsRegistry;

    @Mock
    private CorsRegistration corsRegistration;

    private final String testAllowedOrigin = "http://localhost:3001";

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(webConfig, "allowedOrigin", testAllowedOrigin);

        when(corsRegistry.addMapping(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);
    }

    @Test
    void testAddCorsMappings() {

        webConfig.addCorsMappings(corsRegistry);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(corsRegistry).addMapping(pathCaptor.capture());
        assertEquals("/**", pathCaptor.getValue());

        ArgumentCaptor<String> stringArrayCaptor = ArgumentCaptor.forClass(String.class);
        verify(corsRegistration).allowedOrigins(stringArrayCaptor.capture());
        assertEquals(testAllowedOrigin, stringArrayCaptor.getValue());

        verify(corsRegistration).allowedMethods(stringArrayCaptor.capture());
        assertEquals("*", stringArrayCaptor.getValue());

        verify(corsRegistration).allowedHeaders(stringArrayCaptor.capture());
        assertEquals("*", stringArrayCaptor.getValue());

        ArgumentCaptor<Boolean> booleanCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(corsRegistration).allowCredentials(booleanCaptor.capture());
        assertTrue(booleanCaptor.getValue());
    }
}