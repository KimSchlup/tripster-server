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

    /**
     * GET /roadtrips a user is owner of or has an accepted or pending invitation
     * 
     * @return List of RoadtripGetDTO
     */
    @GetMapping("/roadtrips")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoadtripGetDTO> getRoadtrips(@RequestHeader("Authorization") String token) {

        User user = userService.getUserByToken(token); // No need to verify if user exists, otherwise authentication
                                                       // fails

        // Fetch roadtrips user is owner, or has an accepted or pending invitation to
        List<Roadtrip> roadtrips = roadtripService.getRoadtrips(user);

        // convert internal representation of roadtrips back to API
        List<RoadtripGetDTO> roadtripGetDTOs = new ArrayList<>();
        for (Roadtrip roadtrip : roadtrips) {
            roadtripGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRoadtripGetDTO(roadtrip));
        }

        return roadtripGetDTOs;
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
        Roadtrip roadtrip = roadtripService.getRoadtripById(roadtripId, user);

        // convert internal representation of rodatrip back to API
        RoadtripGetDTO roadtripGetDTO = DTOMapper.INSTANCE.convertEntityToRoadtripGetDTO(roadtrip);

        return roadtripGetDTO;
    }

    @PutMapping("/roadtrips/{roadtripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public RoadtripGetDTO updateRoadtrip(@PathVariable Long roadtripId, @RequestHeader("Authorization") String token) {

        // Get user from token
        User user = userService.getUserByToken(token);

        // Fetch roadtrips user is owner of or member of
        Roadtrip roadtrip = roadtripService.getRoadtripById(roadtripId, user);

        // convert internal representation of rodatrip back to API
        RoadtripGetDTO roadtripGetDTO = DTOMapper.INSTANCE.convertEntityToRoadtripGetDTO(roadtrip);

        return roadtripGetDTO;
    }

    @DeleteMapping("/roadtrips/{roadtripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content response on successful deletion
    public void deleteRoadtrip(@PathVariable Long roadtripId, @RequestHeader("Authorization") String token) {

        Long authenticatedUserId = userService.getUserByToken(token).getUserId();
        roadtripService.deleteRoadtrip(roadtripId, authenticatedUserId);
    }
}
