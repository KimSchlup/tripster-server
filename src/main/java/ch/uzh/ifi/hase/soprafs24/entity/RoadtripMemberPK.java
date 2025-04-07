package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/*
 * This is a composite key for Entity RoadtripMember. Since we have only ever 0 or 1 roadtripMember row relating 
 * a user to a roadtrip, we use user_id + roadtrip_id as a primary key.
 */
@Embeddable
public class RoadtripMemberPK implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "roadtrip_id")
    private Long roadtripId;

    // Default constructor
    public RoadtripMemberPK() {
    }

    // All-args constructor
    public RoadtripMemberPK(Long userId, Long roadtripId) {
        this.userId = userId;
        this.roadtripId = roadtripId;
    }

    // Getters and setters
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

    // We need to provide an implementation of the hashcode() and equals() methods
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RoadtripMemberPK))
            return false;
        RoadtripMemberPK that = (RoadtripMemberPK) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(roadtripId, that.roadtripId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roadtripId);
    }
}