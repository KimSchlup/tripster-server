package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;



/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Qualifier("roadtripRepository")
    @Autowired
    private RoadtripRepository roadtripRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoadtripService roadtripService;

    @BeforeEach
    public void setup() {
        roadtripRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        // when
        User fetchedUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getUserId(), fetchedUser.getUserId());
        assertEquals(testUser.getFirstName(), fetchedUser.getFirstName());
        assertEquals(testUser.getUsername(), fetchedUser.getUsername());
        assertNotNull(fetchedUser.getToken());
        assertEquals(UserStatus.ONLINE, fetchedUser.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();

        // change the name but forget about the username
        testUser2.setFirstName("testName2");
        testUser2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    public void GetUserById_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        Long UserId = testUser.getUserId();

        // when
        User fetchedUser = userService.getUserById(UserId);

        // then
        assertEquals(testUser.getUserId(), fetchedUser.getUserId());
        assertEquals(testUser.getFirstName(), fetchedUser.getFirstName());
        assertEquals(testUser.getUsername(), fetchedUser.getUsername());
        assertNotNull(fetchedUser.getToken());
    }

    @Test
    public void GetUserById_InvalidInput() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.getUserById(999L));
    }

    @Test
    public void updateUser_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));
        assertNull(userRepository.findByUsername("newUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        Long testUserId = testUser.getUserId();

        User updatedUser = new User();
        updatedUser.setUsername("newUsername");
        updatedUser.setFirstName("newFirstName");
        updatedUser.setLastName("newLastName");
        updatedUser.setPhoneNumber("newPhoneNumber");
        updatedUser.setMail("newMail");
        updatedUser.setPassword("newPassword");
        updatedUser.setReceiveNotifications(false);
        //updatedUser.setUserPreferences(new UserPreferences());


        // when
        userService.updateUser(testUserId, updatedUser);

        // then
        // Verify that the user is updated correctly
        User updatedTestUser = userRepository.findById(testUserId).orElse(null);
        assertEquals("newUsername", updatedTestUser.getUsername());
        assertEquals("newFirstName", updatedTestUser.getFirstName());
        assertEquals("newLastName", updatedTestUser.getLastName());
        assertEquals("newPhoneNumber", updatedTestUser.getPhoneNumber());
        assertEquals("newMail", updatedTestUser.getMail());
        assertEquals("newPassword", updatedTestUser.getPassword());
        assertEquals(false, updatedTestUser.getReceiveNotifications());
        //assertNotNull(updatedTestUser.getUserPreferences());
    }

    @Test
    public void updateUser_NoSuccess_duplicateUsername() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));
        assertNull(userRepository.findByUsername("duplicateUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        Long testUserId = testUser.getUserId();

        User anotherUser = new User();
        anotherUser.setFirstName("testName");
        anotherUser.setLastName("lastname");
        anotherUser.setUsername("duplicateUsername");
        anotherUser.setPassword("password");
        userService.createUser(anotherUser);

        User updatedUser = new User();
        updatedUser.setUsername("duplicateUsername");
        updatedUser.setFirstName("newFirstName");
        updatedUser.setLastName("newLastName");
        updatedUser.setPhoneNumber("newPhoneNumber");
        updatedUser.setMail("newMail");
        updatedUser.setPassword("newPassword");
        updatedUser.setReceiveNotifications(false);
        //updatedUser.setUserPreferences(new UserPreferences());


    // when and then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
        userService.updateUser(testUserId, updatedUser);
    });

    // Verify the exception status and message
    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    assertEquals("Username already taken", exception.getReason());
    }

    @Test
    public void updateUser_NoSuccess_emptyUsername() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));
        assertNull(userRepository.findByUsername("duplicateUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        Long testUserId = testUser.getUserId();

        User updatedUser = new User();
        updatedUser.setUsername("");
        updatedUser.setFirstName("newFirstName");
        updatedUser.setLastName("newLastName");
        updatedUser.setPhoneNumber("newPhoneNumber");
        updatedUser.setMail("newMail");
        updatedUser.setPassword("newPassword");
        updatedUser.setReceiveNotifications(false);
        //updatedUser.setUserPreferences(new UserPreferences());


    // when and then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
        userService.updateUser(testUserId, updatedUser);
    });

    // Verify the exception status and message
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Username cannot be empty", exception.getReason());
    }

    @Test
    public void deleteUser_Success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        Long testUserId = testUser.getUserId();


        // when
        userService.deleteUser(testUserId);

        // Verify
        // when and then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.getUserById(testUserId);
        });

        // Verify the exception status and message
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void deleteUser_NoSuccess() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        Long testUserId = testUser.getUserId();
        String token = testUser.getToken();

        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("TestRoadtrip");
        roadtripService.createRoadtrip(testRoadtrip, token);

        // Verify
        // when and then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.deleteUser(testUserId);
        });

        // Verify the exception status and message
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    public void logoutUser_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        // when
        userService.logoutUser(testUser);
        userRepository.flush();

        // Retrieve the user from the repository to check the updated status
        User loggedInUser = userRepository.findByUsername("testUsername");

        // then
        assertNotNull(loggedInUser, "The logged out user should not be null");
        assertEquals(UserStatus.OFFLINE, loggedInUser.getStatus());
    }

    @Test
    public void logoutUser_fail() {
        // given
        assertNull(userRepository.findByUsername("noName"));

        User testUser = new User();
        testUser.setFirstName("noName");
        testUser.setLastName("lastname");
        testUser.setUsername("noUsername");
        testUser.setPassword("password");

       // Verify
        // when and then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.logoutUser(testUser);
        });

        // Verify the exception status and message
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    @Test
    public void loginUser_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);
        testUser.setStatus(UserStatus.OFFLINE);

        assertEquals(UserStatus.OFFLINE, testUser.getStatus());

        //when
        userService.loginUser(testUser);
        userRepository.flush();

        // Retrieve the user from the repository to check the updated status
        User loggedInUser = userRepository.findByUsername("testUsername");

        // then
        assertNotNull(loggedInUser, "The logged out user should not be null");
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
    }

    @Test
    public void loginUser_fail_NotFound() {
        // given
        assertNull(userRepository.findByUsername("noName"));

        User testUser = new User();
        testUser.setFirstName("noName");
        testUser.setLastName("lastname");
        testUser.setUsername("noUsername");
        testUser.setPassword("password");

       // Verify
        // when and then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.loginUser(testUser);
        });

        // Verify the exception status and message
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void loginUser_fail_WrongPassword() {
        // given
        assertNull(userRepository.findByUsername("noName"));

        User testUser = new User();
        testUser.setFirstName("noName");
        testUser.setLastName("lastname");
        testUser.setUsername("noUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        User fakeUser = new User();
        fakeUser.setFirstName("noName");
        fakeUser.setLastName("lastname");
        fakeUser.setUsername("noUsername");
        fakeUser.setPassword("wrongpassword");

       // Verify
        // when and then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.loginUser(fakeUser);
        });

        // Verify the exception status and message
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
}