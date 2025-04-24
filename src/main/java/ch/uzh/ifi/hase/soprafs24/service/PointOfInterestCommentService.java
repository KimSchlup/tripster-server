package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterestComment;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
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
    private final RoadtripMemberRepository roadtripMemberRepository; // Add this

    public PointOfInterestCommentService(
            @Qualifier("pointOfInterestRepository") PointOfInterestRepository pointOfInterestRepository,
            UserRepository userRepository,
            RoadtripMemberRepository roadtripMemberRepository) { // Add parameter
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.userRepository = userRepository;
        this.roadtripMemberRepository = roadtripMemberRepository; // Add this
    }

    public PointOfInterestComment addComment(String token, String comment, Long poiId, Long roadtripId) {
        // Verify user exists and is authenticated
        User author = userRepository.findByToken(token);
        if (author == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // Find point of interest and verify it exists
        List<PointOfInterest> pois = pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
        PointOfInterest poi = pois.stream()
                .filter(p -> p.getPoiId().equals(poiId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Poi " + poiId + " was not found for roadtrip " + roadtripId));

        // Find and verify the user's membership status
        RoadtripMember member = poi.getRoadtrip().getRoadtripMembers().stream()
                .filter(m -> m.getUser().getUserId().equals(author.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "User is not a member of this roadtrip"));

        if (member.getInvitationStatus() != InvitationStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "User must accept the roadtrip invitation before adding comments");
        }

        // Create and set up the comment
        PointOfInterestComment poiComment = new PointOfInterestComment();
        poiComment.setPoi(poi);
        poiComment.setAuthorId(author.getUserId());
        poiComment.setComment(comment);
        poiComment.setCreationDate(LocalDate.now());

        // Add comment to POI
        List<PointOfInterestComment> comments = poi.getPointOfInterestComment();
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(poiComment);
        poi.setPointOfInterestComments(comments);

        // Save changes
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

    public void deleteComment(String token, Long commentId, Long poiId, Long roadtripId) {
        List<PointOfInterest> pois = pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
        PointOfInterest poi = null;
        User author = userRepository.findByToken(token);
        boolean deleted = false;

        for (PointOfInterest temp : pois) {
            if (poiId.equals(temp.getPoiId())) {
                poi = temp;
                break;
            }
        }

        if (poi == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poi " + poiId + " was not found for roadtrip " + roadtripId);
        }

        List<PointOfInterestComment> comments = poi.getPointOfInterestComment();
        for (PointOfInterestComment temp : comments) {
            if (temp.getAuthorId().equals(author.getUserId()) && temp.getCommentId().equals(commentId)) {
                poi.removeComment(temp); // Use the helper method
                deleted = true;
                break;
            }
        }

        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No comment with matching id " + commentId + " of the author " + author.getUserId() + " was found");
        }

        pointOfInterestRepository.save(poi); // Persist the changes
        pointOfInterestRepository.flush();
    }
}
