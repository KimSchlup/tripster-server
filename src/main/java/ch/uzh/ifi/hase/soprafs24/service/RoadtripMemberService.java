package ch.uzh.ifi.hase.soprafs24.service;

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
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;

/**
 * Roadtrip Service
 * This class is the "worker" and responsible for all functionality related to
 * the RoadtripMember.
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller
 * 
 */

@Service
@Transactional
public class RoadtripMemberService {

    private final Logger log = LoggerFactory.getLogger(RoadtripMemberService.class);

    private final RoadtripMemberRepository roadtripMemberRepository;
    private final UserRepository userRepository;
    private final RoadtripRepository roadtripRepository;

    public RoadtripMemberService(
            @Qualifier("roadtripMemberRepository") RoadtripMemberRepository roadtripMemberRepository,
            @Qualifier("userRepository") UserRepository userRepository,
            @Qualifier("roadtripRepository") RoadtripRepository roadtripRepository) {
        this.roadtripMemberRepository = roadtripMemberRepository;
        this.userRepository = userRepository;
        this.roadtripRepository = roadtripRepository;
    }

    public List<RoadtripMember> getRoadtripMembers(Long roadtripId) {
        // verify roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // get all members of the roadtrip
        return roadtripMemberRepository.findByRoadtrip(roadtrip);
    }

    public RoadtripMember createRoadtripMember(Long roadtripId, User invitingUser, String invitedUsername) {
        // verify roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // verify invitingUser is owner of roadtrip
        boolean isOwner = Objects.equals(roadtrip.getOwner().getUserId(), invitingUser.getUserId());
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to invite");
        }

        // verify invited user exists
        User invitedUser = userRepository.findByUsername(invitedUsername);
        if (invitedUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // verify userId provided is not the owner itself
        if (Objects.equals(invitingUser.getUserId(), invitedUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner cannot invite itself");
        }

        // verify invited user is not already member of roadtrip
        List<RoadtripMember> roadtripMembers = roadtripMemberRepository.findByRoadtrip(roadtrip);
        boolean isAlreadyMember = roadtripMembers.stream()
                .anyMatch(member -> Objects.equals(member.getUser().getUserId(), invitedUser.getUserId()));

        if (isAlreadyMember) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User is already a member or was invited to the roadtrip");
        }

        // Create composite key
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(invitedUser.getUserId());
        pk.setRoadtripId(roadtripId);

        // Set user and roadtrip
        RoadtripMember newRoadtripMember = new RoadtripMember();
        newRoadtripMember.setRoadtripMemberId(pk);
        newRoadtripMember.setUser(invitedUser);
        newRoadtripMember.setRoadtrip(roadtrip);
        newRoadtripMember.setInvitationStatus(InvitationStatus.PENDING);

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newRoadtripMember = roadtripMemberRepository.save(newRoadtripMember);
        roadtripMemberRepository.flush();

        return newRoadtripMember;
    }

    public void updateRoadtripMember(
            Long roadtripId,
            User updatingUser,
            RoadtripMember roadtripMemberInput,
            Long userId) {

        // verify roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // only roadtrip owner or the user itself can update its roadtrip membership
        // status
        boolean isOwner = Objects.equals(roadtrip.getOwner().getUserId(), updatingUser.getUserId());
        boolean isSameUser = Objects.equals(updatingUser.getUserId(), userId);
        if (!isOwner && !isSameUser) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to update");
        }

        // verify user is member of roadtrip
        RoadtripMemberPK roadtripMemberPK = new RoadtripMemberPK();
        roadtripMemberPK.setUserId(userId);
        roadtripMemberPK.setRoadtripId(roadtripId);
        RoadtripMember roadtripMember = roadtripMemberRepository.findById(roadtripMemberPK)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip member not found"));

        // update invitation status
        if (roadtripMemberInput.getInvitationStatus() != null) {
            roadtripMember.setInvitationStatus(roadtripMemberInput.getInvitationStatus());
        }

        roadtripMemberRepository.save(roadtripMember);
        roadtripMemberRepository.flush();
    }

    public void deleteRoadtripMember(Long roadtripId, User deletingUser, long userId) {
        // verify roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // verify deletingUser is owner of roadtrip
        boolean isOwner = Objects.equals(roadtrip.getOwner().getUserId(), deletingUser.getUserId());
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to delete");
        }
        // verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // verify user is member of roadtrip
        RoadtripMemberPK roadtripMemberPK = new RoadtripMemberPK();
        roadtripMemberPK.setUserId(userId);
        roadtripMemberPK.setRoadtripId(roadtripId);
        RoadtripMember roadtripMember = roadtripMemberRepository.findById(roadtripMemberPK)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip member not found"));

        // delete roadtrip member
        roadtripMemberRepository.delete(roadtripMember);
        roadtripMemberRepository.flush();

    }

}
