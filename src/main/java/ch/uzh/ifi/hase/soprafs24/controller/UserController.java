package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserEmergencyContact;
import ch.uzh.ifi.hase.soprafs24.rest.dto.EmergencyContactGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.EmergencyContactPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUserbyUserId(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
    // add Id authentication here
    User authenticatedUser = userService.getUserByToken(token);

    if (!Objects.equals(authenticatedUser.getUserId(), userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
    } else {
    // fetch user in the internal representation
    User user = userService.getUserById(userId);

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);}
  }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(@PathVariable Long userId, @RequestBody UserPostDTO userPostDTO, @RequestHeader("Authorization") String token) {

    User authenticatedUser = userService.getUserByToken(token);

      if (!Objects.equals(authenticatedUser.getUserId(), userId)) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
      }

      // convert API user to internal representation
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
      
      // update user
      userService.updateUser(userId, userInput);
    
  }
  

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetCredentials(createdUser);
  }

  @PostMapping("auth/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    // login user
    User loggedInUser = userService.loginUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetCredentials(loggedInUser);
  }

  @PostMapping("auth/logout")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void logoutUser(@RequestHeader("Authorization") String token) {
    User authenticatedUser = userService.getUserByToken(token);

    userService.logoutUser(authenticatedUser);
  }

  @DeleteMapping("users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteUser(@PathVariable Long userId, @RequestHeader("Authorization") String token){

    User authenticatedUser = userService.getUserByToken(token);

    if (!Objects.equals(authenticatedUser.getUserId(), userId)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
    }

    userService.deleteUser(userId);
    return;
  }

  @GetMapping("/users/{userId}/emergency-contacts")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<EmergencyContactGetDTO> getUserEmergencyContacts(
      @PathVariable Long userId, 
      @RequestHeader("Authorization") String token) {
      
      User authenticatedUser = userService.getUserByToken(token);
      User originalUser = userService.getUserById(userId);

      // Check if the authenticated user has permission to view emergency contacts
      if(userService.checkForRoadtripMembership(originalUser, authenticatedUser) == false && !Objects.equals(authenticatedUser.getUserId(), userId)){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
      }

      // Create a list to store emergency contact DTOs
      List<EmergencyContactGetDTO> emergencyContactGetDTOs = new ArrayList<>();

      // Convert each emergency contact to DTO
      for (UserEmergencyContact contact : originalUser.getUserEmergencyContacts()) {
          emergencyContactGetDTOs.add(DTOMapper.INSTANCE.convertUserEmergencyContactToDTO(contact));
      }

      return emergencyContactGetDTOs;
  }

  //post mapping for emergency contact
  @PostMapping("/users/{userId}/emergency-contacts")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody public EmergencyContactGetDTO createEmergencyContact( @PathVariable Long userId, @RequestBody EmergencyContactPostDTO emergencyContactPostDTO, @RequestHeader("Authorization") String token) { 
    User authenticatedUser = userService.getUserByToken(token);
    if (!Objects.equals(authenticatedUser.getUserId(), userId)) { 
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource");
      }
    // Convert DTO to entity
    UserEmergencyContact emergencyContact = DTOMapper.INSTANCE.convertEmergencyContactPostDTOToEntity(emergencyContactPostDTO);
    // Create emergency contact
    UserEmergencyContact savedContact = userService.createEmergencyContact(userId, emergencyContact);
    // Convert back to DTO for response
    return DTOMapper.INSTANCE.convertUserEmergencyContactToDTO(savedContact);
  }

  //Put mapping for emergency contacts
  

  //Delete mapping for emergency contacts

  /*
  @GetMapping("/users/{userId}/emergency-information")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserEmergencyInformationDTO> getAllUserEmergencyInformations(@PathVariable Long userId,
      @RequestHeader("Authorization") String token) {
    // add Id authentication here
    User authenticatedUser = userService.getUserByToken(token);

    if (!Objects.equals(authenticatedUser.getUserId(), userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "You are not allowed to access this users emergency information.");
    }
    // fetch user in the internal representation
    User user = userService.getUserById(userId);

    List<UserEmergencyInformation> emergencyInformation = user.getUserEmergencyInformations();
    List<UserEmergencyInformationDTO> dtoInformations = new ArrayList<UserEmergencyInformationDTO>();

    for (UserEmergencyInformation contact : emergencyInformation) {
      dtoInformations.add(DTOMapper.INSTANCE.convertUserEmergencyInformationToDTO(contact));
    }
    return dtoInformations;
  }
    */
}
