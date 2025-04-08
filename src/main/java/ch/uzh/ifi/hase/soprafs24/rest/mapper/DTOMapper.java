package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripSettings;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestPutDTO;

import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripSettingsGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripSettingsPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
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
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  RoadtripGetDTO convertEntityToRoadtripGetDTO(Roadtrip roadtrip);

  // Roadtrip mappings
  // If we do the lookup directly in the service we can use ignore = true
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "roadtrip", ignore = true)
  @Mapping(target = "roadtripMemberId", ignore = true)
  @Mapping(target = "invitationStatus", ignore = true)
  RoadtripMember convertRoadtripMemberPostDTOtoEntity(RoadtripMemberPostDTO roadtripMemberPostDTO);

  @Mapping(source = "roadtripMemberId.userId", target = "userId")
  @Mapping(source = "roadtripMemberId.roadtripId", target = "roadtripId")
  @Mapping(source = "invitationStatus", target = "invitationStatus")
  RoadtripMemberGetDTO convertEntityToRoadtripMemberGetDTO(RoadtripMember roadtripMember);

  // RoadtripSettings mappings
  @Mapping(source = "roadtripSettingsId", target = "roadtripSettingsId")
  @Mapping(source = "roadtrip.roadtripId", target = "roadtripId") // Map roadtripId from Roadtrip entity
  @Mapping(source = "basemapType", target = "basemapType")
  @Mapping(source = "decisionProcess", target = "decisionProcess")
  @Mapping(source = "boundingBox", target = "boundingBox", qualifiedByName = "polygonToGeoJsonNode")
  @Mapping(source = "startDate", target = "startDate")
  @Mapping(source = "endDate", target = "endDate")
  RoadtripSettingsGetDTO convertEntityToRoadtripSettingsGetDTO(RoadtripSettings roadtripSettings);

  @Mapping(target = "roadtripSettingsId", ignore = true)
  @Mapping(target = "roadtrip.roadtripId", ignore = true)
  @Mapping(source = "basemapType", target = "basemapType")
  @Mapping(source = "decisionProcess", target = "decisionProcess")
  @Mapping(source = "boundingBox", target = "boundingBox", qualifiedByName = "mapGeoJsonToPolygon")
  @Mapping(source = "startDate", target = "startDate")
  @Mapping(source = "endDate", target = "endDate")
  RoadtripSettings convertRoadtripSettingsPutDTOtoEntity(RoadtripSettingsPutDTO roadtripSettingsPutDTO);

  public static final ObjectMapper objectMapper = new ObjectMapper();

  @Named("polygonToGeoJsonNode")
  public static JsonNode polygonToGeoJsonNode(Polygon polygon) {
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
  @Mapping(target = "user", ignore = true)
  PointOfInterest convertPointOfInterestPostDTOToEntity(PointOfInterestPostDTO pointOfInterestPostDTO);
  
  PointOfInterestGetDTO convertEntityToPointOfInterestGetDTO(PointOfInterest pointOfInterest);


  @Mapping(target = "poiId", ignore = true)
  @Mapping(target = "roadtrip", ignore = true)
  @Mapping(target = "user", ignore = true)
  PointOfInterest convertPointOfInterestPutDTOToEntity(PointOfInterestPutDTO pointOfInterestPutDTO);
}
