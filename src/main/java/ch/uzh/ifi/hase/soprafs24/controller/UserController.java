package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

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
  /*
  @GetMapping("/users/{userId}/emergency-contacts")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserEmergencyContactDTO> getAllUserEmergencyContacts(@PathVariable Long userId,
      @RequestHeader("Authorization") String token) {
    // add Id authentication here
    User authenticatedUser = userService.getUserByToken(token);

    if (!Objects.equals(authenticatedUser.getUserId(), userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "You are not allowed to access this users emergency contact.");
    }
    // fetch user in the internal representation
    User user = userService.getUserById(userId);
    List<UserEmergencyContact> emergencyContacts = user.getUserEmergencyContacts();
    List<UserEmergencyContactDTO> dtoContacts = new ArrayList<UserEmergencyContactDTO>();

    for (UserEmergencyContact contact : emergencyContacts) {
      dtoContacts.add(DTOMapper.INSTANCE.convertUserEmergencyContactToDTO(contact));
    }
    return dtoContacts;
  }

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
