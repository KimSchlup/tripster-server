package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserEmergencyContact;
import ch.uzh.ifi.hase.soprafs24.entity.UserEmergencyInformation;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserEmergencyContactDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserEmergencyInformationDTO;
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
    //add Id authentication here
    User authenticatedUser = userService.getUserByToken(token);

      if (!Objects.equals(authenticatedUser.getUserId(), userId)) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this user");
      }
    
    // fetch user in the internal representation
    User user = userService.getUserById(userId);

    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
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

  // @GetMapping("/users/{userId}")
  // @ResponseStatus(HttpStatus.OK)
  // @ResponseBody
  // public ArrayList<UserEmergencyContactDTO> getAllUserEmergencyContacts(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
  //   //add Id authentication here
  //   User authenticatedUser = userService.getUserByToken(token);

  //     if (!Objects.equals(authenticatedUser.getId(), userId)) {
  //         throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this users emergency contact.");
  //     }
    
  //   // fetch user in the internal representation
  //   User user = userService.getUserById(userId);
  //   ArrayList<UserEmergencyContact> emergencyContacts = user.getUserEmergencyContacts();
  //   ArrayList<UserEmergencyContactDTO> dtoContacts = new ArrayList<UserEmergencyContactDTO>();

  //   for(UserEmergencyContact contact : emergencyContacts){
  //     dtoContacts.add(DTOMapper.INSTANCE.convertUserEmergencyContactToDTO(contact));
  //   }
  //   return dtoContacts;
  // }

  // @GetMapping("/users/{userId}")
  // @ResponseStatus(HttpStatus.OK)
  // @ResponseBody
  // public ArrayList<UserEmergencyInformationDTO> getAllUserEmergencyInformations(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
  //   //add Id authentication here
  //   User authenticatedUser = userService.getUserByToken(token);

  //     if (!Objects.equals(authenticatedUser.getId(), userId)) {
  //         throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this users emergency information.");
  //     }
    
  //   // fetch user in the internal representation
  //   User user = userService.getUserById(userId);
  //   ArrayList<UserEmergencyInformation> emergencyInformation = user.getUserEmergencyInformations();
  //   ArrayList<UserEmergencyInformationDTO> dtoInformations = new ArrayList<UserEmergencyInformationDTO>();

  //   for(UserEmergencyInformation contact : emergencyInformation){
  //     dtoInformations.add(DTOMapper.INSTANCE.convertUserEmergencyInformationToDTO(contact));
  //   }
  //   return dtoInformations;
  // }


}
