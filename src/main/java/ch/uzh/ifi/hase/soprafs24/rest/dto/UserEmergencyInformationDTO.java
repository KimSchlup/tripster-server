package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserEmergencyInformationDTO {
    
  private Long userId;
  private String type;
  private String comment;

  public Long getId() {
    return userId;
  }
  public void setId(Long userId) {
    this.userId = userId;
  }

  public String getType(){
    return type;
  }
  public void setType(String type){
    this.type = type;
  }

  public String getComment(){
    return comment;
  }
  public void setComment(String comment){
    this.comment = comment;
  }
}
