package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

  @Column(nullable = false)
  private LocalDate creationDate;

  // @Column(nullable = true)
  // private String profilePictureUrl;

  @Column
  private Boolean receiveNotifications;

  //One-to-Many relationship with UserEmergencyContact
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval =
  true)
  private List<UserEmergencyContact> userEmergencyContacts = new ArrayList<>();

  // One-to-Many relationship with UserEmergencyInformation
  // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval =
  // true)
  // private List<UserEmergencyInformation> userEmergencyInformations = new
  // ArrayList<>();

  // Optional One-to-One relationship with UserPreferences
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private UserPreferences userPreferences;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RoadtripMember> roadtripMemberships = new ArrayList<>();

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

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

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
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

  public List<UserEmergencyContact> getUserEmergencyContacts() {
    return userEmergencyContacts;
  }

  public void setUserEmergencyContacts(List<UserEmergencyContact> userEmergencyContacts) {
    this.userEmergencyContacts = userEmergencyContacts;
  }
}