package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterestComment;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class PointOfInterestCommentService {
    private final UserRepository userRepository;


    private final PointOfInterestRepository pointOfInterestRepository;

    public PointOfInterestCommentService(@Qualifier("pointOfInterestRepository") PointOfInterestRepository pointOfInterestRepository, 
                                                                                UserRepository userRepository){
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.userRepository = userRepository;
    }

    public PointOfInterestComment addComment(String token, String comment, Long poiId, Long roadtripId){

        PointOfInterestComment poiComment = new PointOfInterestComment();
        List<PointOfInterest> pois = pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
        PointOfInterest poi = new PointOfInterest();
        User author = userRepository.findByToken(token);        
        
        for(PointOfInterest temp : pois){
            if(poiId.equals(temp.getPoiId())){
                poi = temp;
            }
        }

        if(poi.equals(null)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poi "+ poiId + "was not found for roadtrip " + roadtripId );
        }

        List<PointOfInterestComment> comments = poi.getPointOfInterestComment();

        if(comments.equals(null)){
            comments = new ArrayList<>();
        }
        
        poiComment.setPoi(poi);
        poiComment.setAuthorId(author.getUserId());
        poiComment.setComment(comment);
        poiComment.setCreationDate(LocalDate.now());
        
        comments.add(poiComment);
        
        poi.setPointOfInterestComments(comments);
        pointOfInterestRepository.save(poi);
        pointOfInterestRepository.flush();

        return poiComment;
    }


    public List<PointOfInterestComment> getComment(String token, Long poiId, Long roadtripId){

        List<PointOfInterest> pois = pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
        PointOfInterest poi = new PointOfInterest();
        
        for(PointOfInterest temp : pois){
            if(poiId.equals(temp.getPoiId())){
                poi = temp;
            }
        }

        if(poi.equals(null)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poi "+ poiId + "was not found for roadtrip " + roadtripId);
        }

        return poi.getPointOfInterestComment();
    }

    public void deleteComment(String token, Long commentId, Long poiId, Long roadtripId){

        List<PointOfInterest> pois = pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
        PointOfInterest poi = new PointOfInterest();
        User author = userRepository.findByToken(token);
        Boolean deleted = false;
    
        for(PointOfInterest temp : pois){
            if(poiId.equals(temp.getPoiId())){
                poi = temp;
            }
        }

        if(poi.equals(null)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poi "+ poiId + "was not found for roadtrip " + roadtripId);
        }

        List<PointOfInterestComment> comments = poi.getPointOfInterestComment();
        for(PointOfInterestComment temp : comments){
            if(temp.getAuthorId().equals(author.getUserId()) && temp.getCommentId().equals(commentId)){
                comments.remove(temp);
                deleted = true;
            }
        }
        if(!deleted){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No comment with matching id "+ commentId + " of the author " + author.getUserId()+ " was found");
        }
        poi.setPointOfInterestComments(new ArrayList<PointOfInterestComment>(comments));

    }
}
