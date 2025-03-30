package ch.uzh.ifi.hase.soprafs24.entity;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "userEmergencyInformation")
public class UserEmergencyInformation implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  // @GeneratedValue
  private Long userId;
  @Column
  private String type;
  @Column
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
