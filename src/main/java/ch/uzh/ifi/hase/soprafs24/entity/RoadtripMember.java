package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;

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
    private RoadtripMemberPK roadtripId;

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

    public RoadtripMemberPK getId() {
        return roadtripId;
    }

    public void setId(RoadtripMemberPK roadtripId) {
        this.roadtripId = roadtripId;
    }

    public void setStatus(InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public InvitationStatus getStatus() {
        return this.invitationStatus;
    }

}