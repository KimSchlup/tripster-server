package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserEmergencyContact;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for emergency contact functionality in UserService
 */
@WebAppConfiguration
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserEmergencyContactServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Qualifier("userEmergencyContactRepository")
    @Autowired
    private ch.uzh.ifi.hase.soprafs24.repository.UserEmergencyContactRepository userEmergencyContactRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    private ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository roadtripMemberRepository;

    @Autowired
    private ch.uzh.ifi.hase.soprafs24.repository.ChecklistRepository checklistRepository;

    @Autowired
    private ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository roadtripRepository;

    @BeforeEach
    public void setup() {
        // Delete in correct order to avoid foreign key constraint violations
        pointOfInterestRepository.deleteAll();
        checklistRepository.deleteAll();
        roadtripMemberRepository.deleteAll();
        roadtripRepository.deleteAll();
        userEmergencyContactRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createEmergencyContact_validInput_success() {
        // given
        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        UserEmergencyContact emergencyContact = new UserEmergencyContact();
        emergencyContact.setFirstName("Emergency");
        emergencyContact.setLastName("Contact");
        emergencyContact.setPhoneNumber("1234567890");

        // when
        UserEmergencyContact savedContact = userService.createEmergencyContact(testUser.getUserId(), emergencyContact);

        // then
        assertNotNull(savedContact.getContactId());
        assertEquals(emergencyContact.getFirstName(), savedContact.getFirstName());
        assertEquals(emergencyContact.getLastName(), savedContact.getLastName());
        assertEquals(emergencyContact.getPhoneNumber(), savedContact.getPhoneNumber());
        assertEquals(testUser.getUserId(), savedContact.getUser().getUserId());
    }

    @Test
    public void createMultipleEmergencyContacts_success() {
        // given
        User testUser = new User();
        testUser.setFirstName("testName");
        testUser.setLastName("lastname");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        userService.createUser(testUser);

        UserEmergencyContact emergencyContact1 = new UserEmergencyContact();
        emergencyContact1.setFirstName("Emergency1");
        emergencyContact1.setLastName("Contact1");
        emergencyContact1.setPhoneNumber("1111111111");

        UserEmergencyContact emergencyContact2 = new UserEmergencyContact();
        emergencyContact2.setFirstName("Emergency2");
        emergencyContact2.setLastName("Contact2");
        emergencyContact2.setPhoneNumber("2222222222");

        // when
        UserEmergencyContact savedContact1 = userService.createEmergencyContact(testUser.getUserId(), emergencyContact1);
        UserEmergencyContact savedContact2 = userService.createEmergencyContact(testUser.getUserId(), emergencyContact2);

        // then
        List<UserEmergencyContact> userContacts = userEmergencyContactRepository.findByUser_UserId(testUser.getUserId());
        
        assertEquals(2, userContacts.size());
        assertTrue(userContacts.stream().anyMatch(c -> c.getFirstName().equals("Emergency1")));
        assertTrue(userContacts.stream().anyMatch(c -> c.getFirstName().equals("Emergency2")));
    }

    @Test
    public void createEmergencyContact_nonExistentUser_throwsException() {
        // given
        UserEmergencyContact emergencyContact = new UserEmergencyContact();
        emergencyContact.setFirstName("Emergency");
        emergencyContact.setLastName("Contact");
        emergencyContact.setPhoneNumber("1234567890");

        // when and then
        assertThrows(ResponseStatusException.class, () -> {
            userService.createEmergencyContact(999L, emergencyContact);
        });
    }
}
