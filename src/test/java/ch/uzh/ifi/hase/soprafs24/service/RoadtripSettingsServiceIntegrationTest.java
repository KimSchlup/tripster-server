package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.BasemapType;
import ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripSettings;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripSettingsRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoadtripSettingsServiceIntegrationTest {
    @Autowired
    private RoadtripRepository roadtripRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoadtripMemberRepository roadtripMemberRepository;
    @Autowired
    private RoadtripSettingsRepository roadtripSettingsRepository;
    @Autowired
    private RoadtripService roadtripService;
    @Autowired
    private RoadtripSettingsService roadtripSettingsService;
    @Autowired
    private UserService userService;
    @Autowired
    private ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository pointOfInterestRepository;

    @BeforeEach
    public void setup() {
        // Delete in correct order to avoid foreign key constraint violations
        pointOfInterestRepository.deleteAll();
        roadtripSettingsRepository.deleteAll();
        roadtripMemberRepository.deleteAll();
        roadtripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createRoadtripSettings_validInputs_success() {
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

        // Create a roadtrip with owner
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setOwner(createdUser);
        testRoadtrip = roadtripRepository.save(testRoadtrip);

        // When
        RoadtripSettings createdSettings = roadtripSettingsService.createRoadtripSettings(testRoadtrip);

        // Then
        assertNotNull(createdSettings);
        // Compare roadtrip IDs instead of the objects themselves
        assertEquals(testRoadtrip.getRoadtripId(), createdSettings.getRoadtrip().getRoadtripId());
        assertEquals(BasemapType.OPEN_STREET_MAP, createdSettings.getBasemapType());
        assertEquals(DecisionProcess.MAJORITY, createdSettings.getDecisionProcess());
        assertNotNull(createdSettings.getStartDate());
        assertNotNull(createdSettings.getEndDate());
    }

    @Test
    public void getRoadtripSettingsById_validId_returnsSettings() {
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
        testRoadtrip.setOwner(createdUser);
        Roadtrip createdRoadtrip = roadtripRepository.save(testRoadtrip);

        // Create settings
        RoadtripSettings settings = roadtripSettingsService.createRoadtripSettings(createdRoadtrip);

        // When
        RoadtripSettings retrievedSettings = roadtripSettingsService.getRoadtripSettingsById(
                createdRoadtrip.getRoadtripId(), createdUser);

        // Then
        assertEquals(settings.getRoadtripSettingsId(), retrievedSettings.getRoadtripSettingsId());
        assertEquals(settings.getBasemapType(), retrievedSettings.getBasemapType());
        assertEquals(settings.getDecisionProcess(), retrievedSettings.getDecisionProcess());
    }

    @Test
    public void getRoadtripSettingsById_userNotMember_throwsForbidden() {
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
        testRoadtrip.setOwner(createdOwner);
        Roadtrip createdRoadtrip = roadtripRepository.save(testRoadtrip);

        // Create settings
        roadtripSettingsService.createRoadtripSettings(createdRoadtrip);

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            roadtripSettingsService.getRoadtripSettingsById(createdRoadtrip.getRoadtripId(), createdNonMember);
        });
    }

    @Test
    public void updateRoadtripSettingsById_validInputs_success() {
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
        testRoadtrip.setOwner(createdUser);
        Roadtrip createdRoadtrip = roadtripRepository.save(testRoadtrip);

        // Create settings
        RoadtripSettings settings = roadtripSettingsService.createRoadtripSettings(createdRoadtrip);

        // Update settings
        RoadtripSettings updatedSettings = new RoadtripSettings();
        updatedSettings.setBasemapType(BasemapType.SATELLITE);
        updatedSettings.setDecisionProcess(DecisionProcess.OWNER_DECISION);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(10);
        updatedSettings.setStartDate(startDate);
        updatedSettings.setEndDate(endDate);

        // When
        roadtripSettingsService.updateRoadtripSettingsById(
                createdRoadtrip.getRoadtripId(), updatedSettings, createdUser);

        // Then
        RoadtripSettings retrievedSettings = roadtripSettingsRepository
                .findByRoadtrip_RoadtripId(createdRoadtrip.getRoadtripId()).orElse(null);

        assertNotNull(retrievedSettings);
        assertEquals(BasemapType.SATELLITE, retrievedSettings.getBasemapType());
        assertEquals(DecisionProcess.OWNER_DECISION, retrievedSettings.getDecisionProcess());
        assertEquals(startDate, retrievedSettings.getStartDate());
        assertEquals(endDate, retrievedSettings.getEndDate());
    }

    @Test
    public void updateRoadtripSettingsById_userNotOwner_throwsForbidden() {
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
        testRoadtrip.setOwner(createdOwner);
        Roadtrip createdRoadtrip = roadtripRepository.save(testRoadtrip);

        // Create settings
        roadtripSettingsService.createRoadtripSettings(createdRoadtrip);

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

        // Update settings
        RoadtripSettings updatedSettings = new RoadtripSettings();
        updatedSettings.setBasemapType(BasemapType.SATELLITE);

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            roadtripSettingsService.updateRoadtripSettingsById(
                    createdRoadtrip.getRoadtripId(), updatedSettings, createdMember);
        });
    }