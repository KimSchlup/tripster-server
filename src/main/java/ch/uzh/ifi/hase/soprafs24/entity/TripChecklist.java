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
@Table(name = "trip_checklist")
public class TripChecklist implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long tripId;
  @Column
  private Long userId;
  @Column
  private String type;
  @Column
  private String comment;
  @Column
  private ArrayList<TripChecklistElement> tripChecklistElements;
  
  public void setTripId(Long tripId){
    this.tripId = tripId;
  }
  public Long getTripId(){
    return this.tripId;
  }
  public void setUserId(Long userId){
    this.userId = userId;
  }
  public Long getUserId(){
    return this.userId;
  }
  public void setType(String type){
    this.type = type;
  }
  public String getType(){
    return this.type;
  }
  public void setComment(String comment){
    this.comment = comment;
  }
  public String getComment(){
    return this.comment;
  }
  public void setTripChecklistElements(TripChecklistElement tripChecklistElement){
    if(this.tripChecklistElements ==null){
        this.tripChecklistElements = new ArrayList<TripChecklistElement>();
    }
    this.tripChecklistElements.add(tripChecklistElement);
  }
  public ArrayList<TripChecklistElement> getTripChecklistElements(){
    return this.tripChecklistElements;
  }
  

}
