package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ChecklistCategory;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Priority;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChecklistServiceIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private ChecklistRepository checklistRepository;
    @Autowired private ChecklistElementRepository checklistElementRepository;
    @Autowired private RoadtripRepository roadtripRepository;
    @Autowired private RoadtripMemberRepository roadtripMemberRepository;
    @Autowired private UserService userService;
    @Autowired private RoadtripService roadtripService;
    @Autowired private ChecklistService checklistService;

    @BeforeEach
    public void setup() {
        checklistElementRepository.deleteAll();
        checklistRepository.deleteAll();
        roadtripMemberRepository.deleteAll();
        roadtripRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setCreationDate(java.time.LocalDate.now());
        user.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        user.setToken("token-" + java.util.UUID.randomUUID());
        return userService.createUser(user);
    }

    private Roadtrip createTestRoadtrip(User user) {
        Roadtrip roadtrip = new Roadtrip();
        roadtrip.setName("Test Roadtrip");
        roadtrip.setDescription("Test Description");
        return roadtripService.createRoadtrip(roadtrip, user.getToken());
    }

    @Test
    public void getChecklistByRoadtripId_success() {
        // Create test user with all required fields
        User createdUser = createTestUser("testUsername");

        // Create roadtrip
        Roadtrip createdRoadtrip = createTestRoadtrip(createdUser);

        // When
        Checklist checklist = checklistService.getChecklistByRoadtripId(createdRoadtrip.getRoadtripId());

        // Then
        assertNotNull(checklist);
        assertEquals(createdRoadtrip.getRoadtripId(), checklist.getRoadtripId());
        assertEquals(createdRoadtrip.getRoadtripId(), checklist.getRoadtrip().getRoadtripId());
    }

    @Test
    public void getChecklistByRoadtripId_notFound() {
        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            checklistService.getChecklistByRoadtripId(999L);
        });
    }

    @Test
    public void addChecklistElement_validInputs_success() {
        User createdUser = createTestUser("testUsername");
        Roadtrip createdRoadtrip = createTestRoadtrip(createdUser);

        // Create checklist element
        ChecklistElement element = new ChecklistElement();
        element.setName("Test Element");
        element.setPriority(Priority.HIGH);
        element.setCategory(ChecklistCategory.ITEM);

        // When
        ChecklistElement createdElement = checklistService.addChecklistElement(createdRoadtrip.getRoadtripId(), element);

        // Then
        assertNotNull(createdElement);
        assertNotNull(createdElement.getChecklistElementId());
        assertEquals("Test Element", createdElement.getName());
        assertEquals(Priority.HIGH, createdElement.getPriority());
        assertEquals(ChecklistCategory.ITEM, createdElement.getCategory());
        assertEquals(false, createdElement.getIsCompleted());
    }

    @Test
    public void addChecklistElement_withAssignedUser_success() {
        User createdOwner = createTestUser("owner");
        User createdMember = createTestUser("member");
        Roadtrip createdRoadtrip = createTestRoadtrip(createdOwner);

        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(createdMember.getUserId());
        pk.setRoadtripId(createdRoadtrip.getRoadtripId());

        RoadtripMember roadtripMember = new RoadtripMember();
        roadtripMember.setRoadtripMemberId(pk);
        roadtripMember.setUser(createdMember);
        roadtripMember.setRoadtrip(createdRoadtrip);
        roadtripMember.setInvitationStatus(InvitationStatus.ACCEPTED);
        roadtripMemberRepository.save(roadtripMember);

        ChecklistElement element = new ChecklistElement();
        element.setName("Test Element");
        element.setPriority(Priority.HIGH);
        element.setCategory(ChecklistCategory.ITEM);

        User assignedUser = new User();
        assignedUser.setUsername("member");
        element.setAssignedUser(assignedUser);

        ChecklistElement createdElement = checklistService.addChecklistElement(createdRoadtrip.getRoadtripId(), element);

        assertNotNull(createdElement);
        assertNotNull(createdElement.getChecklistElementId());
        assertEquals("Test Element", createdElement.getName());
        assertEquals(Priority.HIGH, createdElement.getPriority());
        assertEquals(ChecklistCategory.ITEM, createdElement.getCategory());
        assertEquals(false, createdElement.getIsCompleted());
        assertNotNull(createdElement.getAssignedUser());
        assertEquals("member", createdElement.getAssignedUser().getUsername());
    }

    @Test
    public void updateChecklistElement_validInputs_success() {
        User createdUser = createTestUser("testUsername");
        Roadtrip createdRoadtrip = createTestRoadtrip(createdUser);

        ChecklistElement element = new ChecklistElement();
        element.setName("Test Element");
        element.setPriority(Priority.HIGH);
        element.setCategory(ChecklistCategory.ITEM);
        ChecklistElement createdElement = checklistService.addChecklistElement(createdRoadtrip.getRoadtripId(), element);

        ChecklistElement updatedElement = new ChecklistElement();
        updatedElement.setName("Updated Element");
        updatedElement.setPriority(Priority.LOW);
        updatedElement.setCategory(ChecklistCategory.TASK);
        updatedElement.setIsCompleted(true);

        checklistService.updateChecklistElement(updatedElement, createdElement.getChecklistElementId(), createdRoadtrip.getRoadtripId());

        ChecklistElement retrievedElement = checklistElementRepository.findById(createdElement.getChecklistElementId()).orElse(null);
        assertNotNull(retrievedElement);
        assertEquals("Updated Element", retrievedElement.getName());
        assertEquals(Priority.LOW, retrievedElement.getPriority());
        assertEquals(ChecklistCategory.TASK, retrievedElement.getCategory());
        assertEquals(true, retrievedElement.getIsCompleted());
    }

    @Test
    public void deleteChecklistElement_validInputs_success() {
        User createdUser = createTestUser("testUsername");
        Roadtrip createdRoadtrip = createTestRoadtrip(createdUser);

        ChecklistElement element = new ChecklistElement();
        element.setName("Test Element");
        element.setPriority(Priority.HIGH);
        element.setCategory(ChecklistCategory.ITEM);
        ChecklistElement createdElement = checklistService.addChecklistElement(createdRoadtrip.getRoadtripId(), element);

        checklistService.deleteChecklistElement(createdElement.getChecklistElementId());

        assertFalse(checklistElementRepository.existsById(createdElement.getChecklistElementId()));
    }

    @Test
    public void checkAccessRights_userIsMember_success() {
        User createdOwner = createTestUser("owner");
        Roadtrip createdRoadtrip = createTestRoadtrip(createdOwner);
        checklistService.checkAccessRights(createdRoadtrip.getRoadtripId(), createdOwner.getToken());
    }

    @Test
    public void checkAccessRights_userNotMember_throwsForbidden() {
        User createdOwner = createTestUser("owner");
        User createdNonMember = createTestUser("nonMember");
        Roadtrip createdRoadtrip = createTestRoadtrip(createdOwner);

        assertThrows(ResponseStatusException.class, () -> {
            checklistService.checkAccessRights(createdRoadtrip.getRoadtripId(), createdNonMember.getToken());
        });
    }
}