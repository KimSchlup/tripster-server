package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChecklistRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripGetDTO;

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

@WebAppConfiguration
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoadtripServiceIntegrationTest {

    @Autowired
    private RoadtripRepository roadtripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoadtripMemberRepository roadtripMemberRepository;

    @Autowired
    private ChecklistRepository checklistRepository;

    @Autowired
    private RoadtripService roadtripService;

    @Autowired
    private UserService userService;

    @Autowired
    private ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository pointOfInterestRepository;

    @BeforeEach
    public void setup() {
        // Delete in correct order to avoid foreign key constraint violations
        pointOfInterestRepository.deleteAll();
        checklistRepository.deleteAll();
        roadtripMemberRepository.deleteAll();
        roadtripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createRoadtrip_validInputs_success() {
        // Create a test user with all required fields
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreationDate(java.time.LocalDate.now());
        testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdUser = userService.createUser(testUser);

        // Create a roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");

        // When
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

        // Then
        assertNotNull(createdRoadtrip.getRoadtripId());
        assertEquals("Test Roadtrip", createdRoadtrip.getName());
        assertEquals("Test Description", createdRoadtrip.getDescription());
        assertEquals(createdUser.getUserId(), createdRoadtrip.getOwner().getUserId());

        // Verify checklist was created
        assertTrue(checklistRepository.existsByRoadtripId(createdRoadtrip.getRoadtripId()));

        // Verify roadtrip member was created for owner
        List<RoadtripMember> members = roadtripMemberRepository.findByRoadtrip(createdRoadtrip);
        assertEquals(1, members.size());
        assertEquals(createdUser.getUserId(), members.get(0).getUser().getUserId());
        assertEquals(InvitationStatus.ACCEPTED, members.get(0).getInvitationStatus());
    }

    @Test
    public void getRoadtripsOfUser_returnsCorrectRoadtrips() {
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

        // Create roadtrips
        Roadtrip ownedRoadtrip = new Roadtrip();
        ownedRoadtrip.setName("Owned Roadtrip");
        ownedRoadtrip.setDescription("Owned Description");
        Roadtrip createdOwnedRoadtrip = roadtripService.createRoadtrip(ownedRoadtrip, createdOwner.getToken());

        // Create member roadtrip
        Roadtrip memberRoadtrip = new Roadtrip();
        memberRoadtrip.setName("Member Roadtrip");
        memberRoadtrip.setDescription("Member Description");
        Roadtrip createdMemberRoadtrip = roadtripService.createRoadtrip(memberRoadtrip, createdMember.getToken());

        // Create roadtrip member
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(createdOwner.getUserId());
        pk.setRoadtripId(createdMemberRoadtrip.getRoadtripId());

        RoadtripMember roadtripMember = new RoadtripMember();
        roadtripMember.setRoadtripMemberId(pk);
        roadtripMember.setUser(createdOwner);
        roadtripMember.setRoadtrip(createdMemberRoadtrip);
        roadtripMember.setInvitationStatus(InvitationStatus.ACCEPTED);
        roadtripMemberRepository.save(roadtripMember);

        // When
        List<RoadtripGetDTO> ownerRoadtrips = roadtripService.getRoadtripsOfUser(createdOwner);

        // Then
        assertEquals(2, ownerRoadtrips.size());
        assertTrue(ownerRoadtrips.stream().anyMatch(r -> r.getName().equals("Owned Roadtrip")));
        assertTrue(ownerRoadtrips.stream().anyMatch(r -> r.getName().equals("Member Roadtrip")));
    }

    @Test
    public void getRoadtripById_validId_returnsRoadtrip() {
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
        RoadtripGetDTO roadtripGetDTO = roadtripService.getRoadtripById(createdRoadtrip.getRoadtripId(), createdUser);

        // Then
        assertEquals(createdRoadtrip.getRoadtripId(), roadtripGetDTO.getRoadtripId());
        assertEquals("Test Roadtrip", roadtripGetDTO.getName());
        assertEquals("Test Description", roadtripGetDTO.getDescription());
        assertEquals(createdUser.getUserId(), roadtripGetDTO.getOwnerId());
        assertEquals(InvitationStatus.ACCEPTED, roadtripGetDTO.getInvitationStatus());
    }

    @Test
    public void getRoadtripById_userNotMember_throwsForbidden() {
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
            roadtripService.getRoadtripById(createdRoadtrip.getRoadtripId(), createdNonMember);
        });
    }

    @Test
    public void updateRoadtripById_validInputs_success() {
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

        // Update roadtrip
        Roadtrip updatedRoadtrip = new Roadtrip();
        updatedRoadtrip.setName("Updated Roadtrip");
        updatedRoadtrip.setDescription("Updated Description");

        // When
        Roadtrip result = roadtripService.updateRoadtripById(createdRoadtrip.getRoadtripId(), updatedRoadtrip);

        // Then
        assertEquals("Updated Roadtrip", result.getName());
        assertEquals("Updated Description", result.getDescription());

        // Verify in repository
        Roadtrip storedRoadtrip = roadtripRepository.findById(createdRoadtrip.getRoadtripId()).orElse(null);
        assertNotNull(storedRoadtrip);
        assertEquals("Updated Roadtrip", storedRoadtrip.getName());
        assertEquals("Updated Description", storedRoadtrip.getDescription());
    }

    @Test
    public void deleteRoadtrip_validId_success() {
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
        roadtripService.deleteRoadtrip(createdRoadtrip.getRoadtripId(), createdUser.getUserId());

        // Then
        assertFalse(roadtripRepository.existsById(createdRoadtrip.getRoadtripId()));
    }

    @Test
    public void deleteRoadtrip_userNotOwner_throwsForbidden() {
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

        User nonOwner = new User();
        nonOwner.setUsername("nonOwner");
        nonOwner.setPassword("password");
        nonOwner.setFirstName("NonOwner");
        nonOwner.setLastName("User");
        nonOwner.setCreationDate(java.time.LocalDate.now());
        nonOwner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        nonOwner.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdNonOwner = userService.createUser(nonOwner);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdOwner.getToken());

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            roadtripService.deleteRoadtrip(createdRoadtrip.getRoadtripId(), createdNonOwner.getUserId());
        });
    }
}
