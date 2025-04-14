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
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.PointOfInterestService;


@RestController
public class PointOfInterestController {

    private final PointOfInterestService pointOfInterestService;

    PointOfInterestController(PointOfInterestService pointOfInterestService){
        this.pointOfInterestService = pointOfInterestService;
    }

    @PostMapping("/roadtrips/{roadtripId}/pois")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PointOfInterestGetDTO createPointOfInterest(@RequestHeader("Authorization") String token, @RequestBody PointOfInterestPostDTO pointOfInterestPostDTO, @PathVariable Long roadtripId) {
        PointOfInterest pointOfInterestInput = DTOMapper.INSTANCE.convertPointOfInterestPostDTOToEntity(pointOfInterestPostDTO);
        PointOfInterest createdPointOfInterest = pointOfInterestService.createPointOfInterest(pointOfInterestInput, roadtripId, token);
        return DTOMapper.INSTANCE.convertEntityToPointOfInterestGetDTO(createdPointOfInterest);
    }
    
    @GetMapping("/roadtrips/{roadtripId}/pois")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PointOfInterestGetDTO> getPointOfInterests(@PathVariable Long roadtripId) {
        List<PointOfInterest> pointOfInterests = pointOfInterestService.getPointOfInterestsByRoadTrip(roadtripId);
        List<PointOfInterestGetDTO> pointOfInterestGetDTOs = new ArrayList<>();

        for (PointOfInterest pointOfInterest : pointOfInterests) {
            pointOfInterestGetDTOs.add(DTOMapper.INSTANCE.convertEntityToPointOfInterestGetDTO(pointOfInterest));
        }
        
        return pointOfInterestGetDTOs;
    }

    @PutMapping("/roadtrips/{roadtripId}/pois/{poiId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updatePointOfInterest(@RequestBody PointOfInterestPutDTO pointOfInterestPutDTO, @PathVariable Long roadtripId, @PathVariable Long poiId){
        
        PointOfInterest newPointOfInterest = DTOMapper.INSTANCE.convertPointOfInterestPutDTOToEntity(pointOfInterestPutDTO);
        PointOfInterest oldPointOfInterest = pointOfInterestService.getPointOfInterestByID(roadtripId, poiId);
        
        pointOfInterestService.updatePointOfInterest(oldPointOfInterest, newPointOfInterest);
        
    }

    @DeleteMapping("/roadtrips/{roadtripId}/pois/{poiId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deletePointOfInterest(@PathVariable Long roadtripId, @PathVariable Long poiId){
        
        pointOfInterestService.deletePointOfInterest(poiId);
        
    }

}
