package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserPreferences;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.http.HttpStatus;

import java.util.Optional;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setUserId(1L);
    testUser.setFirstName("firstname");
    testUser.setLastName("lastname");
    testUser.setUsername("testUsername");

    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getUserId(), createdUser.getUserId());
    assertEquals(testUser.getFirstName(), createdUser.getFirstName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByFirstName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void getUserById_validInput() {
    // Mock the repository to return the test user when findByUserId is called
    Mockito.when(userRepository.findById(testUser.getUserId())).thenReturn(java.util.Optional.of(testUser));

    // when
    User retrievedUser = userService.getUserById(testUser.getUserId());

    // then
    assertEquals(testUser.getUsername(), retrievedUser.getUsername());
  }

  @Test
  public void getUserById_InvalidInput() {
    // User WrongIdUser = new User();
    Long wrongId = 2L;
    Mockito.when(userRepository.findById(wrongId)).thenReturn(Optional.empty());

    // then
    assertThrows(ResponseStatusException.class, () -> userService.getUserById(wrongId));
  }

  @Test
  public void getUserByToken_validInput() {
    // Mock the repository to return the test user when findByUserId is called
    Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);

    // when
    User retrievedUser = userService.getUserByToken(testUser.getToken());

    // then
    assertEquals(testUser.getUsername(), retrievedUser.getUsername());
  }

  @Test
  public void getUserByToken_InvalidInput() {
  String wrongToken = "wrongToken";

  Mockito.when(userRepository.findByToken(wrongToken)).thenReturn(null);


    // then
    assertThrows(ResponseStatusException.class, () -> userService.getUserByToken(wrongToken));
  }

  @Test
  public void updateUser_success() {
      // given
      Long userId = 1L;
      User existingUser = new User();
      existingUser.setUserId(userId);
      existingUser.setUsername("oldUsername");
      existingUser.setFirstName("oldFirstName");
      existingUser.setLastName("oldLastName");
      existingUser.setPhoneNumber("oldPhoneNumber");
      existingUser.setMail("oldMail");
      existingUser.setPassword("oldPassword");
      existingUser.setReceiveNotifications(true);
      existingUser.setUserPreferences(new UserPreferences());

      User updatedUser = new User();
      updatedUser.setUsername("newUsername");
      updatedUser.setFirstName("newFirstName");
      updatedUser.setLastName("newLastName");
      updatedUser.setPhoneNumber("newPhoneNumber");
      updatedUser.setMail("newMail");
      updatedUser.setPassword("newPassword");
      updatedUser.setReceiveNotifications(false);
      updatedUser.setUserPreferences(new UserPreferences());

      // Mock the repository to return the existing user when getUserById is called
      Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
      Mockito.when(userRepository.findByUsername(updatedUser.getUsername())).thenReturn(null);

      // Mock the repository to save the user when save is called
      Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);

      // when
      userService.updateUser(userId, updatedUser);

      // then
      // Verify that the user is updated correctly
      assertEquals("newUsername", existingUser.getUsername());
      assertEquals("newFirstName", existingUser.getFirstName());
      assertEquals("newLastName", existingUser.getLastName());
      assertEquals("newPhoneNumber", existingUser.getPhoneNumber());
      assertEquals("newMail", existingUser.getMail());
      assertEquals("newPassword", existingUser.getPassword());
      assertEquals(false, existingUser.getReceiveNotifications());
      assertNotNull(existingUser.getUserPreferences());

      // Verify that the repository save method is called
      Mockito.verify(userRepository, Mockito.times(1)).save(existingUser);
      Mockito.verify(userRepository, Mockito.times(1)).flush();
  }

  @Test
  public void updateUser_NoSuccess_duplicateUsername() {
      // given
      Long userId = 1L;
      User existingUser = new User();
      existingUser.setUserId(userId);
      existingUser.setUsername("oldUsername");
      existingUser.setFirstName("oldFirstName");
      existingUser.setLastName("oldLastName");
      existingUser.setPhoneNumber("oldPhoneNumber");
      existingUser.setMail("oldMail");
      existingUser.setPassword("oldPassword");
      existingUser.setReceiveNotifications(true);
      existingUser.setUserPreferences(new UserPreferences());

      User updatedUser = new User();
      updatedUser.setUsername("newUsername");
      updatedUser.setFirstName("newFirstName");
      updatedUser.setLastName("newLastName");
      updatedUser.setPhoneNumber("newPhoneNumber");
      updatedUser.setMail("newMail");
      updatedUser.setPassword("newPassword");
      updatedUser.setReceiveNotifications(false);
      updatedUser.setUserPreferences(new UserPreferences());

      // Mock the repository to return the existing user when getUserById is called
      Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
      Mockito.when(userRepository.findByUsername(updatedUser.getUsername())).thenReturn(updatedUser);

      // Mock the repository to save the user when save is called
      Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);

    // Verify the exception status and message
    // then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.updateUser(userId, updatedUser);
    });

    // Verify the exception status and message
    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    assertEquals("Username already taken", exception.getReason());
  }

  @Test
  public void updateUser_NoSuccess_emptyUsername() {
      // given
      Long userId = 1L;
      User existingUser = new User();
      existingUser.setUserId(userId);
      existingUser.setUsername("oldUsername");
      existingUser.setFirstName("oldFirstName");
      existingUser.setLastName("oldLastName");
      existingUser.setPhoneNumber("oldPhoneNumber");
      existingUser.setMail("oldMail");
      existingUser.setPassword("oldPassword");
      existingUser.setReceiveNotifications(true);
      existingUser.setUserPreferences(new UserPreferences());

      User updatedUser = new User();
      updatedUser.setUsername(""); //empty Username
      updatedUser.setFirstName("newFirstName");
      updatedUser.setLastName("newLastName");
      updatedUser.setPhoneNumber("newPhoneNumber");
      updatedUser.setMail("newMail");
      updatedUser.setPassword("newPassword");
      updatedUser.setReceiveNotifications(false);
      updatedUser.setUserPreferences(new UserPreferences());

      // Mock the repository to return the existing user when getUserById is called
      Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

      // Mock the repository to save the user when save is called
      Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);

    // Verify the exception status and message
    // then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.updateUser(userId, updatedUser);
    });

    // Verify the exception status and message
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Username cannot be empty", exception.getReason());
  }

  @Test
  public void deleteUser_success() {
      // given
      Long userId = 1L;
      User existingUser = new User();
      existingUser.setUserId(userId);

      // Mock the repository to return the existing user when findById is called
      Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

      // when
      userService.deleteUser(userId);

      // then
      // Verify that the repository delete method is called
      Mockito.verify(userRepository, Mockito.times(1)).delete(existingUser);
      Mockito.verify(userRepository, Mockito.times(1)).flush();
  }

  @Test
  public void loginUser_success_statusOnline() {
      // given
      User loginUser = new User();
      loginUser.setUsername("testUser");
      loginUser.setPassword("password");

      User existingUser = new User();
      existingUser.setUsername("testUser");
      existingUser.setPassword("password");
      existingUser.setStatus(UserStatus.OFFLINE); // Initial status

      // Mock the repository to return the existing user when findByUsername is called
      when(userRepository.findByUsername(loginUser.getUsername())).thenReturn(existingUser);

      // Mock the repository to save the user when save is called
      when(userRepository.save(existingUser)).thenReturn(existingUser);

      // when
      User loggedInUser = userService.loginUser(loginUser);

      // then
      assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
      verify(userRepository, times(1)).save(existingUser);
      verify(userRepository, times(1)).flush();
  }

  @Test
  public void loginUser_userNotFound() {
      // given
      User loginUser = new User();
      loginUser.setUsername("testUser");
      loginUser.setPassword("password");

      // Mock the repository to return null when findByUsername is called
      when(userRepository.findByUsername(loginUser.getUsername())).thenReturn(null);

      // then
      assertThrows(ResponseStatusException.class, () -> userService.loginUser(loginUser));
  }

  @Test
  public void loginUser_incorrectPassword() {
      // given
      User loginUser = new User();
      loginUser.setUsername("testUser");
      loginUser.setPassword("wrongPassword");

      User existingUser = new User();
      existingUser.setUsername("testUser");
      existingUser.setPassword("correctPassword");

      // Mock the repository to return the existing user when findByUsername is called
      when(userRepository.findByUsername(loginUser.getUsername())).thenReturn(existingUser);

      // then
      ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.loginUser(loginUser));
      assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
      assertEquals("Password incorrect", exception.getReason());
  }

  @Test
  public void logoutUser_success_statusOffline() {
      // given
      User logoutUser = new User();
      logoutUser.setUsername("testUser");

      User existingUser = new User();
      existingUser.setUsername("testUser");
      existingUser.setStatus(UserStatus.ONLINE); // Initial status

      // Mock the repository to return the existing user when findByUsername is called
      when(userRepository.findByUsername(logoutUser.getUsername())).thenReturn(existingUser);

      // Mock the repository to save the user when save is called
      when(userRepository.save(existingUser)).thenReturn(existingUser);

      // when
      userService.logoutUser(logoutUser);

      // then
      assertEquals(UserStatus.OFFLINE, existingUser.getStatus());
      verify(userRepository, times(1)).save(existingUser);
      verify(userRepository, times(1)).flush();
  }

  @Test
  public void logoutUser_userNotFound() {
      // given
      User logoutUser = new User();
      logoutUser.setUsername("testUser");

      // Mock the repository to return null when findByUsername is called
      when(userRepository.findByUsername(logoutUser.getUsername())).thenReturn(null);

      // then
      ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.logoutUser(logoutUser));
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
      assertEquals("User not found", exception.getReason());
  }
}
