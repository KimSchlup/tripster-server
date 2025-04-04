package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripService;

/**
 * Roadtrip Controller
 * This class is responsible for handling all REST request that are related to
 * the Roadtrip.
 * The controller will receive the request and delegate the execution to the
 * RoadtripService and finally return the result.
 */

@RestController
public class RoadtripController {

    private final RoadtripService roadtripService;

    RoadtripController(RoadtripService roadtripService) {
        this.roadtripService = roadtripService;
    }


    @PostMapping("/roadtrips")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoadtripGetDTO createRoadtrip(@RequestBody RoadtripPostDTO roadtripPostDTO) {
    // convert API user to internal representation
    Roadtrip roadtripInput = DTOMapper.INSTANCE.convertRoadtripPostDTOtoEntity(roadtripPostDTO);

    // create roadtrip
    Roadtrip createdRoadtrip = roadtripService.createRoadtrip(roadtripInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToRoadtripGetDTO(createdRoadtrip);
    }

    @DeleteMapping("/roadtrips/{roadtripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content response on successful deletion
    public void deleteRoadtrip(@PathVariable Long roadtripId) {
        roadtripService.deleteRoadtrip(roadtripId);
    }
}




