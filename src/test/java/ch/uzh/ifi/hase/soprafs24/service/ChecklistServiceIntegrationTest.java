package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ChecklistCategory;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Priority;
import ch.uzh.ifi.hase.soprafs24.entity.Checklist;
import ch.uzh.ifi.hase.soprafs24.entity.ChecklistElement;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChecklistElementRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChecklistRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChecklistRepository checklistRepository;

    @Autowired
    private ChecklistElementRepository checklistElementRepository;

    @Autowired
    private RoadtripRepository roadtripRepository;

    @Autowired
    private RoadtripMemberRepository roadtripMemberRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoadtripService roadtripService;

    @Autowired
    private ChecklistService checklistService;

    @BeforeEach
    public void setup() {
        checklistElementRepository.deleteAll();
        checklistRepository.deleteAll();
        roadtripMemberRepository.deleteAll();
        roadtripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getChecklistByRoadtripId_success() {
        // Create test user with all required fields
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreationDate(java.time.LocalDate.now());
        testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdUser = userService.createUser(testUser);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

        // When
        Checklist checklist = checklistService.getChecklistByRoadtripId(createdRoadtrip.getRoadtripId());

        // Then
        assertNotNull(checklist);
        assertEquals(createdRoadtrip.getRoadtripId(), checklist.getRoadtripId());
        // Compare roadtrip IDs instead of the objects themselves
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
        // Create test user with all required fields
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreationDate(java.time.LocalDate.now());
        testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdUser = userService.createUser(testUser);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

        // Create checklist element
        ChecklistElement element = new ChecklistElement();
        element.setName("Test Element");
        element.setPriority(Priority.HIGH);
        element.setCategory(ChecklistCategory.ITEM);

        // When
        ChecklistElement createdElement = checklistService.addChecklistElement(createdRoadtrip.getRoadtripId(),
                element);

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
        // Create test users with all required fields
        User owner = new User();
        owner.setUsername("owner");
        owner.setPassword("password");
        owner.setFirstName("Owner");
        owner.setLastName("User");
        owner.setCreationDate(java.time.LocalDate.now());
        owner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        owner.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdOwner = userService.createUser(owner);

        User member = new User();
        member.setUsername("member");
        member.setPassword("password");
        member.setFirstName("Member");
        member.setLastName("User");
        member.setCreationDate(java.time.LocalDate.now());
        member.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        member.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdMember = userService.createUser(member);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdOwner.getToken());

        // Create roadtrip member
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(createdMember.getUserId());
        pk.setRoadtripId(createdRoadtrip.getRoadtripId());

        RoadtripMember roadtripMember = new RoadtripMember();
        roadtripMember.setRoadtripMemberId(pk);
        roadtripMember.setUser(createdMember);
        roadtripMember.setRoadtrip(createdRoadtrip);
        roadtripMember.setInvitationStatus(InvitationStatus.ACCEPTED);
        roadtripMemberRepository.save(roadtripMember);

        // Create checklist element with assigned user
        ChecklistElement element = new ChecklistElement();
        element.setName("Test Element");
        element.setPriority(Priority.HIGH);
        element.setCategory(ChecklistCategory.ITEM);

        User assignedUser = new User();
        assignedUser.setUsername("member");
        element.setAssignedUser(assignedUser);

        // When
        ChecklistElement createdElement = checklistService.addChecklistElement(createdRoadtrip.getRoadtripId(),
                element);

        // Then
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
        // Create test user with all required fields
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreationDate(java.time.LocalDate.now());
        testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdUser = userService.createUser(testUser);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

        // Create checklist element
        ChecklistElement element = new ChecklistElement();
        element.setName("Test Element");
        element.setPriority(Priority.HIGH);
        element.setCategory(ChecklistCategory.ITEM);
        ChecklistElement createdElement = checklistService.addChecklistElement(createdRoadtrip.getRoadtripId(),
                element);

        // Update checklist element
        ChecklistElement updatedElement = new ChecklistElement();
        updatedElement.setName("Updated Element");
        updatedElement.setPriority(Priority.LOW);
        updatedElement.setCategory(ChecklistCategory.TASK);
        updatedElement.setIsCompleted(true);

        // When
        checklistService.updateChecklistElement(updatedElement, createdElement.getChecklistElementId(),
                createdRoadtrip.getRoadtripId());

        // Then
        ChecklistElement retrievedElement = checklistElementRepository.findById(createdElement.getChecklistElementId())
                .orElse(null);
        assertNotNull(retrievedElement);
        assertEquals("Updated Element", retrievedElement.getName());
        assertEquals(Priority.LOW, retrievedElement.getPriority());
        assertEquals(ChecklistCategory.TASK, retrievedElement.getCategory());
        assertEquals(true, retrievedElement.getIsCompleted());
    }

    @Test
    public void deleteChecklistElement_validInputs_success() {
        // Create test user with all required fields
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreationDate(java.time.LocalDate.now());
        testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdUser = userService.createUser(testUser);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

        // Create checklist element
        ChecklistElement element = new ChecklistElement();
        element.setName("Test Element");
        element.setPriority(Priority.HIGH);
        element.setCategory(ChecklistCategory.ITEM);
        ChecklistElement createdElement = checklistService.addChecklistElement(createdRoadtrip.getRoadtripId(),
                element);

        // When
        checklistService.deleteChecklistElement(createdElement.getChecklistElementId());

        // Then
        assertFalse(checklistElementRepository.existsById(createdElement.getChecklistElementId()));
    }

    @Test
    public void checkAccessRights_userIsMember_success() {
        // Create test users with all required fields
        User owner = new User();
        owner.setUsername("owner");
        owner.setPassword("password");
        owner.setFirstName("Owner");
        owner.setLastName("User");
        owner.setCreationDate(java.time.LocalDate.now());
        owner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        owner.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdOwner = userService.createUser(owner);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdOwner.getToken());

        // When/Then - No exception should be thrown
        checklistService.checkAccessRights(createdRoadtrip.getRoadtripId(), createdOwner.getToken());
    }

    @Test
    public void checkAccessRights_userNotMember_throwsForbidden() {
        // Create test users with all required fields
        User owner = new User();
        owner.setUsername("owner");
        owner.setPassword("password");
        owner.setFirstName("Owner");
        owner.setLastName("User");
        owner.setCreationDate(java.time.LocalDate.now());
        owner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        owner.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdOwner = userService.createUser(owner);

        User nonMember = new User();
        nonMember.setUsername("nonMember");
        nonMember.setPassword("password");
        nonMember.setFirstName("NonMember");
        nonMember.setLastName("User");
        nonMember.setCreationDate(java.time.LocalDate.now());
        nonMember.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        nonMember.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdNonMember = userService.createUser(nonMember);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdOwner.getToken());

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            checklistService.checkAccessRights(createdRoadtrip.getRoadtripId(), createdNonMember.getToken());
        });
    }
}
