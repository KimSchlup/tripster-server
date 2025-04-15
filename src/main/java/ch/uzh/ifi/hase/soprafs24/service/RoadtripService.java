package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;

import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Roadtrip Service
 * This class is the "worker" and responsible for all functionality related to
 * the rodatrip.
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller
 * 
 */

@Service
@Transactional
public class RoadtripService {

    private final RoadtripRepository roadtripRepository;
    private final UserRepository userRepository;
    private final RoadtripMemberRepository roadtripMemberRepository;
    private final RoadtripSettingsService roadtripSettingsService;

    private final Logger log = LoggerFactory.getLogger(RoadtripService.class);

    public RoadtripService(@Qualifier("roadtripRepository") RoadtripRepository roadtripRepository,
            UserRepository userRepository, RoadtripMemberRepository roadtripMemberRepository,
            RoadtripSettingsService roadtripSettingsService) {
        this.roadtripRepository = roadtripRepository;
        this.userRepository = userRepository;
        this.roadtripMemberRepository = roadtripMemberRepository;
        this.roadtripSettingsService = roadtripSettingsService;
    }

    public List<Roadtrip> getRoadtrips(User user) {

        // Get roadtrips where user is owner
        List<Roadtrip> ownedTrips = roadtripRepository.findByOwner(user);

        // Get roadtrips where invitation was accepted or is still pending
        List<InvitationStatus> validStatuses = List.of(InvitationStatus.PENDING, InvitationStatus.ACCEPTED);
        List<Roadtrip> memberTrips = roadtripMemberRepository.findRoadtripsByUserAndStatusIn(user, validStatuses);

        // Combine and remove duplicates
        Set<Roadtrip> allTrips = new HashSet<>(ownedTrips);
        allTrips.addAll(memberTrips);

        return new ArrayList<>(allTrips);
    }

    public Roadtrip getRoadtripById(Long roadtripId, User user) {

        // Check if roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // Check if user is owner or member
        boolean isOwner = Objects.equals(roadtrip.getOwner(), user);
        RoadtripMember roadtripMember = roadtripMemberRepository.findByUserAndRoadtrip(user, roadtrip);
        boolean isMember = roadtripMember != null && roadtripMember.getInvitationStatus() == InvitationStatus.ACCEPTED;

        if (!isOwner && !isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this roadtrip");
        }

        return roadtrip;

    }

    public Roadtrip createRoadtrip(Roadtrip newRoadtrip, String token) {

        // Get the user from token and set as owner
        User owner = userRepository.findByToken(token);
        newRoadtrip.setOwner(owner);

        // Save the roadtrip
        newRoadtrip = roadtripRepository.save(newRoadtrip);
        roadtripSettingsService.createRoadtripSettings(newRoadtrip);

        // Create a RoadtripMember entry for the owner with ACCEPTED status
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(owner.getUserId());
        pk.setRoadtripId(newRoadtrip.getRoadtripId());

        RoadtripMember ownerMember = new RoadtripMember();
        ownerMember.setRoadtripMemberId(pk);
        ownerMember.setUser(owner);
        ownerMember.setRoadtrip(newRoadtrip);
        ownerMember.setInvitationStatus(InvitationStatus.ACCEPTED);

        // Save the roadtrip member
        roadtripMemberRepository.save(ownerMember);

        // Flush all changes to the database
        roadtripRepository.flush();
        roadtripMemberRepository.flush();

        log.debug("Created Information for Roadtrip: {}", newRoadtrip);
        return newRoadtrip;
    }

    public void deleteRoadtrip(Long roadtripId, Long requestingUserId) {

        Roadtrip roadtripToBeDeleted = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        Long roadtripOwnerId = roadtripToBeDeleted.getOwner().getUserId();
        boolean requestingUserIsOwner = Objects.equals(roadtripOwnerId, requestingUserId);

        if (!requestingUserIsOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the roadtrip owner can delete this roadtrip.");
        }

        roadtripRepository.deleteById(roadtripId);
    }

    public Roadtrip updateRoadtripById(Long roadtripId, Roadtrip roadtripUpdate) {

        Roadtrip roadtripToBeUpdated = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        roadtripToBeUpdated.setName(roadtripUpdate.getName());
        roadtripToBeUpdated.setDescription(roadtripUpdate.getDescription());

        return roadtripRepository.save(roadtripToBeUpdated);
    }
}
