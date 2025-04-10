package ch.uzh.ifi.hase.soprafs24.rest.dto;


import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserPreferences;

public class UserPostDTO {

  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String mail;
  private String username;
  private String password;
  private String token;
  // private String profilePictureUrl;
  private Boolean receiveNotifications;
  private UserPreferences userPreferences;
  private UserStatus status;


  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  // public String getProfilePictureUrl(){
  // return profilePictureUrl;
  // }
  // public void setProfilePictureUrl(String profilePictureUrl){
  // this.profilePictureUrl = profilePictureUrl;
  // }
  public Boolean getReceiveNotifications() {
    return this.receiveNotifications;
  }

  public void setReceiveNotifications(Boolean receiveNotification) {
    this.receiveNotifications = receiveNotification;
  }

  public UserPreferences getUserPreferences() {
    return userPreferences;
  }

  public void setUserPreferences(UserPreferences userPreferences) {
    this.userPreferences = userPreferences;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

}
