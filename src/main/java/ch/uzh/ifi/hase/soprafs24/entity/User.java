package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long userId;
  @Column(nullable = false)
  private String firstName;
  @Column(nullable = false)
  private String lastName;
  @Column(nullable = true)
  private String phoneNumber;
  @Column(nullable = true)
  private String mail;
  @Column(nullable = false, unique = true)
  private String username;
  @Column(nullable = false)
  private String password;
  @Column(nullable = false, unique = true)
  private String token;
  @Column(nullable = false)
  private UserStatus status;
<<<<<<< HEAD
  @Column(nullable = false)
  private LocalDate creationDate;
  // @Column(nullable = true)
  // private String profilePictureUrl;
  @Column
  private Boolean receiveNotifications;
  @Column
  private UserPreferences userPreferences;
  // @Column
  // private ArrayList<UserEmergencyContact> userEmergencyContacts;
  // @Column
  // private ArrayList<UserEmergencyInformation> userEmergencyInformations;
||||||| parent of fea1801 (added all necessary classes except POI classes (all including the JTS objects))
=======
  @Column(nullable = false)
  private LocalDate creationDate;
  @Column(nullable = true)
  private String profilePictureUrl;
  @Column(nullable = false)
  private Boolean receiveNotifications;
  @Column
  private UserPreferences userPreferences;
  @Column
  private ArrayList<UserEmergencyContact> userEmergencyContacts;
  @Column
  private ArrayList<UserEmergencyInformation> userEmergencyInformations;

>>>>>>> fea1801 (added all necessary classes except POI classes (all including the JTS objects))

<<<<<<< HEAD

  public Long getUserId() {
    return userId;
||||||| parent of fea1801 (added all necessary classes except POI classes (all including the JTS objects))
  public Long getId() {
    return id;
=======
  public Long getId() {
    return userId;
>>>>>>> fea1801 (added all necessary classes except POI classes (all including the JTS objects))
  }
<<<<<<< HEAD
  public void setUserId(Long userId) {
    this.userId = userId;
||||||| parent of fea1801 (added all necessary classes except POI classes (all including the JTS objects))

  public void setId(Long id) {
    this.id = id;
=======
  public void setId(Long userId) {
    this.userId = userId;
>>>>>>> fea1801 (added all necessary classes except POI classes (all including the JTS objects))
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
  public String getPassword(){
    return password;
  }
  public void setPassword(String password){
    this.password = password;
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
<<<<<<< HEAD
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
    return this.receiveNotifications;
  }
  public void setReceiveNotifications(Boolean receiveNotification){
    this.receiveNotifications = receiveNotification;
  }
  public UserPreferences getUserPreferences(){
    return userPreferences;
  }
  public void setUserPreferences(UserPreferences userPreferences){
    this.userPreferences = userPreferences;
  }

  // public ArrayList<UserEmergencyContact> getUserEmergencyContacts(){
  //   return this.userEmergencyContacts;
  // }
  // public void setUserEmergencyContacts(UserEmergencyContact userEmergencyContact){
  //   if(this.userEmergencyContacts == null){
  //     this.userEmergencyContacts = new ArrayList<UserEmergencyContact>();
  //   }
  //   this.userEmergencyContacts.add(userEmergencyContact);
  // }

  // public ArrayList<UserEmergencyInformation> getUserEmergencyInformations(){
  //   return this.userEmergencyInformations;
  // }
  // public void setUserEmergencyInformations(UserEmergencyInformation userEmergencyInformation){
  //   if(this.userEmergencyInformations == null){
  //     this.userEmergencyInformations = new ArrayList<UserEmergencyInformation>();
  //   }
  //   this.userEmergencyInformations.add(userEmergencyInformation);
  // }

}
||||||| parent of fea1801 (added all necessary classes except POI classes (all including the JTS objects))
}
=======
  public LocalDate getCreationDate(){
    return creationDate;
  }
  public void setCreationDate(LocalDate creationDate){
    this.creationDate = creationDate;
  }
  public String getProfilePictureUrl(){
    return profilePictureUrl;
  }
  public void setProfilePictureUrl(String profilePictureUrl){
    this.profilePictureUrl = profilePictureUrl;
  }
  public Boolean getReceiveNotifications(){
    return receiveNotifications;
  }
  public void setReceiveNorifications(Boolean receiveNotifications){
    this.receiveNotifications = receiveNotifications;
  }
  public UserPreferences getUserPreferences(){
    return userPreferences;
  }
  public void setUserPreferences(UserPreferences userPreferences){
    this.userPreferences = userPreferences;
  }

  public ArrayList<UserEmergencyContact> getUserEmergencyContact(){
    return userEmergencyContacts;
  }
  public void setUserEmergencyContact(UserEmergencyContact userEmergencyContact){
    if(this.userEmergencyContacts == null){
      this.userEmergencyContacts = new ArrayList<UserEmergencyContact>();
    }
    this.userEmergencyContacts.add(userEmergencyContact);
  }

  public ArrayList<UserEmergencyInformation> getUserEmergencyInformations(){
    return userEmergencyInformations;
  }
  public void setUserEmergencyInformation(UserEmergencyInformation userEmergencyInformation){
    if(this.userEmergencyInformations == null){
      this.userEmergencyInformations = new ArrayList<UserEmergencyInformation>();
    }
    this.userEmergencyInformations.add(userEmergencyInformation);
  }

}
>>>>>>> fea1801 (added all necessary classes except POI classes (all including the JTS objects))
