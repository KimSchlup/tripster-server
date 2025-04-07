package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "roadtrip")
public class Roadtrip implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @OneToMany(mappedBy = "roadtrip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RoadtripMember> roadtripMembers = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

  /*
   * public void setTripProjectSetting(TripProjectSettings tripProjectSettings){
   * this.tripProjectSettings = tripProjectSettings;
   * }
   * public TripProjectSettings getTripProjectSettings(){
   * return this.tripProjectSettings;
   * }
   * public void setTripProjectMembers(TripProjectMember tripProjectMember){
   * this.tripProjectMembers.add(tripProjectMember);
   * }
   * public ArrayList<TripProjectMember> getTripProjectMembers(){
   * return this.tripProjectMembers;
   * }
   */
  public void setOwner(User user) {
    this.owner = user;
  }

  public User getOwner() {
    return this.owner;
  }

}