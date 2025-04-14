package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;

public class RoadtripMemberGetDTO {
    private Long userId;
    private Long roadtripId;
    private InvitationStatus invitationStatus;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoadtripId() {
        return roadtripId;
    }

    public void setRoadtripId(Long roadtripId) {
        this.roadtripId = roadtripId;
    }

    public InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }
}
