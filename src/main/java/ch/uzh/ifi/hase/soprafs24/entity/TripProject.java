<<<<<<< HEAD
package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "tripProject")
public class TripProject implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long tripId;
  @Column(nullable = false)
  private String tripName;
  @Column
  private String tripDescription;
  @Column
  private TripProjectSettings tripProjectSettings;
  @Column(nullable = false)
  private ArrayList<TripProjectMember> tripProjectMembers;
  @Column
  private User owner;
  

  public void setTripName(String tripName){
    this.tripName = tripName;
  }
  public String getTripName(){
    return this.tripName;
  }
  public void setTripDescription(String tripDescription){
    this.tripDescription = tripDescription;
  }
  public String getTripDescription(){
    return this.tripDescription;
  }
  public void setTripProjectSetting(TripProjectSettings tripProjectSettings){
    this.tripProjectSettings = tripProjectSettings;
  }
  public TripProjectSettings getTripProjectSettings(){
    return this.tripProjectSettings;
  }
  public void setTripProjectMembers(TripProjectMember tripProjectMember){
    this.tripProjectMembers.add(tripProjectMember);
  }
  public ArrayList<TripProjectMember> getTripProjectMembers(){
    return this.tripProjectMembers;
  }
  public void setOwner(User user){
    this.owner = user;
  }
  public User getOwner(){
    return this.owner;
  }

}
||||||| parent of fea1801 (added all necessary classes except POI classes (all including the JTS objects))
=======
package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
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
@Table(name = "tripProject")
public class TripProject implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long tripId;
  @Column(nullable = false)
  private String tripName;
  @Column
  private String tripDescription;
  @Column
  private TripProjectSettings tripProjectSettings;
  @Column(nullable = false)
  private ArrayList<TripProjectMember> tripProjectMembers;

  public void setTripName(String tripName){
    this.tripName = tripName;
  }
  public String getTripName(){
    return this.tripName;
  }
  public void setTripDescription(String tripDescription){
    this.tripDescription = tripDescription;
  }
  public String getTripDescription(){
    return this.tripDescription;
  }
  public void setTripProjectSetting(TripProjectSettings tripProjectSettings){
    this.tripProjectSettings = tripProjectSettings;
  }
  public TripProjectSettings getTripProjectSettings(){
    return this.tripProjectSettings;
  }
  public void setTripProjectMembers(TripProjectMember tripProjectMember){
    this.tripProjectMembers.add(tripProjectMember);
  }
  public ArrayList<TripProjectMember> getTripProjectMembers(){
    return this.tripProjectMembers;
  }

}
>>>>>>> fea1801 (added all necessary classes except POI classes (all including the JTS objects))
