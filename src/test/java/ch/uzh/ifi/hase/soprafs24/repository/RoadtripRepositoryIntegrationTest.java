package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.config.TestConfig;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;

import org.junit.jupiter.api.Test;
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
public class RoadtripRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private RoadtripRepository roadtripRepository;

  @MockBean
  private AuthenticationInterceptor authenticationInterceptor;

  @Test
  public void findByName_success() {
    // given
    User testUser = new User();
    testUser.setUsername("testuser");
    testUser.setFirstName("Firstname");
    testUser.setLastName("lastname");
    testUser.setPassword("password");
    testUser.setStatus(UserStatus.OFFLINE);
    testUser.setToken("1");
    testUser.setCreationDate(LocalDate.now());

    entityManager.persistAndFlush(testUser); // Combines persist + flush

    Roadtrip roadtrip = new Roadtrip();
    roadtrip.setOwner(testUser);
    roadtrip.setName("testname");
    roadtrip.setDescription("Test Description");

    entityManager.persist(roadtrip);
    entityManager.flush();

    // when
    Roadtrip found = roadtripRepository.findById(roadtrip.getRoadtripId()).orElse(null);

    // then
    assertNotNull(found.getRoadtripId());
    assertEquals(found.getName(), roadtrip.getName());
    assertEquals(found.getDescription(), roadtrip.getDescription());
  }
}
