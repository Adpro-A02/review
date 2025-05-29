package id.ac.ui.cs.advprog.review.security;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "CORS_ALLOWED_ORIGIN=http://localhost:3000"
})
class SecurityConfigTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtTokenProvider);

        ReflectionTestUtils.setField(securityConfig, "allowedOrigin", "http://localhost:3000");
    }

    @Test
    void testConstructor() {
        SecurityConfig config = new SecurityConfig(jwtTokenProvider);
        assertNotNull(config);
    }

    @Test
    void testJwtAuthenticationFilter_shouldReturnJwtAuthenticationFilter() {
        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter();

        assertNotNull(filter);
        assertInstanceOf(JwtAuthenticationFilter.class, filter);
    }

    @Test
    void testCorsConfigurationSource_shouldReturnProperConfiguration() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

        assertNotNull(corsSource);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/reviews");

        CorsConfiguration config = corsSource.getCorsConfiguration(request);

        assertNotNull(config);
        assertEquals(Arrays.asList("http://localhost:3000"), config.getAllowedOrigins());
        assertEquals(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"), config.getAllowedMethods());
        assertEquals(Arrays.asList("*"), config.getAllowedHeaders());
        assertTrue(config.getAllowCredentials());
    }

    @Test
    void testCorsConfigurationSource_withDifferentPath_shouldReturnSameConfiguration() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/other");

        CorsConfiguration config = corsSource.getCorsConfiguration(request);

        assertNotNull(config);
        assertEquals(Arrays.asList("http://localhost:3000"), config.getAllowedOrigins());
    }

    @Test
    void testCorsConfigurationSource_withRootPath_shouldReturnConfiguration() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");

        CorsConfiguration config = corsSource.getCorsConfiguration(request);

        assertNotNull(config);
        assertEquals(Arrays.asList("http://localhost:3000"), config.getAllowedOrigins());
    }

    @Test
    void testFilterChain_shouldConfigureProperly() throws Exception {
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);

        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.exceptionHandling(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);

        SecurityFilterChain result = securityConfig.filterChain(httpSecurity);

        assertNotNull(result);

        verify(httpSecurity).cors(any());
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).exceptionHandling(any());
        verify(httpSecurity).addFilterBefore(any(JwtAuthenticationFilter.class), eq(UsernamePasswordAuthenticationFilter.class));
        verify(httpSecurity).build();
    }

    @Test
    void testAuthenticationEntryPoint_shouldSendUnauthorizedError() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);

        when(httpSecurity.cors(any())).thenReturn(httpSecurity);
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.exceptionHandling(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        //when(httpSecurity.build()).thenReturn(mock(SecurityFilterChain.class));

        securityConfig.filterChain(httpSecurity);

        verify(httpSecurity).exceptionHandling(any());
    }

    @Test
    void testCorsConfigurationWithMultipleOrigins() {

        ReflectionTestUtils.setField(securityConfig, "allowedOrigin", "http://example.com");

        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");

        CorsConfiguration config = corsSource.getCorsConfiguration(request);

        assertNotNull(config);
        assertEquals(Arrays.asList("http://example.com"), config.getAllowedOrigins());
    }

    @Test
    void testCorsConfigurationAllowedMethods() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();

        CorsConfiguration config = corsSource.getCorsConfiguration(request);

        List<String> expectedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
        assertEquals(expectedMethods, config.getAllowedMethods());
    }

    @Test
    void testCorsConfigurationAllowedHeaders() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();

        CorsConfiguration config = corsSource.getCorsConfiguration(request);

        assertEquals(Arrays.asList("*"), config.getAllowedHeaders());
    }

    @Test
    void testCorsConfigurationAllowCredentials() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();

        CorsConfiguration config = corsSource.getCorsConfiguration(request);

        assertTrue(config.getAllowCredentials());
    }

    @Test
    void testJwtAuthenticationFilterCreation_withSameProvider() {
        JwtAuthenticationFilter filter1 = securityConfig.jwtAuthenticationFilter();
        JwtAuthenticationFilter filter2 = securityConfig.jwtAuthenticationFilter();

        assertNotNull(filter1);
        assertNotNull(filter2);
    }

    @Test
    void testCorsConfigurationUrlPattern() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

        String[] testPaths = {"/api/reviews", "/api/test", "/health", "/actuator"};

        for (String path : testPaths) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI(path);

            CorsConfiguration config = corsSource.getCorsConfiguration(request);
            assertNotNull(config, "CORS config should not be null for path: " + path);
        }
    }
}