package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.User;


import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberPostDTO;

import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripMemberService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

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
    private final UserService userService;

    RoadtripMemberController(RoadtripMemberService roadtripMemberService, UserService userService) {
        this.roadtripMemberService = roadtripMemberService;
        this.userService = userService;
    }

    @PostMapping("/roadtrips/{roadtripId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoadtripMemberGetDTO createRoadtripMember(@RequestBody RoadtripMemberPostDTO roadtripMemberPostDTO,
            @PathVariable Long roadtripId, @RequestHeader("Authorization") String token) {

        User invitingUser = userService.getUserByToken(token); // No need to verify if user exists, otherwise
                                                               // authentication
        // fails
        // convert API user to internal representation
        Long userId = roadtripMemberPostDTO.getUserId();

        // create roadtrip
        RoadtripMember createdRoadtripMember = roadtripMemberService.createRoadtripMember(roadtripId, invitingUser,
                userId);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToRoadtripMemberGetDTO(createdRoadtripMember);
    }

    @DeleteMapping("/roadtrips/{roadtripId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteRoadtripMember(@PathVariable Long roadtripId, @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        User deletingUser = userService.getUserByToken(token); // No need to verify if user exists, otherwise
                                                               // authentication
        // fails
        roadtripMemberService.deleteRoadtripMember(roadtripId, deletingUser, userId);
    }

}