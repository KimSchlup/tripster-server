package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadtripMemberRepository extends JpaRepository<RoadtripMember, RoadtripMemberPK> {

    // Find all members of a specific roadtrip
    List<RoadtripMember> findByRoadtrip(Roadtrip roadtrip);

    // Find all trips a user is part of
    List<RoadtripMember> findByUser(User user);

    // Find all members of a trip by invitation status
    List<RoadtripMember> findByRoadtripAndInvitationStatus(Roadtrip roadtrip, InvitationStatus status);

    // Find membership of a specific user in a specific trip
    RoadtripMember findByUserAndRoadtrip(User user, Roadtrip roadtrip);
}