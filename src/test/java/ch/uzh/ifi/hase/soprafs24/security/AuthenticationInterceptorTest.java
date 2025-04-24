package ch.uzh.ifi.hase.soprafs24.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationInterceptorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationInterceptor authenticationInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void preHandle_optionsRequest_shouldAllow() throws Exception {
        when(request.getRequestURI()).thenReturn("/users");
        when(request.getMethod()).thenReturn("OPTIONS");
        

        boolean result = authenticationInterceptor.preHandle(request, response, null);

        assertTrue(result);
    }

    @Test
    public void preHandle_loginEndpoint_shouldAllow() throws Exception {
        when(request.getRequestURI()).thenReturn("/auth/login");
        when(request.getMethod()).thenReturn("POST");

        boolean result = authenticationInterceptor.preHandle(request, response, null);

        assertTrue(result);
    }

    @Test
    public void preHandle_postUsersEndpoint_shouldAllow() throws Exception {
        when(request.getRequestURI()).thenReturn("/users");
        when(request.getMethod()).thenReturn("POST");

        boolean result = authenticationInterceptor.preHandle(request, response, null);

        assertTrue(result);
    }

    @Test
    public void preHandle_noToken_shouldThrowUnauthorized() {
        when(request.getRequestURI()).thenReturn("/someEndpoint");
        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> {
            authenticationInterceptor.preHandle(request, response, null);
        });
    }

    @Test
    public void preHandle_invalidToken_shouldThrowUnauthorized() {
        when(request.getRequestURI()).thenReturn("/someEndpoint");
        when(request.getHeader("Authorization")).thenReturn("invalidToken");
        when(request.getMethod()).thenReturn("POST");
        when(userRepository.findByToken("invalidToken")).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> {
            authenticationInterceptor.preHandle(request, response, null);
        });
    }

    @Test
    public void preHandle_expiredToken_shouldThrowUnauthorized() {
        when(request.getRequestURI()).thenReturn("/someEndpoint");
        when(request.getHeader("Authorization")).thenReturn("expiredToken");
        when(request.getMethod()).thenReturn("GET");
        User user = new User();
        user.setStatus(UserStatus.OFFLINE);
        when(userRepository.findByToken("expiredToken")).thenReturn(user);

        assertThrows(ResponseStatusException.class, () -> {
            authenticationInterceptor.preHandle(request, response, null);
        });
    }

    @Test
    public void preHandle_validToken_shouldAllow() throws Exception {
        when(request.getRequestURI()).thenReturn("/someEndpoint");
        when(request.getHeader("Authorization")).thenReturn("validToken");
        when(request.getMethod()).thenReturn("POST");
        User user = new User();
        user.setStatus(UserStatus.ONLINE);
        when(userRepository.findByToken("validToken")).thenReturn(user);

        boolean result = authenticationInterceptor.preHandle(request, response, null);

        assertTrue(result);
    }
}