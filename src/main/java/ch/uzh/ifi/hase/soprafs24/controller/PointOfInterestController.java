package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.VotePutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.PointOfInterestService;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
public class PointOfInterestController {

    private final PointOfInterestService pointOfInterestService;
    private final SimpMessagingTemplate messagingTemplate;

    PointOfInterestController(PointOfInterestService pointOfInterestService, SimpMessagingTemplate messagingTemplate) {
        this.pointOfInterestService = pointOfInterestService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/roadtrips/{roadtripId}/pois")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PointOfInterestGetDTO createPointOfInterest(@RequestHeader("Authorization") String token,
                                                        @RequestBody PointOfInterestPostDTO pointOfInterestPostDTO, 
                                                        @PathVariable Long roadtripId) {
        PointOfInterest pointOfInterestInput = DTOMapper.INSTANCE
                .convertPointOfInterestPostDTOToEntity(pointOfInterestPostDTO);
        PointOfInterest createdPointOfInterest = pointOfInterestService.createPointOfInterest(pointOfInterestInput,
                roadtripId, token);
        PointOfInterestGetDTO pointOfInterestGetDTO = DTOMapper.INSTANCE
                .convertEntityToPointOfInterestGetDTO(createdPointOfInterest);

        // websocket: notify subscribers about new point of interest
        messagingTemplate.convertAndSend("/topic/roadtrips/" + roadtripId + "/pois", pointOfInterestGetDTO);

        return pointOfInterestGetDTO;

    }

    @GetMapping("/roadtrips/{roadtripId}/pois")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PointOfInterestGetDTO> getPointOfInterests(@RequestHeader("Authorization") String token, @PathVariable Long roadtripId) {
        List<PointOfInterest> pointOfInterests = pointOfInterestService.getPointOfInterestsByRoadTrip(token, roadtripId);
        List<PointOfInterestGetDTO> pointOfInterestGetDTOs = new ArrayList<>();

        for (PointOfInterest pointOfInterest : pointOfInterests) {
            pointOfInterestService.calculateStatus(token, pointOfInterest, roadtripId);
            pointOfInterestGetDTOs.add(DTOMapper.INSTANCE.convertEntityToPointOfInterestGetDTO(pointOfInterest));
        }

        return pointOfInterestGetDTOs;
    }

    @PutMapping("/roadtrips/{roadtripId}/pois/{poiId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updatePointOfInterest(@RequestHeader("Authorization") String token, 
                                        @RequestBody PointOfInterestPostDTO pointOfInterestPostDTO,  
                                        @PathVariable Long roadtripId, 
                                        @PathVariable Long poiId){
        
        PointOfInterest newPointOfInterest = DTOMapper.INSTANCE.convertPointOfInterestPostDTOToEntity(pointOfInterestPostDTO);
        PointOfInterest oldPointOfInterest = pointOfInterestService.getPointOfInterestByID(token, roadtripId, poiId);
        
        pointOfInterestService.updatePointOfInterest(oldPointOfInterest, newPointOfInterest);

    }

    @DeleteMapping("/roadtrips/{roadtripId}/pois/{poiId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deletePointOfInterest(@RequestHeader("Authorization") String token, @PathVariable Long roadtripId, @PathVariable Long poiId) {
        pointOfInterestService.deletePointOfInterest(token, roadtripId, poiId); // Pass all required arguments
    }

    @PutMapping("/roadtrips/{roadtripId}/pois/{poiId}/votes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void castVote(@PathVariable Long roadtripId, @PathVariable Long poiId,
            @RequestHeader("Authorization") String token, @RequestBody VotePutDTO votePutDTO) {
        String vote = votePutDTO.getVote();
        pointOfInterestService.castVote(token, roadtripId, poiId, vote);
    }

    @DeleteMapping("/roadtrips/{roadtripId}/pois/{poiId}/votes")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteVote(@PathVariable Long roadtripId, @PathVariable Long poiId,
            @RequestHeader("Authorization") String token) {
        pointOfInterestService.deleteVote(token, roadtripId, poiId);
    }

}
