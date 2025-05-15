package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserEmergencyContact;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserEmergencyContactRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;


import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;


/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user.
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller
 * 
 */
@Service
@Transactional
public class UserService {

  private final RoadtripMemberRepository roadtripMemberRepository;

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  private final UserEmergencyContactRepository userEmergencyContactRepository;

  public UserService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("userEmergencyContactRepository") UserEmergencyContactRepository userEmergencyContactRepository, @Qualifier("roadtripMemberRepository") RoadtripMemberRepository roadtripMemberRepository) {
    this.userRepository = userRepository;
    this.userEmergencyContactRepository = userEmergencyContactRepository;
    this.roadtripMemberRepository = roadtripMemberRepository;
  }

  public User loginUser(User user) {
    User userByUsername = userRepository.findByUsername(user.getUsername());
    if (userByUsername == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    if (!userByUsername.getPassword().equals(user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password incorrect");
    }
    userByUsername.setStatus(UserStatus.ONLINE);
    userByUsername.setToken(UUID.randomUUID().toString());
    userRepository.save(userByUsername);
    userRepository.flush();

    return userByUsername;
  }

  public void logoutUser(User user) {
    User userByUsername = userRepository.findByUsername(user.getUsername());
    if (userByUsername == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    userByUsername.setStatus(UserStatus.OFFLINE);
    userRepository.save(userByUsername);
    userRepository.flush();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setReceiveNotifications(true);
    newUser.setCreationDate(LocalDate.now());
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User getUserById(Long userId) {
    return this.userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  public User getUserByToken(String token) {
    User user = userRepository.findByToken(token);
    if (user == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    return user;
  }

  public void updateUser(Long userId, User updatedUser) {
    User user = getUserById(userId);
    
    //check that username is valid input            
    checkIfUserExists(updatedUser);

    if (updatedUser.getUsername() != null) {
        user.setUsername(updatedUser.getUsername());
    }
    if (updatedUser.getFirstName() != null) {
      user.setFirstName(updatedUser.getFirstName());
    }
    if (updatedUser.getLastName() != null) {
        user.setLastName(updatedUser.getLastName());
    }
    if (updatedUser.getPhoneNumber() != null) {
        user.setPhoneNumber(updatedUser.getPhoneNumber());
    }
    if (updatedUser.getMail() != null) {
        user.setMail(updatedUser.getMail());
    }
    if (updatedUser.getPassword() != null) {
        user.setPassword(updatedUser.getPassword());
    }
    if (updatedUser.getReceiveNotifications() != null) {
        user.setReceiveNotifications(updatedUser.getReceiveNotifications());
    }
    if (updatedUser.getUserPreferences() != null) {
      user.setUserPreferences(updatedUser.getUserPreferences());
    }

    this.userRepository.save(user);
    userRepository.flush();
  }

  public void deleteUser(Long userId) {
    User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    try {
      this.userRepository.delete(user);
      this.userRepository.flush();
      } catch (DataIntegrityViolationException e) {
      // Handle the foreign key constraint violation
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete user. Please delete your roadtrips first.");
    }
  }

  public UserEmergencyContact createEmergencyContact(Long userId, UserEmergencyContact emergencyContact){
    // Fetch the user
    User user = getUserById(userId);
    emergencyContact.setUser(user);


    emergencyContact = userEmergencyContactRepository.save(emergencyContact);
    userEmergencyContactRepository.flush();

    return emergencyContact;
  }

  public void updateEmergencyContact(Long contactId, UserEmergencyContact updatedInput){
    //find contact
    UserEmergencyContact contact = userEmergencyContactRepository.findById(contactId).
      orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
    
    if(updatedInput.getFirstName() != null){
      contact.setFirstName(updatedInput.getFirstName());
    }
    if(updatedInput.getLastName() != null){
      contact.setLastName(updatedInput.getLastName());
    }
    if(updatedInput.getPhoneNumber() != null){
      contact.setPhoneNumber(updatedInput.getPhoneNumber());
    }

    this.userEmergencyContactRepository.save(contact);
    userEmergencyContactRepository.flush();
  }

  public void deleteEmergencyContact(Long contactId){
    //find contact
    UserEmergencyContact contact = userEmergencyContactRepository.findById(contactId).
      orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
    
    this.userEmergencyContactRepository.delete(contact);
    userEmergencyContactRepository.flush();
  }


  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    //check to ensure the username is not empty
    if (userToBeCreated.getUsername() != null && userToBeCreated.getUsername().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
    }

    //check for duplicate username
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
    } 
  }

    //Helper method to check if requesting user is in the same roadtrip as the user who's emergency info he requests.
  public boolean checkForRoadtripMembership(User originalUser, User authenticatedUser) {
      // Find all roadtrips the original user is part of
      List<RoadtripMember> originalUserRoadtripMemberships = roadtripMemberRepository.findByUser(originalUser);

      // If the original user is not in any roadtrips, return false
      if (originalUserRoadtripMemberships.isEmpty()) {
          return false;
      }

      // Extract the roadtrips from the memberships
      List<Roadtrip> originalUserRoadtrips = originalUserRoadtripMemberships.stream()
          .map(RoadtripMember::getRoadtrip)
          .collect(Collectors.toList());

      // Find the roadtrips the authenticated user is part of
      List<RoadtripMember> authenticatedUserRoadtripMemberships = roadtripMemberRepository.findByUser(authenticatedUser);

      // Check if any of the authenticated user's roadtrips match the original user's roadtrips
      return authenticatedUserRoadtripMemberships.stream()
          .map(RoadtripMember::getRoadtrip)
          .anyMatch(originalUserRoadtrips::contains);
    }
}
