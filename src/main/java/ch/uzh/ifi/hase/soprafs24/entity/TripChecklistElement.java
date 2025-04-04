package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;
import java.io.Serializable;

import ch.uzh.ifi.hase.soprafs24.constant.Priority;

/**
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "tripChecklistElement")
public class TripChecklistElement implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long elementId;
  @Column
  private Long assignedUserId;
  @Column(nullable = false)
  private String name;
  @Column
  private Priority priority;
  @Column
  private String category;

  public void setElementId(Long elementId){
    this.elementId = elementId;
  }
  public Long getElementId(){
    return this.elementId;
  }
  public void setAssignedUserId(Long assignedUserId){
    this.assignedUserId = assignedUserId;
  }
  public Long getAssignedUserId(){
    return this.assignedUserId;
  }
  public void setName(String name){
    this.name = name;
  }
  public String getName(){
    return this.name;
  }
  public void setPriority(Priority priority){
    this.priority = priority;
  }
  public Priority getPriority(){
    return this.priority;
  }
  public void setCategory(String category){
    this.category = category;
  }
  public String getCategory(String category){
    return this.category;
  }

}
