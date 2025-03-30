package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs24.constant.*;

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
@Table(name = "tripProjectSettings")
public class TripProjectSettings implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long tripId;
  @Column
  private BasemapType basemap;
  @Column
  private DecisionProcess decisionProcess;
  @Column
  private LocalDate startDate;
  @Column
  private LocalDate endDate;

  public void setTripId(Long tripId){
    this.tripId = tripId;
  }
  public Long getTripId(){
    return this.tripId;
  }
  public void setBasemapType(BasemapType basemap){
    this.basemap = basemap;
  }
  public BasemapType getBasemapType(){
    return this.basemap;
  }
  public void setDecisionProcess(DecisionProcess decisionProcess){
    this.decisionProcess = decisionProcess;
  }
  public DecisionProcess getDecisionProcess(){
    return this.decisionProcess;
  }
  public void setStartDate(LocalDate startDate){
    this.startDate = startDate;
  }
  public LocalDate getStartDate(){
    return this.startDate;
  }
  public void setEndDate(LocalDate endDate){
    this.endDate = endDate;
  }
  public LocalDate getEndDate(){
    return this.endDate;
  }

}
