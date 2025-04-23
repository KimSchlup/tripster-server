package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;

public class RoadtripGetDTO {

  private Long roadtripId;
  private Long ownerId;
  private InvitationStatus invitationStatus;
  private String name;
  private String description;

  public Long getRoadtripId() {
    return roadtripId;
  }

  public void setRoadtripId(Long roadtripId) {
    this.roadtripId = roadtripId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public InvitationStatus getInvitationStatus() {
    return invitationStatus;
  }
  public void setInvitationStatus(InvitationStatus invitationStatus) {
    this.invitationStatus = invitationStatus;
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
