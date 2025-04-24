package ch.uzh.ifi.hase.soprafs24.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionAdviceTest {

    @InjectMocks
    private GlobalExceptionAdvice globalExceptionAdvice;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/test"));
    }

    @Test
    public void testHandleConflict_IllegalArgumentException() {
        // Arrange
        String errorMessage = "Test error message";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionAdvice.handleConflict(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    public void testHandleConflict_IllegalStateException() {
        // Arrange
        String errorMessage = "Test error message";
        IllegalStateException exception = new IllegalStateException(errorMessage);

        // Act
        ResponseEntity<Object> response = globalExceptionAdvice.handleConflict(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    public void testHandleConflict_NullMessage() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException();

        // Act
        ResponseEntity<Object> response = globalExceptionAdvice.handleConflict(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("A conflict occurred with the current state of the resource", response.getBody());
    }

    @Test
    public void testHandleTransactionSystemException_WithRootCause() {
        // Arrange
        String rootCauseMessage = "Root cause message";
        RuntimeException rootCause = new RuntimeException(rootCauseMessage);
        TransactionSystemException exception = new TransactionSystemException("Transaction failed", rootCause);

        // Act
        ResponseStatusException result = globalExceptionAdvice.handleTransactionSystemException(exception,
                httpServletRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertTrue(result.getReason().contains(rootCauseMessage));
    }

    @Test
    public void testHandleTransactionSystemException_WithoutRootCause() {
        // Arrange
        TransactionSystemException exception = new TransactionSystemException("Transaction failed");

        // Act
        ResponseStatusException result = globalExceptionAdvice.handleTransactionSystemException(exception,
                httpServletRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertTrue(result.getReason().contains("Transaction failed"));
    }

    @Test
    public void testHandleTransactionSystemException_WithNestedRootCause() {
        // Arrange
        String deepRootCauseMessage = "Deep root cause message";
        Exception deepRootCause = new Exception(deepRootCauseMessage);
        Exception middleCause = new Exception("Middle cause", deepRootCause);
        TransactionSystemException exception = new TransactionSystemException("Transaction failed", middleCause);

        // Act
        ResponseStatusException result = globalExceptionAdvice.handleTransactionSystemException(exception,
                httpServletRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertTrue(result.getReason().contains(deepRootCauseMessage));
    }

    @Test
    public void testHandleException_InternalServerError() {
        // Arrange
        String errorMessage = "Internal server error";
        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                errorMessage);

        // Act
        ResponseStatusException result = globalExceptionAdvice.handleException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getReason().contains(errorMessage));
    }

    @Test
    public void testHandleException_GenericException() {
        // Arrange
        String errorMessage = "Generic exception";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseStatusException result = globalExceptionAdvice.handleException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getReason().contains(errorMessage));
    }
}
