package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;
import java.util.List;

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

import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

/**
 * Roadtrip Controller
 * This class is responsible for handling all REST request that are related to
 * the Roadtrip.
 * The controller will receive the request and delegate the execution to the
 * RoadtripService and finally return the result.
 */

@RestController
public class RoadtripController {

    private final UserService userService;
    private final RoadtripService roadtripService;

    RoadtripController(UserService userService, RoadtripService roadtripService) {
        this.userService = userService;
        this.roadtripService = roadtripService;
    }

    @PostMapping("/roadtrips")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoadtripGetDTO createRoadtrip(@RequestBody RoadtripPostDTO roadtripPostDTO,
            @RequestHeader("Authorization") String token) {

        // convert API user to internal representation, fetch userId
        Roadtrip roadtripInput = DTOMapper.INSTANCE.convertRoadtripPostDTOtoEntity(roadtripPostDTO);

        // create roadtrip
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(roadtripInput, token);

        // convert internal representation of roadtrip back to API
        return DTOMapper.INSTANCE.convertEntityToRoadtripGetDTO(createdRoadtrip);
    }

    @GetMapping("/roadtrips")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoadtripGetDTO> getRoadtrips(@RequestHeader("Authorization") String token) {
        User user = userService.getUserByToken(token);
        return roadtripService.getRoadtripsOfUser(user);
    }

    /**
     * GET /roadtrips/{roadtripId} a user is owner of or has an accepted
     * invitation
     * 
     * @return RoadtripGetDTO
     */
    @GetMapping("/roadtrips/{roadtripId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RoadtripGetDTO getRoadtripById(@PathVariable Long roadtripId, @RequestHeader("Authorization") String token) {

        // Get user from token
        User user = userService.getUserByToken(token);

        // Fetch roadtrips user is owner of or member of
        RoadtripGetDTO roadtripGetDTO = roadtripService.getRoadtripById(roadtripId, user);

        return roadtripGetDTO;
    }

    @PutMapping("/roadtrips/{roadtripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public RoadtripGetDTO updateRoadtrip(@PathVariable Long roadtripId, @RequestBody RoadtripPostDTO roadtripPostDTO,
            @RequestHeader("Authorization") String token) {

        // Get user from token
        User user = userService.getUserByToken(token);

        // Check if user has access to this roadtrip
        roadtripService.getRoadtripById(roadtripId, user);

        // Convert API roadtrip to internal representation
        Roadtrip roadtripInput = DTOMapper.INSTANCE.convertRoadtripPostDTOtoEntity(roadtripPostDTO);

        // Update roadtrip
        Roadtrip updatedRoadtrip = roadtripService.updateRoadtripById(roadtripId, roadtripInput);

        // Convert internal representation of roadtrip back to API
        return DTOMapper.INSTANCE.convertEntityToRoadtripGetDTO(updatedRoadtrip);
    }

    @DeleteMapping("/roadtrips/{roadtripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content response on successful deletion
    public void deleteRoadtrip(@PathVariable Long roadtripId, @RequestHeader("Authorization") String token) {

        Long authenticatedUserId = userService.getUserByToken(token).getUserId();
        roadtripService.deleteRoadtrip(roadtripId, authenticatedUserId);
    }

    
}
