package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class RoadtripGetDTO {

  private Long roadtripId;
  private String name;
  private String description;

  public Long getRoadtripId() {
    return roadtripId;
  }

  public void setRoadtripId(Long roadtripId) {
    this.roadtripId = roadtripId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
