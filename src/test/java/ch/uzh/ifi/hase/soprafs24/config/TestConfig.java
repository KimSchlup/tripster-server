package ch.uzh.ifi.hase.soprafs24.config;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration to mock the SecretManagerServiceClient
 * This prevents the application from trying to connect to Google Cloud Secret
 * Manager during tests
 */
@TestConfiguration
public class TestConfig {

    /**
     * Creates a mock SecretManagerServiceClient bean for testing
     * This bean will be used instead of the real SecretManagerServiceClient during
     * tests
     * 
     * @return A mock SecretManagerServiceClient
     */
    @Bean
    @Primary
    public SecretManagerServiceClient secretManagerServiceClient() {
        return Mockito.mock(SecretManagerServiceClient.class);
    }
}
