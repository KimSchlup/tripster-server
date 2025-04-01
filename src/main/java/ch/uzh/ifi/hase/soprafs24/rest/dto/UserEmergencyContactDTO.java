package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserEmergencyContactPostDTO {
    
  private Long userId;
  private String firstName;
  private String lastName;
  private String phoneNumber;

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
}
