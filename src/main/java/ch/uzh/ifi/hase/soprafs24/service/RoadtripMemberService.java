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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

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

    public RoadtripMember createRoadtripMember(Long roadtripId, RoadtripMember newRoadtripMember) {

        // verify user exists
        Long userId = newRoadtripMember.getId().getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    

        // verify roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // Set user and roadtrip
        newRoadtripMember.setUser(user);
        newRoadtripMember.setRoadtrip(roadtrip);

        // Set composite key
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(userId);
        pk.setRoadtripId(roadtripId);
        newRoadtripMember.setId(pk);

        // Set default status
        newRoadtripMember.setInvitationStatus(InvitationStatus.PENDING);

         // saves the given entity but data is only persisted in the database once
        // flush() is called
        newRoadtripMember = roadtripMemberRepository.save(newRoadtripMember);
        roadtripMemberRepository.flush();

        log.debug("Created Information for Roadtrip: {}", newRoadtripMember);
        return newRoadtripMember;
    }

}