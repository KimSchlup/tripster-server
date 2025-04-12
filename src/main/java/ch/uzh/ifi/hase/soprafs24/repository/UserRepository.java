package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByFirstName(String firstName);
  User findByLastName(String lastName);
  User findByUsername(String username);
  User findByToken(String token);
  User findByUserId(Long userId);
}
