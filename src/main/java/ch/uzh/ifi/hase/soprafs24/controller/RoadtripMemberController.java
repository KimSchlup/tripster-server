package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripMemberService;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripService;

/**
 * Roadtrip Controller
 * This class is responsible for handling all REST request that are related to
 * the Roadtrip.
 * The controller will receive the request and delegate the execution to the
 * RoadtripService and finally return the result.
 */

@RestController
public class RoadtripMemberController {

    private final RoadtripMemberService roadtripMemberService;

    RoadtripMemberController(RoadtripMemberService roadtripMemberService) {
        this.roadtripMemberService = roadtripMemberService;
    }

    @PostMapping("/roadtrips{roadtripId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoadtripMemberGetDTO createRoadtripMember(@RequestBody RoadtripMemberPostDTO roadtripMemberPostDTO, @PathVariable Long roadtripId) {
        // convert API user to internal representation
        RoadtripMember roadtripMemberInput = DTOMapper.INSTANCE
                .convertRoadtripMemberPostDTOtoEntity(roadtripMemberPostDTO);

        // create roadtrip
        RoadtripMember createdRoadtripMember = roadtripMemberService.createRoadtripMember(roadtripId, roadtripMemberInput);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToRoadtripMemberGetDTO(createdRoadtripMember);
    }
}