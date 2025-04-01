package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserEmergencyContact;
import ch.uzh.ifi.hase.soprafs24.entity.UserEmergencyInformation;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserEmergencyContactDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserEmergencyInformationDTO;

import java.util.ArrayList;

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


  // Individual mappers

  @IterableMapping(qualifiedByName = "convertEmergencyContactDTOsToEntities")
  ArrayList<UserEmergencyContact> convertEmergencyContactDTOsToEntities(ArrayList<UserEmergencyContactDTO> emergencyContactDTOs);

  @IterableMapping(qualifiedByName = "convertEntitiesToEmergencyContactDTO")
  ArrayList<UserEmergencyContactDTO> convertEntitiesToEmergencyContactDTO(ArrayList<UserEmergencyContact> emergencyContacts);

  @IterableMapping(qualifiedByName = "convertEmergencyInformationDTOsToEntities")
  ArrayList<UserEmergencyInformation> convertEmergencyInformationDTOsToEntities(ArrayList<UserEmergencyInformationDTO> emergencyInformationDTOs);
  
  @IterableMapping(qualifiedByName = "convertEntitiesToEmergencyInformationDTOs")
  ArrayList<UserEmergencyInformationDTO> convertEntitiesToEmergencyInformationDTOs(ArrayList<UserEmergencyInformation> emergencyInformations);
  
  @Named("convertUserEmergencyContactToDTO")
  UserEmergencyContactDTO convertUserEmergencyContactToDTO(UserEmergencyContact userEmergencyContact);

  @Named("convertDTOToUserEmergencyContact")
  UserEmergencyContact convertDTOToUserEmergencyContact(UserEmergencyContactDTO userEmergencyContactDTO);

  @Named("convertUserEmergencyInformationToDTO")
  UserEmergencyInformationDTO convertUserEmergencyInformationToDTO(UserEmergencyInformation userEmergencyInformation);

  @Named("convertDTOToUserEmergencyInformation")
  UserEmergencyInformation convertDTOToUserEmergencyInformation(UserEmergencyInformationDTO userEmergencyInformationDTO);

  // User to PostDTO mapper
  @Mapping(source="firstName", target="firstName")
  @Mapping(source="lastName", target="lastName")
  @Mapping(source="phoneNumber", target="phoneNumber")
  @Mapping(source="mail", target="mail")
  @Mapping(source="username", target="username")
  @Mapping(source="token", target="token")
  @Mapping(source="profilePictureUrl", target="profilePictureUrl")
  @Mapping(source="receiveNotifications", target="receiveNotifications")
  @Mapping(source="userPreferences", target="userPreferences")
  @Mapping(source="userEmergencyContacts", target="userEmergencyContacts")
  @Mapping(source="userEmergencyInformations", target="userEmergencyInformations")
  @Mapping(target="creationDate", ignore=true)
  @Mapping(target="id", ignore=true)
  @Mapping(target="status", ignore = true)
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  // userEmergencyContact mapper
  @Mapping(source="userId",target="userId")
  @Mapping(source="firstName",target="firstName")
  @Mapping(source="lastName",target="lastName")
  @Mapping(source="phoneNumber",target="phoneNumber")
  UserEmergencyContact convertUserEmergencyContactToDTO(UserEmergencyContactDTO userEmergencyContactDTO);

  @Mapping(source="userId",target="userId")
  @Mapping(source="firstName",target="firstName")
  @Mapping(source="lastName",target="lastName")
  @Mapping(source="phoneNumber",target="phoneNumber")
  UserEmergencyContactDTO convertDTOTOUserEmergencyContact(UserEmergencyContact userEmergencyContact);


  // userEmergencyInformation mapper
  @Mapping(source="userId",target="userId")
  @Mapping(source="type",target="type")
  @Mapping(source="comment",target="comment")
  UserEmergencyInformation convertUserEmergencyInformationToDTO(UserEmergencyInformationDTO userEmergencyInformationDTO);

  @Mapping(source="userId",target="userId")
  @Mapping(source="type",target="type")
  @Mapping(source="comment",target="comment")
  UserEmergencyInformationDTO convertDTOTOUserEmergencyInformation(UserEmergencyInformation userEmergencyInformation);


  // @Mapping(source = "username", target = "username")
  // @Mapping(source = "id", target = "id")
  // @Mapping(source = "token", target = "token")
  // UserGetDTO convertEntityToUserGetCredentials(User user);

  

  // @Mapping(source="userId", target="userId")
  // @Mapping(source="firstName", target="firstName")
  // @Mapping(source="lastName", target="lastName")
  // @Mapping(source="phoneNumber", target="phoneNumber")
  // @Mapping(source="mail", target="mail")
  // @Mapping(source="username", target="username")
  // @Mapping(source="token", target="token")
  // @Mapping(source="status", target="status")
  // @Mapping(source="creationDate", target="creationDate")
  // @Mapping(source="profilePictureUrl", target="profilePictureUrl")
  // @Mapping(source="receiveNotifications", target="receiveNotifications")
  // @Mapping(source="userPreferences", target="userPreferences")
  // @Mapping(source="userEmergencyContacts", target="userEmergencyContacts")
  // @Mapping(source="userEmergencyInformations", target="userEmergencyInformations")
  // UserGetDTO convertEntityToUserGetDTO(User user);



}
