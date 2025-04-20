package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterestComment;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestCommentPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.PointOfInterestCommentService;


@RestController
public class PointOfInterestCommentController {

    private final PointOfInterestCommentService pointOfInterestCommentService;

    PointOfInterestCommentController(PointOfInterestCommentService pointOfInterestCommentService){
        this.pointOfInterestCommentService = pointOfInterestCommentService;
    }

    // TODO test implementation
    @PostMapping("/roadtrips/{roadtripId}/pois/{poiId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PointOfInterestComment addComment(@RequestHeader("Authorization") String token, 
                                                    @RequestBody PointOfInterestCommentPostDTO commentPostDTO, 
                                                    @PathVariable Long roadtripId, 
                                                    @PathVariable Long poiId ){
        
        PointOfInterestComment poiComment = DTOMapper.INSTANCE.converPointOfInterestCommentPostDTOToEntity(commentPostDTO);
        PointOfInterestComment newComment = pointOfInterestCommentService.addComment(token, poiComment.getComment(), poiId, roadtripId);
        return newComment;
    }
    
    // TODO test implementation
    @DeleteMapping("/roadtrips/{roadtripId}/pois/{poiId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteComment(@RequestHeader("Authorization") String token, 
                                                @RequestBody PointOfInterestCommentPostDTO commentPostDTO, 
                                                @PathVariable Long roadtripId, 
                                                @PathVariable Long poiId,
                                                @PathVariable Long commentId){
        pointOfInterestCommentService.deleteComment(token, commentId, poiId, roadtripId);
    }

    // TODO test implementation
    @GetMapping("/roadtrips/{roadtripId}/pois/{poiId}/comments")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PointOfInterestComment> getComments(@RequestHeader("Authorization") String token,  
                                                        @PathVariable Long roadtripId, 
                                                        @PathVariable Long poiId,
                                                        @PathVariable Long commentId){
        
        return pointOfInterestCommentService.getComment(token, poiId, roadtripId);
    }

}
