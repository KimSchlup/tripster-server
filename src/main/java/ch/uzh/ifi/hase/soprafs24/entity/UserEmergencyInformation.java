<<<<<<< HEAD
package ch.uzh.ifi.hase.soprafs24.entity;


import jakarta.persistence.*;
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
||||||| parent of fea1801 (added all necessary classes except POI classes (all including the JTS objects))
=======
package ch.uzh.ifi.hase.soprafs24.entity;


import javax.persistence.*;
import java.io.Serializable;

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
>>>>>>> fea1801 (added all necessary classes except POI classes (all including the JTS objects))
