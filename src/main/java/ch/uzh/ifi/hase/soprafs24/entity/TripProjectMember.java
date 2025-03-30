package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;

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
@Table(name = "tripProjectMember")
public class TripProjectMember implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long tripId;
  @Id
  // @GeneratedValue
  private Long userId;
  @Column
  private InvitationStatus status;

  public Long getTripId() {
    return tripId;
  }
  public void setTripId(Long tripId) {
    this.tripId = tripId;
  }

  public Long getUserId(){
    return this.userId;
  }
  public void setUserId(Long userId){
    this.userId = userId;
  }

}
