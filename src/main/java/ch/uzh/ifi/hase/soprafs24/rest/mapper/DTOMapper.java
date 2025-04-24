package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterestComment;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripSettings;
import ch.uzh.ifi.hase.soprafs24.entity.Route;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestCommentGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestCommentPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripSettingsGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripSettingsPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RouteDeleteDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RouteGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoutePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.entity.Checklist;
import ch.uzh.ifi.hase.soprafs24.entity.ChecklistElement;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistElementGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistElementPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistPostDTO;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.LineString;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  // User to PostDTO mapper
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "phoneNumber", target = "phoneNumber")
  @Mapping(source = "mail", target = "mail")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "receiveNotifications", target = "receiveNotifications")
  @Mapping(source = "userPreferences", target = "userPreferences")
  @Mapping(source = "status", target = "status")
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "userId", ignore = true)
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "token", target = "token")
  UserGetDTO convertEntityToUserGetCredentials(User user);

  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "firstName", target = "firstName")
  @Mapping(source = "lastName", target = "lastName")
  @Mapping(source = "phoneNumber", target = "phoneNumber")
  @Mapping(source = "mail", target = "mail")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "receiveNotifications", target = "receiveNotifications")
  @Mapping(source = "userPreferences", target = "userPreferences")
  UserGetDTO convertEntityToUserGetDTO(User user);

  // Roadtrip mappings
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(target = "owner", ignore = true)
  @Mapping(target = "roadtripId", ignore = true)
  Roadtrip convertRoadtripPostDTOtoEntity(RoadtripPostDTO roadtripPostDTO);

  @Mapping(source = "roadtripId", target = "roadtripId")
  @Mapping(source = "owner.userId", target = "ownerId")
  @Mapping(source = "", target = "invitationStatus")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  RoadtripGetDTO convertEntityToRoadtripGetDTO(Roadtrip roadtrip);

  // Roadtrip Member mappings
  // If we do the lookup directly in the service we can use ignore = true
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "roadtrip", ignore = true)
  @Mapping(target = "roadtripMemberId", ignore = true)
  @Mapping(target = "invitationStatus", ignore = true)
  RoadtripMember convertRoadtripMemberPostDTOtoEntity(RoadtripMemberPostDTO roadtripMemberPostDTO);

  @Mapping(source = "roadtripMemberId.userId", target = "userId")
  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "roadtripMemberId.roadtripId", target = "roadtripId")
  @Mapping(source = "invitationStatus", target = "invitationStatus")
  RoadtripMemberGetDTO convertEntityToRoadtripMemberGetDTO(RoadtripMember roadtripMember);

  @Mapping(target = "roadtripMemberId", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "roadtrip", ignore = true)
  @Mapping(source = "invitationStatus", target = "invitationStatus")
  RoadtripMember convertRoadtripMemberPutDTOtoEntity(RoadtripMemberPutDTO roadtripMemberPutDTO);

  // RoadtripSettings mappings
  @Mapping(source = "roadtripSettingsId", target = "roadtripSettingsId")
  @Mapping(source = "roadtrip.roadtripId", target = "roadtripId") // Map roadtripId from Roadtrip entity
  @Mapping(source = "basemapType", target = "basemapType", qualifiedByName = "stringToBasemapType")
  @Mapping(source = "decisionProcess", target = "decisionProcess", qualifiedByName = "stringToDecisionProcess")
  @Mapping(source = "boundingBox", target = "boundingBox", qualifiedByName = "polygonToGeoJsonNode")
  @Mapping(source = "startDate", target = "startDate")
  @Mapping(source = "endDate", target = "endDate")
  RoadtripSettingsGetDTO convertEntityToRoadtripSettingsGetDTO(RoadtripSettings roadtripSettings);

  @Mapping(target = "roadtripSettingsId", ignore = true)
  @Mapping(target = "roadtrip", ignore = true)
  @Mapping(source = "basemapType", target = "basemapType", qualifiedByName = "stringToBasemapType")
  @Mapping(source = "decisionProcess", target = "decisionProcess", qualifiedByName = "stringToDecisionProcess")
  @Mapping(source = "boundingBox", target = "boundingBox", qualifiedByName = "mapGeoJsonToPolygon")
  @Mapping(source = "startDate", target = "startDate", qualifiedByName = "stringToLocalDate")
  @Mapping(source = "endDate", target = "endDate", qualifiedByName = "stringToLocalDate")
  RoadtripSettings convertRoadtripSettingsPutDTOtoEntity(RoadtripSettingsPutDTO roadtripSettingsPutDTO);

  public static final ObjectMapper objectMapper = new ObjectMapper();

  @Named("polygonToGeoJsonNode")
  public static JsonNode polygonToGeoJsonNode(Polygon polygon) {
    if (polygon == null) {
      return null; // Return null if the input polygon is null
    }
    try {
      GeoJsonWriter writer = new GeoJsonWriter();
      String geoJson = writer.write(polygon);
      return new ObjectMapper().readTree(geoJson);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert Polygon to GeoJSON", e);
    }
  }

  @Named("mapGeoJsonToPolygon")
  public static Polygon mapGeoJsonToPolygon(JsonNode geoJsonNode) {
    if (geoJsonNode == null) {
      return null; // Return null if the input JsonNode is null
    }
    try {
      // GeojsonReader expects a String
      String geoJson = objectMapper.writeValueAsString(geoJsonNode);
      GeoJsonReader reader = new GeoJsonReader();
      Geometry geometry = reader.read(geoJson);

      if (!(geometry instanceof Polygon)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Provided geometry is not a Polygon");
      }

      return (Polygon) geometry;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid GeoJSON format");
    }
  }

  @Mapping(target = "poiId", ignore = true)
  @Mapping(target = "roadtrip", ignore = true)
  @Mapping(target = "upvotes", ignore = true)
  @Mapping(target = "downvotes", ignore = true)
  @Mapping(target = "pointOfInterestComments", ignore = true)
  @Mapping(target = "pointOfInterestComment", ignore = true)
  @Mapping(source = "creatorId", target = "creatorId")
  @Mapping(source = "coordinate", target = "coordinate", qualifiedByName = "mapGeoJsonToPoint")
  PointOfInterest convertPointOfInterestPostDTOToEntity(PointOfInterestPostDTO pointOfInterestPostDTO);

  @Mapping(source = "creatorId", target = "creatorId")
  @Mapping(source = "coordinate", target = "coordinate", qualifiedByName = "pointToGeoJsonNode")
  PointOfInterestGetDTO convertEntityToPointOfInterestGetDTO(PointOfInterest pointOfInterest);

  @Named("mapGeoJsonToPoint")
  public static Point mapGeoJsonToPoint(JsonNode geoJsonNode) {
    if (geoJsonNode == null) {
      return null; // Return null if the input JsonNode is null
    }
    try {
      // GeojsonReader expects a String
      String geoJson = objectMapper.writeValueAsString(geoJsonNode);
      GeoJsonReader reader = new GeoJsonReader();
      Geometry geometry = reader.read(geoJson);

      if (!(geometry instanceof Point)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Provided geometry is not a Point");
      }

      return (Point) geometry;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid GeoJSON format");
    }
  }

  @Named("pointToGeoJsonNode")
  public static JsonNode pointToGeoJsonNode(Point point) {
    if (point == null) {
      return null; // Return null if the input point is null
    }
    try {
      GeoJsonWriter writer = new GeoJsonWriter();
      String geoJson = writer.write(point);
      return new ObjectMapper().readTree(geoJson);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert Point to GeoJSON", e);
    }
  }

  @Named("stringToLocalDate")
  public static java.time.LocalDate stringToLocalDate(String dateString) {
    if (dateString == null || dateString.isEmpty()) {
      return null;
    }
    try {
      return java.time.LocalDate.parse(dateString);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid date format: " + dateString + ". Expected format: YYYY-MM-DD");
    }
  }

  @Named("stringToBasemapType")
  public static ch.uzh.ifi.hase.soprafs24.constant.BasemapType stringToBasemapType(String basemapType) {
    if (basemapType == null) {
      return null;
    }
    try {
      return ch.uzh.ifi.hase.soprafs24.constant.BasemapType.valueOf(basemapType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid basemapType: " + basemapType
              + ". Valid values are: SATELLITE, SATELLITE_HYBRID, OPEN_STREET_MAP, DEFAULT");
    }
  }

  @Named("stringToDecisionProcess")
  public static ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess stringToDecisionProcess(String decisionProcess) {
    if (decisionProcess == null) {
      return null;
    }
    try {
      return ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess.valueOf(decisionProcess.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid decisionProcess: " + decisionProcess + ". Valid values are: MAJORITY, OWNER_DECISION, DEFAULT");
    }
  }

  // ChecklistElement mappings
  @Mapping(source = "checklistElementId", target = "checklistElementId")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "isCompleted", target = "isCompleted")
  @Mapping(source = "assignedUser.username", target = "assignedUser")
  @Mapping(source = "priority", target = "priority")
  @Mapping(source = "category", target = "category")
  ChecklistElementGetDTO convertEntityToChecklistElementGetDTO(ChecklistElement checklistElement);

  @Mapping(target = "checklistElementId", ignore = true)
  @Mapping(target = "checklist", ignore = true)
  @Mapping(target = "assignedUser", ignore = true)
  @Mapping(source = "name", target = "name")
  @Mapping(source = "isCompleted", target = "isCompleted")
  @Mapping(source = "assignedUser", target = "assignedUser.username")
  @Mapping(source = "priority", target = "priority")
  @Mapping(source = "category", target = "category")
  ChecklistElement convertChecklistElementPostDTOToEntity(ChecklistElementPostDTO postDTO);

  // Checklist mappings
  @Mapping(source = "roadtripId", target = "roadtripId")
  @Mapping(source = "checklistElements", target = "checklistElements")
  ChecklistGetDTO convertEntityToChecklistGetDTO(Checklist checklist);

  @Mapping(target = "roadtripId", ignore = true)
  @Mapping(target = "roadtrip", ignore = true)
  @Mapping(source = "checklistElements", target = "checklistElements")
  Checklist convertChecklistPostDTOToEntity(ChecklistPostDTO postDTO);

  @Mapping(target = "commentId", ignore = true)
  @Mapping(target = "authorId", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "poi", ignore = true)
  @Mapping(source = "comment", target = "comment")
  PointOfInterestComment converPointOfInterestCommentPostDTOToEntity(
      PointOfInterestCommentPostDTO pointOfInterestCommentPostDTO);

  @Mapping(source = "commentId", target = "commentId")
  @Mapping(source = "authorId", target = "authorId")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "poi", target = "poi")
  @Mapping(source = "comment", target = "comment")
  PointOfInterestCommentGetDTO convertEntityToPointOfInterestCommentGetDTO(
      PointOfInterestComment pointOfInterestComment);

  // Map RoutePostDTO to Route entity
  @Mapping(target = "route", ignore = true) // Ignore the route field
  @Mapping(target = "status", ignore = true) // Status is not set during creation
  Route convertRoutePostDTOToEntity(RoutePostDTO routePostDTO);

  // Map Route entity to RouteGetDTO
  @Mapping(source = "route", target = "route", qualifiedByName = "lineStringToGeoJson")
  RouteGetDTO convertEntityToRouteGetDTO(Route route);

  // Map RouteDeleteDTO to Route entity (if needed)
  @Mapping(target = "route", ignore = true) // Route geometry is not needed for deletion
  @Mapping(target = "distance", ignore = true)
  @Mapping(target = "travelTime", ignore = true)
  @Mapping(target = "travelMode", ignore = true)
  @Mapping(target = "status", ignore = true)
  Route convertRouteDeleteDTOToEntity(RouteDeleteDTO routeDeleteDTO);

  @Named("lineStringToGeoJson")
  public static String lineStringToGeoJson(LineString lineString) {
    if (lineString == null) {
      return null;
    }
    try {
      GeoJsonWriter writer = new GeoJsonWriter();
      return writer.write(lineString);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert LineString to GeoJSON", e);
    }
  }

  @Named("geoJsonToLineString")
  public static LineString geoJsonToLineString(String geoJson) {
    if (geoJson == null) {
      return null;
    }
    try {
      GeoJsonReader reader = new GeoJsonReader();
      Geometry geometry = reader.read(geoJson);

      if (!(geometry instanceof LineString)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided geometry is not a LineString");
      }

      return (LineString) geometry;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid GeoJSON format");
    }
  }
}
