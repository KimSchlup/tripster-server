package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
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

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUserbyId(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
    // add Id authentication here
    User authenticatedUser = userService.getUserByToken(token);

    if (!Objects.equals(authenticatedUser.getUserId(), userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this user");
    }

    // fetch user in the internal representation
    User user = userService.getUserById(userId);

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(@PathVariable Long userId, @RequestBody UserPostDTO userPostDTO, @RequestHeader("Authorization") String token) {

    User authenticatedUser = userService.getUserByToken(token);

      if (!Objects.equals(authenticatedUser.getId(), userId)) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this user");
      }

      // convert API user to internal representation
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
      // update user

      User updatedUser = userService.updateUser(userId, userInput);
      // convert internal representation of user back to API
      return;
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

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void logoutUser(@RequestHeader("Authorization") String token) {
    User authenticatedUser = userService.getUserByToken(token);

    userService.logoutUser(authenticatedUser);
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
