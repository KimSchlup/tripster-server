package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user.
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller
 * 
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User loginUser(User user) {
    User userByUsername = userRepository.findByUsername(user.getUsername());
    if (userByUsername == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    if (!userByUsername.getPassword().equals(user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password incorrect");
    }
    userByUsername.setStatus(UserStatus.ONLINE);
    userByUsername.setToken(UUID.randomUUID().toString());
    userRepository.save(userByUsername);
    userRepository.flush();

    return userByUsername;
  }

  public void logoutUser(User user) {
    User userByUsername = userRepository.findByUsername(user.getUsername());
    if (userByUsername == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    userByUsername.setStatus(UserStatus.OFFLINE);
    userRepository.save(userByUsername);
    userRepository.flush();
    return;
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
    newUser.setCreationDate(LocalDate.now());
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User getUserById(Long userId) {
    return this.userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  public User getUserByToken(String token) {
    System.out.println(token);
    return this.userRepository.findByToken(token);
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    }
  }
}
