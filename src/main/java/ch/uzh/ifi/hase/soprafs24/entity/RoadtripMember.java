package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;

import java.io.Serializable;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;

/*
 * Roadtrip Member depends on User and Roadtrip. If user or roadtrip get deleted, so gets Roadtrip member
  ManyToMany relationship: Roadtrip can have many user members and a user can join many roadtripos

 */
@Entity
@Table(name = "roadtrip_member")
public class RoadtripMember implements Serializable {

    // We can use a composite primary key
    @EmbeddedId
    private RoadtripMemberPK roadtripMemberId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("roadtripId")
    @JoinColumn(name = "roadtrip_id")
    private Roadtrip roadtrip;

    @Column(nullable = false)
    private InvitationStatus invitationStatus;

    public RoadtripMemberPK getRoadtripMemberId() {
        return roadtripMemberId;
    }

    public void setRoadtripMemberId(RoadtripMemberPK roadtripMemberId) {
        this.roadtripMemberId = roadtripMemberId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Roadtrip getRoadtrip() {
        return roadtrip;
    }

    public void setRoadtrip(Roadtrip roadtrip) {
        this.roadtrip = roadtrip;
    }

    public void setInvitationStatus(InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public InvitationStatus getInvitationStatus() {
        return this.invitationStatus;
    }

}