package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoadtripRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private RoadtripRepository roadtripRepository;

  @Test
  public void findByName_success() {
    // given
    Roadtrip roadtrip = new Roadtrip();
    roadtrip.setName("Test Name");
    roadtrip.setDescription("Test Description");

    entityManager.persist(roadtrip);
    entityManager.flush();

    // when
    Roadtrip found = roadtripRepository.findById(roadtrip.getId()).orElse(null);

    // then
    assertNotNull(found.getId());
    assertEquals(found.getName(), roadtrip.getName());
    assertEquals(found.getDescription(), roadtrip.getDescription());
  }
}
