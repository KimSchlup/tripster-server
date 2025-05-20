package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.BasemapType;
import ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

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
        // Given
        User user = createTestUser("testUsername");
        Roadtrip roadtrip = createTestRoadtrip(user);

        // When
        RoadtripSettings createdSettings = roadtripSettingsService.createRoadtripSettings(roadtrip);

        // Then
        assertNotNull(createdSettings);
        assertEquals(roadtrip.getRoadtripId(), createdSettings.getRoadtrip().getRoadtripId());
        assertEquals(BasemapType.OPEN_STREET_MAP, createdSettings.getBasemapType());
        assertEquals(DecisionProcess.MAJORITY, createdSettings.getDecisionProcess());
        assertNotNull(createdSettings.getStartDate());
        assertNotNull(createdSettings.getEndDate());
    }

    @Test
    public void getRoadtripSettingsById_validId_returnsSettings() {
        // Given
        User user = createTestUser("testUsername");
        Roadtrip roadtrip = createTestRoadtrip(user);
        RoadtripSettings settings = roadtripSettingsService.createRoadtripSettings(roadtrip);

        // When
        RoadtripSettings retrievedSettings = roadtripSettingsService.getRoadtripSettingsById(
                roadtrip.getRoadtripId(), user);

        // Then
        assertEquals(settings.getRoadtripSettingsId(), retrievedSettings.getRoadtripSettingsId());
        assertEquals(settings.getBasemapType(), retrievedSettings.getBasemapType());
        assertEquals(settings.getDecisionProcess(), retrievedSettings.getDecisionProcess());
    }

    @Test
    public void getRoadtripSettingsById_userNotMember_throwsForbidden() {
        // Given
        User owner = createTestUser("owner");
        User nonMember = createTestUser("nonMember");

        Roadtrip roadtrip = createTestRoadtrip(owner);
        roadtripSettingsService.createRoadtripSettings(roadtrip);

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            roadtripSettingsService.getRoadtripSettingsById(roadtrip.getRoadtripId(), nonMember);
        });
    }

    @Test
    public void updateRoadtripSettingsById_validInputs_success() {
        // Given
        User user = createTestUser("testUsername");
        Roadtrip roadtrip = createTestRoadtrip(user);
        roadtripSettingsService.createRoadtripSettings(roadtrip);

        RoadtripSettings updatedSettings = new RoadtripSettings();
        updatedSettings.setBasemapType(BasemapType.SATELLITE);
        updatedSettings.setDecisionProcess(DecisionProcess.OWNER_DECISION);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(10);
        updatedSettings.setStartDate(startDate);
        updatedSettings.setEndDate(endDate);

        // When
        roadtripSettingsService.updateRoadtripSettingsById(
                roadtrip.getRoadtripId(), updatedSettings, user);

        // Then
        RoadtripSettings retrievedSettings = roadtripSettingsRepository
                .findByRoadtrip_RoadtripId(roadtrip.getRoadtripId()).orElse(null);

        assertNotNull(retrievedSettings);
        assertEquals(BasemapType.SATELLITE, retrievedSettings.getBasemapType());
        assertEquals(DecisionProcess.OWNER_DECISION, retrievedSettings.getDecisionProcess());
        assertEquals(startDate, retrievedSettings.getStartDate());
        assertEquals(endDate, retrievedSettings.getEndDate());
    }

    @Test
    public void updateRoadtripSettingsById_userNotOwner_throwsForbidden() {
        // Given
        User owner = createTestUser("owner");
        User member = createTestUser("member");

        Roadtrip roadtrip = createTestRoadtrip(owner);
        roadtripSettingsService.createRoadtripSettings(roadtrip);

        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(member.getUserId());
        pk.setRoadtripId(roadtrip.getRoadtripId());

        RoadtripMember roadtripMember = new RoadtripMember();
        roadtripMember.setRoadtripMemberId(pk);
        roadtripMember.setUser(member);
        roadtripMember.setRoadtrip(roadtrip);
        roadtripMember.setInvitationStatus(InvitationStatus.ACCEPTED);
        roadtripMemberRepository.save(roadtripMember);

        RoadtripSettings updatedSettings = new RoadtripSettings();
        updatedSettings.setBasemapType(BasemapType.SATELLITE);

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            roadtripSettingsService.updateRoadtripSettingsById(
                    roadtrip.getRoadtripId(), updatedSettings, member);
        });
    }

    @Test
    public void uploadRoadtripImage_validInput_success() throws Exception {
        // Given
        User user = createTestUser("uploadUser");
        Roadtrip roadtrip = createTestRoadtrip(user);
        roadtripSettingsService.createRoadtripSettings(roadtrip);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "dummy image content".getBytes());

        // When
        String imageName = roadtripSettingsService.uploadRoadtripImage(
                mockFile, "mapmates-object-store", roadtrip.getRoadtripId(), user);

        // Then
        assertNotNull(imageName);
    }

    @Test
    public void uploadRoadtripImage_userNotOwner_throwsForbidden() {
        // Given
        User owner = createTestUser("ownerUpload");
        User nonOwner = createTestUser("nonOwnerUpload");
        Roadtrip roadtrip = createTestRoadtrip(owner);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "content".getBytes());

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            roadtripSettingsService.uploadRoadtripImage(
                    mockFile, "mapmates-object-store", roadtrip.getRoadtripId(), nonOwner);
        });
    }

    @Test
    public void downloadRoadtripImage_validInput_success() {
        // Given
        User user = createTestUser("downloadUser");
        Roadtrip roadtrip = createTestRoadtrip(user);
        roadtripSettingsService.createRoadtripSettings(roadtrip);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "sample.jpg", "image/jpeg", "data".getBytes());
        roadtripSettingsService.uploadRoadtripImage(mockFile, "mapmates-object-store", roadtrip.getRoadtripId(), user);

        // When
        String imageName = roadtripSettingsService.getRoadtripImageName(roadtrip.getRoadtripId(), user);

        // Then
        assertNotNull(imageName);
    }

    @Test
    public void downloadRoadtripImage_userNotOwner_throwsForbidden() {
        // Given
        User owner = createTestUser("ownerDownload");
        User nonOwner = createTestUser("nonOwnerDownload");
        Roadtrip roadtrip = createTestRoadtrip(owner);

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            roadtripSettingsService.getRoadtripImageName(roadtrip.getRoadtripId(), nonOwner);
        });
    }

    @Test
    public void deleteRoadtripImage_validInput_success() {
        // Given
        User user = createTestUser("deleteUser");
        Roadtrip roadtrip = createTestRoadtrip(user);
        roadtripSettingsService.createRoadtripSettings(roadtrip);

        MockMultipartFile file = new MockMultipartFile("file", "delete.jpg", "image/jpeg", "test".getBytes());
        roadtripSettingsService.uploadRoadtripImage(file, "mapmates-object-store", roadtrip.getRoadtripId(), user);

        // When/Then
        assertDoesNotThrow(() -> {
            roadtripSettingsService.deleteRoadtripImage(roadtrip.getRoadtripId(), "mapmates-object-store", user);
        });
    }

    @Test
    public void deleteRoadtripImage_userNotOwner_throwsForbidden() {
        // Given
        User owner = createTestUser("ownerDelete");
        User nonOwner = createTestUser("nonOwnerDelete");
        Roadtrip roadtrip = createTestRoadtrip(owner);

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            roadtripSettingsService.deleteRoadtripImage(roadtrip.getRoadtripId(), "mapmates-object-store", nonOwner);
        });
    }

    // Helper methods
    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setCreationDate(LocalDate.now());
        user.setStatus(UserStatus.ONLINE);
        user.setToken("token-" + UUID.randomUUID());
        return userService.createUser(user);
    }

    private Roadtrip createTestRoadtrip(User owner) {
        Roadtrip roadtrip = new Roadtrip();
        roadtrip.setName("Test Trip");
        roadtrip.setOwner(owner);
        return roadtripRepository.save(roadtrip);
    }
}
