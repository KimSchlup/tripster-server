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
  private Long roadtripId;

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @OneToMany(mappedBy = "roadtrip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RoadtripMember> roadtripMembers = new ArrayList<>();

  @OneToOne(mappedBy = "roadtrip", cascade = CascadeType.ALL, orphanRemoval = true)
  private RoadtripSettings roadtripSettings; // Add this line

  @OneToOne(mappedBy = "roadtrip", cascade = CascadeType.ALL, orphanRemoval = true)
  private Checklist checklist;

  @OneToMany(mappedBy = "roadtrip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Route> routes = new ArrayList<>(); // List of associated routes
  
  public Long getRoadtripId() {
    return roadtripId;
  }

  public void setRoadtripId(Long roadtripId) {
    this.roadtripId = roadtripId;
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
   */
  public void setRoadtripMembers(RoadtripMember roadtripMembers) {
    this.roadtripMembers.add(roadtripMembers);
  }

  public List<RoadtripMember> getRoadtripMembers() {
    return this.roadtripMembers;
  }

  public void setOwner(User user) {
    this.owner = user;
  }

  public User getOwner() {
    return this.owner;
  }

  public Checklist getChecklist() {
    return checklist;
  }

  public void setChecklist(Checklist checklist) {
      this.checklist = checklist;
  }

  public RoadtripSettings getRoadtripSettings() {
    return roadtripSettings;
  }

  public void setRoadtripSettings(RoadtripSettings roadtripSettings) {
    this.roadtripSettings = roadtripSettings;
  }

  public List<Route> getRoutes() {
    return routes;
  }

  public void setRoutes(List<Route> routes) {
    this.routes = routes;
  }
  
}