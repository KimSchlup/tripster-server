package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;



@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByFirstName(String firstName);
  User findByLastName(String lastName);
  User findByUsername(String username);
  User findByToken(String token);
  User findByUserId(Long userId);

  @Query("SELECT u.id FROM User u WHERE u.username = :username")
  Optional<Long> findIdByUsername(@Param("username") String username);
}
