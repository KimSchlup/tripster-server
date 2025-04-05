package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.User;


import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;


import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

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
  Roadtrip convertRoadtripPostDTOtoEntity(RoadtripPostDTO roadtripPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  RoadtripGetDTO convertEntityToRoadtripGetDTO(Roadtrip roadtrip);

}
