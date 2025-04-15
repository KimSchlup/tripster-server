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
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterestComment;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestCommentPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.PointOfInterestService;


@RestController
public class PointOfInterestCommentController {

    private final PointOfInterestService pointOfInterestService;

    PointOfInterestCommentController(PointOfInterestService pointOfInterestService){
        this.pointOfInterestService = pointOfInterestService;
    }


    @PostMapping("/roadtrips/{roadtripId}/pois/{poiId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PointOfInterestComment createComment(@RequestHeader("Authorization") String token, 
                                                @RequestBody PointOfInterestCommentPostDTO commentPostDTO, 
                                                @PathVariable Long roadtripId, 
                                                @PathVariable Long poiId ){

    }

    @DeleteMapping("/roadtrips/{roadtripId}/pois/{poiId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteComment(@RequestHeader("Authorization") String token, 
                                                @RequestBody PointOfInterestCommentPostDTO commentPostDTO, 
                                                @PathVariable Long roadtripId, 
                                                @PathVariable Long poiId,
                                                @PathVariable Long commentId){

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody

}
