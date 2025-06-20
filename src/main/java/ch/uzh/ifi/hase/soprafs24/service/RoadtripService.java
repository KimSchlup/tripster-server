package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripGetDTO;

import org.slf4j.Logger;

import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Checklist;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.ChecklistRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final ChecklistRepository checklistRepository;

    private final Logger log = LoggerFactory.getLogger(RoadtripService.class);

    public RoadtripService(@Qualifier("roadtripRepository") RoadtripRepository roadtripRepository,
            UserRepository userRepository, RoadtripMemberRepository roadtripMemberRepository,
            RoadtripSettingsService roadtripSettingsService, ChecklistRepository checklistRepository) {
        this.roadtripRepository = roadtripRepository;
        this.userRepository = userRepository;
        this.roadtripMemberRepository = roadtripMemberRepository;
        this.roadtripSettingsService = roadtripSettingsService;
        this.checklistRepository = checklistRepository;
    }

    public List<RoadtripGetDTO> getRoadtripsOfUser(User user) {

        // Get roadtrips where user is owner
        List<Roadtrip> ownedTrips = roadtripRepository.findByOwner(user);

        // Get roadtrips where invitation was accepted or is still pending
        List<InvitationStatus> validStatuses = List.of(InvitationStatus.PENDING, InvitationStatus.ACCEPTED);
        List<Roadtrip> memberTrips = roadtripMemberRepository.findRoadtripsByUserAndStatusIn(user, validStatuses);

        // Combine and remove duplicates
        Set<Roadtrip> allTrips = new HashSet<>(ownedTrips);
        allTrips.addAll(memberTrips);

        return allTrips.stream()
                .map(r -> {
                    InvitationStatus status = r.getOwner().getUserId().equals(user.getUserId())
                            ? InvitationStatus.ACCEPTED
                            : r.getRoadtripMembers().stream()
                                    .filter(m -> m.getUser().getUserId().equals(user.getUserId()))
                                    .map(RoadtripMember::getInvitationStatus)
                                    .findFirst()
                                    .orElse(null);

                    RoadtripGetDTO dto = new RoadtripGetDTO();
                    dto.setRoadtripId(r.getRoadtripId());
                    dto.setOwnerId(r.getOwner().getUserId());
                    dto.setName(r.getName());
                    dto.setDescription(r.getDescription());
                    dto.setInvitationStatus(status);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public RoadtripGetDTO getRoadtripById(Long roadtripId, User user) {

        // Fetch roadtrip or throw 404
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // Check if user is owner or member
        boolean isOwner = Objects.equals(roadtrip.getOwner().getUserId(), user.getUserId());
        RoadtripMember roadtripMember = roadtripMemberRepository.findByUserAndRoadtrip(user, roadtrip);
        boolean isMember = roadtripMember != null && roadtripMember.getInvitationStatus() == InvitationStatus.ACCEPTED;

        if (!isOwner && !isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this roadtrip");
        }

        // Map to DTO with invitationStatus
        RoadtripGetDTO dto = new RoadtripGetDTO();
        dto.setRoadtripId(roadtrip.getRoadtripId());
        dto.setOwnerId(roadtrip.getOwner().getUserId());
        dto.setName(roadtrip.getName());
        dto.setDescription(roadtrip.getDescription());

        InvitationStatus status = isOwner
                ? InvitationStatus.ACCEPTED
                : roadtripMember.getInvitationStatus(); // Should be safe due to check above

        dto.setInvitationStatus(status);

        return dto;
    }

    public Roadtrip createRoadtrip(Roadtrip newRoadtrip, String token) {

        // Get the user from token and set as owner
        User owner = userRepository.findByToken(token);
        newRoadtrip.setOwner(owner);

        // Save the roadtrip
        newRoadtrip = roadtripRepository.save(newRoadtrip);
        roadtripSettingsService.createRoadtripSettings(newRoadtrip);

        // Automatically create a checklist for the roadtrip
        Checklist checklist = new Checklist();
        checklist.setRoadtrip(newRoadtrip);
        checklistRepository.save(checklist);
        checklistRepository.flush(); // Ensure the checklist is persisted immediately

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
        checklistRepository.flush();

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

    public Boolean isMember(Long roadtripId, Long userId) {
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        return roadtrip.getRoadtripMembers().stream()
                .anyMatch(member -> member.getUser().getUserId().equals(userId));
    }
}
