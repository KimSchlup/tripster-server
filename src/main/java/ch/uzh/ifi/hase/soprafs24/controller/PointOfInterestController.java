package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.PointOfInterestService;


@RestController
public class PointOfInterestController {
    
    private final PointOfInterestService pointOfInterestService;

    PointOfInterestController(PointOfInterestService pointOfInterestService){
        this.pointOfInterestService = pointOfInterestService;
    }

    @PostMapping("/roadtrips/{roadtrips_id}/pois")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PointOfInterestGetDTO createPointOfInterest(@RequestBody PointOfInterestPostDTO pointOfInterestPostDTO) {
        
        PointOfInterest pointOfInterestInput = DTOMapper.INSTANCE.convertPointOfInterestPostDTOToEntity(pointOfInterestPostDTO);
        PointOfInterest createdPointOfInterest = pointOfInterestService.createPointOfInterest(pointOfInterestInput);
        return DTOMapper.INSTANCE.convertEntityToPointOfInterestGetDTO(createdPointOfInterest);
    }
    
    @GetMapping("/roadtrips/{roadtrips_id}/pois")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PointOfInterestGetDTO> getPointOfInterests(@PathVariable Long roadtrip_id) {

        List<PointOfInterest> pointOfInterests = pointOfInterestService.getPointOfInterestsByRoadTrip(roadtrip_id);
        List<PointOfInterestGetDTO> pointOfInterestGetDTOs = new ArrayList<>();

        for (PointOfInterest pointOfInterest : pointOfInterests) {
            pointOfInterestGetDTOs.add(DTOMapper.INSTANCE.convertEntityToPointOfInterestGetDTO(pointOfInterest));
        }

        return pointOfInterestGetDTOs;
    }

}
