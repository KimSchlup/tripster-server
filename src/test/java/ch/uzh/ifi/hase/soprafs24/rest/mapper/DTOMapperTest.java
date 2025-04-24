package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.BasemapType;
import ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  private final GeometryFactory geometryFactory = new GeometryFactory();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setFirstName("name");
    userPostDTO.setLastName("lastname");
    userPostDTO.setUsername("username");
    userPostDTO.setPassword("password");
    userPostDTO.setPhoneNumber("1234567890");
    userPostDTO.setMail("test@example.com");
    userPostDTO.setReceiveNotifications(true);

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getFirstName(), user.getFirstName());
    assertEquals(userPostDTO.getLastName(), user.getLastName());
    assertEquals(userPostDTO.getUsername(), user.getUsername());
    assertEquals(userPostDTO.getPassword(), user.getPassword());
    assertEquals(userPostDTO.getPhoneNumber(), user.getPhoneNumber());
    assertEquals(userPostDTO.getMail(), user.getMail());
    assertEquals(userPostDTO.getReceiveNotifications(), user.getReceiveNotifications());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setUserId(1L);
    user.setFirstName("Firstname");
    user.setLastName("lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setPhoneNumber("1234567890");
    user.setMail("test@example.com");
    user.setCreationDate(LocalDate.now());
    user.setReceiveNotifications(true);
    UserPreferences preferences = new UserPreferences();
    user.setUserPreferences(preferences);

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getUserId(), userGetDTO.getUserId());
    assertEquals(user.getFirstName(), userGetDTO.getFirstName());
    assertEquals(user.getLastName(), userGetDTO.getLastName());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
    assertEquals(user.getToken(), userGetDTO.getToken());
    assertEquals(user.getPhoneNumber(), userGetDTO.getPhoneNumber());
    assertEquals(user.getMail(), userGetDTO.getMail());
    assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
    assertEquals(user.getReceiveNotifications(), userGetDTO.getReceiveNotifications());
    assertNotNull(userGetDTO.getUserPreferences());
  }

  @Test
  public void testGetUserCredentials_fromUser_toUserGetCredentials_success() {
    // create User
    User user = new User();
    user.setUserId(1L);
    user.setUsername("username");
    user.setToken("token123");

    // MAP -> Create UserGetDTO for credentials
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetCredentials(user);

    // check content
    assertEquals(user.getUserId(), userGetDTO.getUserId());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getToken(), userGetDTO.getToken());

    // Other fields should be null
    assertNull(userGetDTO.getFirstName());
    assertNull(userGetDTO.getLastName());
    assertNull(userGetDTO.getStatus());
  }

  @Test
  public void testCreateRoadtrip_fromRoadtripPostDTO_toRoadtrip_success() {
    // create RoadtripPostDTO
    RoadtripPostDTO roadtripPostDTO = new RoadtripPostDTO();
    roadtripPostDTO.setName("Test Roadtrip");
    roadtripPostDTO.setDescription("Test Description");

    // MAP -> Create roadtrip
    Roadtrip roadtrip = DTOMapper.INSTANCE.convertRoadtripPostDTOtoEntity(roadtripPostDTO);

    // check content
    assertEquals(roadtripPostDTO.getName(), roadtrip.getName());
    assertEquals(roadtripPostDTO.getDescription(), roadtrip.getDescription());
    assertNull(roadtrip.getOwner());
    assertNull(roadtrip.getRoadtripId());
  }

  @Test
  public void testGetRoadtrip_fromRoadtrip_toRoadtripGetDTO_success() {
    // create Roadtrip
    Roadtrip roadtrip = new Roadtrip();
    roadtrip.setRoadtripId(1L);
    roadtrip.setName("Test Roadtrip");
    roadtrip.setDescription("Test Description");

    User owner = new User();
    owner.setUserId(2L);
    roadtrip.setOwner(owner);

    // MAP -> Create RoadtripGetDTO
    RoadtripGetDTO roadtripGetDTO = DTOMapper.INSTANCE.convertEntityToRoadtripGetDTO(roadtrip);

    // check content
    assertEquals(roadtrip.getRoadtripId(), roadtripGetDTO.getRoadtripId());
    assertEquals(roadtrip.getName(), roadtripGetDTO.getName());
    assertEquals(roadtrip.getDescription(), roadtripGetDTO.getDescription());
    assertEquals(owner.getUserId(), roadtripGetDTO.getOwnerId());
    assertNull(roadtripGetDTO.getInvitationStatus()); // This is set separately in the service
  }

  @Test
  public void testCreateRoadtripMember_fromRoadtripMemberPostDTO_toRoadtripMember_success() {
    // create RoadtripMemberPostDTO
    RoadtripMemberPostDTO roadtripMemberPostDTO = new RoadtripMemberPostDTO();
    roadtripMemberPostDTO.setUsername("testuser");

    // MAP -> Create roadtripMember
    RoadtripMember roadtripMember = DTOMapper.INSTANCE.convertRoadtripMemberPostDTOtoEntity(roadtripMemberPostDTO);

    // All fields should be null as they are ignored in the mapping
    assertNull(roadtripMember.getUser());
    assertNull(roadtripMember.getRoadtrip());
    assertNull(roadtripMember.getRoadtripMemberId());
    assertNull(roadtripMember.getInvitationStatus());
  }

  @Test
  public void testGetRoadtripMember_fromRoadtripMember_toRoadtripMemberGetDTO_success() {
    // create RoadtripMember
    RoadtripMember roadtripMember = new RoadtripMember();

    User user = new User();
    user.setUserId(1L);
    user.setUsername("testuser");
    roadtripMember.setUser(user);

    Roadtrip roadtrip = new Roadtrip();
    roadtrip.setRoadtripId(2L);
    roadtripMember.setRoadtrip(roadtrip);

    RoadtripMemberPK pk = new RoadtripMemberPK();
    pk.setUserId(1L);
    pk.setRoadtripId(2L);
    roadtripMember.setRoadtripMemberId(pk);

    roadtripMember.setInvitationStatus(InvitationStatus.ACCEPTED);

    // MAP -> Create RoadtripMemberGetDTO
    RoadtripMemberGetDTO roadtripMemberGetDTO = DTOMapper.INSTANCE.convertEntityToRoadtripMemberGetDTO(roadtripMember);

    // check content
    assertEquals(user.getUserId(), roadtripMemberGetDTO.getUserId());
    assertEquals(user.getUsername(), roadtripMemberGetDTO.getUsername());
    assertEquals(roadtrip.getRoadtripId(), roadtripMemberGetDTO.getRoadtripId());
    assertEquals(InvitationStatus.ACCEPTED, roadtripMemberGetDTO.getInvitationStatus());
  }

  @Test
  public void testUpdateRoadtripMember_fromRoadtripMemberPutDTO_toRoadtripMember_success() {
    // create RoadtripMemberPutDTO
    RoadtripMemberPutDTO roadtripMemberPutDTO = new RoadtripMemberPutDTO();
    roadtripMemberPutDTO.setInvitationStatus(InvitationStatus.ACCEPTED);

    // MAP -> Update roadtripMember
    RoadtripMember roadtripMember = DTOMapper.INSTANCE.convertRoadtripMemberPutDTOtoEntity(roadtripMemberPutDTO);

    // check content
    assertEquals(InvitationStatus.ACCEPTED, roadtripMember.getInvitationStatus());
    assertNull(roadtripMember.getRoadtripMemberId());
    assertNull(roadtripMember.getUser());
    assertNull(roadtripMember.getRoadtrip());
  }

  @Test
  public void testGetRoadtripSettings_fromRoadtripSettings_toRoadtripSettingsGetDTO_success() {
    // create RoadtripSettings
    RoadtripSettings roadtripSettings = new RoadtripSettings();
    roadtripSettings.setRoadtripSettingsId(1L);

    Roadtrip roadtrip = new Roadtrip();
    roadtrip.setRoadtripId(2L);
    roadtripSettings.setRoadtrip(roadtrip);

    roadtripSettings.setBasemapType(BasemapType.SATELLITE);
    roadtripSettings.setDecisionProcess(DecisionProcess.MAJORITY);

    // Create a simple polygon for testing
    Coordinate[] coordinates = new Coordinate[] {
        new Coordinate(0, 0),
        new Coordinate(0, 1),
        new Coordinate(1, 1),
        new Coordinate(1, 0),
        new Coordinate(0, 0)
    };
    Polygon polygon = geometryFactory.createPolygon(coordinates);
    roadtripSettings.setBoundingBox(polygon);

    roadtripSettings.setStartDate(LocalDate.of(2025, 1, 1));
    roadtripSettings.setEndDate(LocalDate.of(2025, 1, 10));

    // MAP -> Create RoadtripSettingsGetDTO
    RoadtripSettingsGetDTO roadtripSettingsGetDTO = DTOMapper.INSTANCE
        .convertEntityToRoadtripSettingsGetDTO(roadtripSettings);

    // check content
    assertEquals(roadtripSettings.getRoadtripSettingsId(), roadtripSettingsGetDTO.getRoadtripSettingsId());
    assertEquals(roadtrip.getRoadtripId(), roadtripSettingsGetDTO.getRoadtripId());
    assertEquals(BasemapType.SATELLITE, roadtripSettingsGetDTO.getBasemapType());
    assertEquals(DecisionProcess.MAJORITY, roadtripSettingsGetDTO.getDecisionProcess());
    assertNotNull(roadtripSettingsGetDTO.getBoundingBox());
    assertEquals(roadtripSettings.getStartDate(), roadtripSettingsGetDTO.getStartDate());
    assertEquals(roadtripSettings.getEndDate(), roadtripSettingsGetDTO.getEndDate());
  }

  @Test
  public void testStringToLocalDate_validInput_success() {
    // Test valid date string
    LocalDate result = DTOMapper.stringToLocalDate("2025-01-01");
    assertEquals(LocalDate.of(2025, 1, 1), result);
  }

  @Test
  public void testStringToLocalDate_nullInput_returnsNull() {
    // Test null input
    LocalDate result = DTOMapper.stringToLocalDate(null);
    assertNull(result);
  }

  @Test
  public void testStringToLocalDate_emptyInput_returnsNull() {
    // Test empty string
    LocalDate result = DTOMapper.stringToLocalDate("");
    assertNull(result);
  }

  @Test
  public void testStringToLocalDate_invalidInput_throwsException() {
    // Test invalid date format
    assertThrows(ResponseStatusException.class, () -> {
      DTOMapper.stringToLocalDate("01/01/2025");
    });
  }

  @Test
  public void testStringToBasemapType_validInput_success() {
    // Test valid basemap type
    BasemapType result = DTOMapper.stringToBasemapType("SATELLITE");
    assertEquals(BasemapType.SATELLITE, result);
  }

  @Test
  public void testStringToBasemapType_caseInsensitive_success() {
    // Test case insensitivity
    BasemapType result = DTOMapper.stringToBasemapType("satellite");
    assertEquals(BasemapType.SATELLITE, result);
  }

  @Test
  public void testStringToBasemapType_nullInput_returnsNull() {
    // Test null input
    BasemapType result = DTOMapper.stringToBasemapType(null);
    assertNull(result);
  }

  @Test
  public void testStringToBasemapType_invalidInput_throwsException() {
    // Test invalid basemap type
    assertThrows(ResponseStatusException.class, () -> {
      DTOMapper.stringToBasemapType("INVALID_TYPE");
    });
  }

  @Test
  public void testStringToDecisionProcess_validInput_success() {
    // Test valid decision process
    DecisionProcess result = DTOMapper.stringToDecisionProcess("MAJORITY");
    assertEquals(DecisionProcess.MAJORITY, result);
  }

  @Test
  public void testStringToDecisionProcess_caseInsensitive_success() {
    // Test case insensitivity
    DecisionProcess result = DTOMapper.stringToDecisionProcess("majority");
    assertEquals(DecisionProcess.MAJORITY, result);
  }

  @Test
  public void testStringToDecisionProcess_nullInput_returnsNull() {
    // Test null input
    DecisionProcess result = DTOMapper.stringToDecisionProcess(null);
    assertNull(result);
  }

  @Test
  public void testStringToDecisionProcess_invalidInput_throwsException() {
    // Test invalid decision process
    assertThrows(ResponseStatusException.class, () -> {
      DTOMapper.stringToDecisionProcess("INVALID_PROCESS");
    });
  }

  @Test
  public void testPointToGeoJsonNode_validInput_success() throws Exception {
    // Create a point
    Point point = geometryFactory.createPoint(new Coordinate(10.0, 20.0));

    // Convert to GeoJSON
    JsonNode jsonNode = DTOMapper.pointToGeoJsonNode(point);

    // Verify the result
    assertNotNull(jsonNode);
    assertEquals("Point", jsonNode.get("type").asText());
    assertEquals(10.0, jsonNode.get("coordinates").get(0).asDouble(), 0.001);
    assertEquals(20.0, jsonNode.get("coordinates").get(1).asDouble(), 0.001);
  }

  @Test
  public void testPointToGeoJsonNode_nullInput_returnsNull() {
    // Test null input
    JsonNode result = DTOMapper.pointToGeoJsonNode(null);
    assertNull(result);
  }

  @Test
  public void testPolygonToGeoJsonNode_validInput_success() throws Exception {
    // Create a polygon
    Coordinate[] coordinates = new Coordinate[] {
        new Coordinate(0, 0),
        new Coordinate(0, 1),
        new Coordinate(1, 1),
        new Coordinate(1, 0),
        new Coordinate(0, 0)
    };
    Polygon polygon = geometryFactory.createPolygon(coordinates);

    // Convert to GeoJSON
    JsonNode jsonNode = DTOMapper.polygonToGeoJsonNode(polygon);

    // Verify the result
    assertNotNull(jsonNode);
    assertEquals("Polygon", jsonNode.get("type").asText());
    assertTrue(jsonNode.get("coordinates").isArray());
    assertTrue(jsonNode.get("coordinates").get(0).isArray());
    assertEquals(5, jsonNode.get("coordinates").get(0).size());
  }

  @Test
  public void testPolygonToGeoJsonNode_nullInput_returnsNull() {
    // Test null input
    JsonNode result = DTOMapper.polygonToGeoJsonNode(null);
    assertNull(result);
  }
}
