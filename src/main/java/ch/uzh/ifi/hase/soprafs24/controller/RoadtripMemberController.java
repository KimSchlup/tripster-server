package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.User;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberPutDTO;
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
    public RoadtripMemberGetDTO createRoadtripMember(
            @RequestBody RoadtripMemberPostDTO roadtripMemberPostDTO,
            @PathVariable Long roadtripId,
            @RequestHeader("Authorization") String token) {

        User invitingUser = userService.getUserByToken(token);

        String invitedUsername = roadtripMemberPostDTO.getUsername();

        // create roadtrip
        RoadtripMember createdRoadtripMember = roadtripMemberService.createRoadtripMember(roadtripId, invitingUser,
        invitedUsername);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToRoadtripMemberGetDTO(createdRoadtripMember);
    }

    @GetMapping("/roadtrips/{roadtripId}/members")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoadtripMemberGetDTO> getRoadtripMembers(
            @PathVariable Long roadtripId) {

        // Get all members of the roadtrip
        List<RoadtripMember> roadtripMembers = roadtripMemberService.getRoadtripMembers(roadtripId);

        // Convert each member to DTO
        List<RoadtripMemberGetDTO> roadtripMemberGetDTOs = new ArrayList<>();
        for (RoadtripMember roadtripMember : roadtripMembers) {
            roadtripMemberGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRoadtripMemberGetDTO(roadtripMember));
        }

        return roadtripMemberGetDTOs;
    }

    @PutMapping("/roadtrips/{roadtripId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateRoadtripMember(
            @PathVariable Long roadtripId,
            @PathVariable Long userId,
            @RequestBody RoadtripMemberPutDTO roadtripMemberPutDTO,
            @RequestHeader("Authorization") String token) {

        // user initiating update
        User updatingUser = userService.getUserByToken(token);

        // convert API roadtripMember to internal representation
        RoadtripMember roadtripMemberInput = DTOMapper.INSTANCE
                .convertRoadtripMemberPutDTOtoEntity(roadtripMemberPutDTO);

        // update roadtrip
        roadtripMemberService.updateRoadtripMember(roadtripId, updatingUser, roadtripMemberInput, userId);

    }

    @DeleteMapping("/roadtrips/{roadtripId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteRoadtripMember(
            @PathVariable Long roadtripId,
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        User deletingUser = userService.getUserByToken(token); // No need to verify if user exists, otherwise
                                                               // authentication
        // fails
        roadtripMemberService.deleteRoadtripMember(roadtripId, deletingUser, userId);
    }

}
