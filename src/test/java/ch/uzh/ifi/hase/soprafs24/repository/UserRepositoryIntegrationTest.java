package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.config.TestConfig;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig.class)
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @Test
    public void findByFirstName_success() {
        // given
        User user = new User();
        user.setFirstName("Firstname");
        user.setLastName("lastname");
        user.setUsername("firstname@lastname");
        user.setPassword("password");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");
        user.setCreationDate(LocalDate.now());

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByFirstName(user.getFirstName());

        // then
        assertNotNull(found.getUserId());
        assertEquals(found.getFirstName(), user.getFirstName());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
    }
}
