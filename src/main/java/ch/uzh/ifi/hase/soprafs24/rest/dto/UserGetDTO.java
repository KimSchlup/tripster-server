package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDate;
import java.util.ArrayList;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserPreferences;

public class UserGetDTO {

  private Long userId;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String mail;
  private String username;
  private String token;
  private UserStatus status;
  private LocalDate creationDate;
  // private String profilePictureUrl;
  private Boolean receiveNotifications;
  private UserPreferences userPreferences;
  // private ArrayList<UserEmergencyContactDTO> userEmergencyContacts;
  // private ArrayList<UserEmergencyInformationDTO> userEmergencyInformations;

  public Long getUserId() {
    return userId;
  }
  public void setUserId(Long userId) {
    this.userId = userId;
  }
  public String getFirstName(){
    return firstName;
  }
  public void setFirstName(String firstName){
    this.firstName = firstName;
  }
  public String getLastName(){
    return lastName;
  }
  public void setLastName(String lastName){
    this.lastName = lastName;
  }
  public String getPhoneNumber(){
    return phoneNumber;
  }
  public void setPhoneNumber(String phoneNumber){
    this.phoneNumber = phoneNumber;
  }
  public String getMail(){
    return mail;
  }
  public void setMail(String mail){
    this.mail = mail;
  }
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getToken() {
    return token;
  }
  public void setToken(String token) {
    this.token = token;
  }
  public UserStatus getStatus() {
    return status;
  }
  public void setStatus(UserStatus status) {
    this.status = status;
  }
  public LocalDate getCreationDate(){
    return creationDate;
  }
  public void setCreationDate(LocalDate creationDate){
    this.creationDate = creationDate;
  }
  // public String getProfilePictureUrl(){
  //   return profilePictureUrl;
  // }
  // public void setProfilePictureUrl(String profilePictureUrl){
  //   this.profilePictureUrl = profilePictureUrl;
  // }
  public Boolean getReceiveNotifications(){
    return receiveNotifications;
  }
  public void setreceiveNotifications(Boolean receiveNotifications){
    this.receiveNotifications = receiveNotifications;
  }
  public UserPreferences getUserPreferences(){
    return userPreferences;
  }
  public void setUserPreferences(UserPreferences userPreferences){
    this.userPreferences = userPreferences;
  }

  // public ArrayList<UserEmergencyContactDTO> getUserEmergencyContacts(){
  //   return userEmergencyContacts;
  // }
  // public void setUserEmergencyContacts(UserEmergencyContactDTO userEmergencyContact){
  //   if(this.userEmergencyContacts == null){
  //     this.userEmergencyContacts = new ArrayList<UserEmergencyContactDTO>();
  //   }
  //   this.userEmergencyContacts.add(userEmergencyContact);
  // }

  // public ArrayList<UserEmergencyInformationDTO> getUserEmergencyInformations(){
  //   return userEmergencyInformations;
  // }
  // public void setUserEmergencyInformations(UserEmergencyInformationDTO userEmergencyInformation){
  //   if(this.userEmergencyInformations == null){
  //     this.userEmergencyInformations = new ArrayList<UserEmergencyInformationDTO>();
  //   }
  //   this.userEmergencyInformations.add(userEmergencyInformation);
  // }
}
