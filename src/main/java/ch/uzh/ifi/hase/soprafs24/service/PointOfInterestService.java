package ch.uzh.ifi.hase.soprafs24.service;

import org.slf4j.Logger;

import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripSettings;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PointOfInterestService {

    private final UserRepository userRepository;

    private final RoadtripRepository roadtripRepository;

    private final Logger log = LoggerFactory.getLogger(PointOfInterestService.class);
    private final PointOfInterestRepository pointOfInterestRepository;

    public PointOfInterestService(@Qualifier("pointOfInterestRepository") PointOfInterestRepository pointOfInterestRepository, 
                                                                                RoadtripRepository roadtripRepository,
                                                                                UserRepository userRepository){
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.roadtripRepository = roadtripRepository;
        this.userRepository = userRepository;
    }

    public List<PointOfInterest> getPointOfInterests() {
        return this.pointOfInterestRepository.findAll();
    }

    public List<PointOfInterest> getPointOfInterestsByRoadTrip(String token, Long roadtripId){
        if(!isUserMemberOfRoadtrip(token, roadtripId)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not a member of the roadtrip");
        }

        List<PointOfInterest> pois = this.pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);

        return pois;
    }

    public PointOfInterest createPointOfInterest(PointOfInterest newPointOfInterest, Long roadtripId, String token) {
        if(!isUserMemberOfRoadtrip(token, roadtripId)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not a member of the roadtrip");
        }
        // set creatorId = userId
        User creator = userRepository.findByToken(token);
        newPointOfInterest.setCreatorId(creator.getUserId());

        // set POI roadtrip to the roadtrip the POI has been created in
        Optional<Roadtrip> roadtrip = roadtripRepository.findById(roadtripId);
        newPointOfInterest.setRoadtrip(roadtrip.get());

        // default value for eligibleVoteCount is set to 1 since there is at least one
        // person anyway
        if (newPointOfInterest.getEligibleVoteCount() == null) {
            newPointOfInterest.setEligibleVoteCount(1);
        }
        // default status is set to pending
        if (newPointOfInterest.getStatus() == null) {
            newPointOfInterest.setStatus(AcceptanceStatus.PENDING);
        }
        // default prio set to low since it can't be that high if one forgets to set
        // is...
        if (newPointOfInterest.getPriority() == null) {
            newPointOfInterest.setPriority(PoiPriority.LOW);
        }

        newPointOfInterest = pointOfInterestRepository.save(newPointOfInterest);
        pointOfInterestRepository.flush();
        log.debug("Created PointOfInterest with content: {}", newPointOfInterest);
        return newPointOfInterest;
    }

    public PointOfInterest getPointOfInterestByID(String token, Long roadtripId, Long poiId) {
        List<PointOfInterest> allPois = getPointOfInterestsByRoadTrip(token, roadtripId);
        PointOfInterest poi = new PointOfInterest();
        for (PointOfInterest curr : allPois) {
            if (poiId == curr.getPoiId()) {
                poi = curr;
                return poi;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "PointOfInterest with id: " + poiId + " in roadtrip: " + roadtripId + " not found.");
    }

    public void updatePointOfInterest(PointOfInterest oldPointOfInterest, PointOfInterest newPointOfInterest) {

        // excludes option to update creatorId
        if (newPointOfInterest.getName() != null) {
            oldPointOfInterest.setName(newPointOfInterest.getName());
        }
        if (newPointOfInterest.getCoordinate() != null) {
            oldPointOfInterest.setCoordinate(newPointOfInterest.getCoordinate());
        }
        if (newPointOfInterest.getDescription() != null) {
            oldPointOfInterest.setDescription(newPointOfInterest.getDescription());
        }
        if (newPointOfInterest.getCategory() != null) {
            oldPointOfInterest.setCategory(newPointOfInterest.getCategory());
        }
        if (newPointOfInterest.getStatus() != null) {
            oldPointOfInterest.setStatus(newPointOfInterest.getStatus());
        }
        if (newPointOfInterest.getEligibleVoteCount() != null) {
            oldPointOfInterest.setEligibleVoteCount(newPointOfInterest.getEligibleVoteCount());
        }
        if (newPointOfInterest.getPriority() != null) {
            oldPointOfInterest.setPriority(newPointOfInterest.getPriority());
        }

        log.debug("PointOfInterest with id: " + newPointOfInterest.getPoiId() + " has been updated");
    }

    public void deletePointOfInterest(String token, Long roadtripId, Long poiId) {
        if(!isUserMemberOfRoadtrip(token, roadtripId)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not a member of the roadtrip");
        }
        if (!pointOfInterestRepository.existsById(poiId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PointOfInterest not found");
        }
        pointOfInterestRepository.deleteById(poiId);
    }

    public void castVote(String token, Long roadtripId, Long poiId, String vote) {
        if(!(vote.equals("upvote") || vote.equals("downvote"))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Casted vote does not match requirement: "+ vote);
        }
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));
        RoadtripSettings setting = roadtrip.getRoadtripSettings();
        List<RoadtripMember> roadtripMembers = roadtrip.getRoadtripMembers();
        int acceptedMembers = 0;
        for(RoadtripMember member : roadtripMembers){
            if(member.getInvitationStatus() == InvitationStatus.ACCEPTED){
                acceptedMembers++;
            }
        }
        List<PointOfInterest> pois = pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
        PointOfInterest poi = new PointOfInterest();
        User user = userRepository.findByToken(token);
        Long userId = user.getUserId();
        for(PointOfInterest temp : pois){
            if(temp.getPoiId() == poiId){
                poi = temp;
                break;
            }
        }

        if(poi.getUpvotes()== null){
            poi.setUpvotes(new ArrayList<Long>());
        }
        if(poi.getDownvotes()== null){
            poi.setDownvotes(new ArrayList<Long>());
        }

        List<Long> downvotes = poi.getDownvotes();
        List<Long> upvotes = poi.getUpvotes();

        if(vote.equals("upvote")){
            if(downvotes.contains(userId)){
                downvotes.remove(userId);
            }
            if(!(upvotes.contains(userId))){
                upvotes.add(userId);
                poi.setUpvotes(upvotes);
            }
        }else if(vote.equals("downvote")){
            if(upvotes.contains(userId)){
                upvotes.remove(userId);
            }
            if(!(downvotes.contains(userId))){
                downvotes.add(userId);
                poi.setDownvotes(downvotes);
            }
        }
        
        // check if acceptance status changes
        if(setting.getDecisionProcess()== DecisionProcess.MAJORITY){
            // if only one person is in roadtrip, set status to accepted or declined
            if(acceptedMembers== 1){
                if(vote.equals("upvote")){
                    poi.setStatus(AcceptanceStatus.ACCEPTED);
                }else if(vote.equals("downvote")){
                    poi.setStatus(AcceptanceStatus.DECLINED);
                }
            }else{ 
                if(upvotes.size() > (acceptedMembers)/2){
                    poi.setStatus(AcceptanceStatus.ACCEPTED);
                }else if(downvotes.size() > (acceptedMembers)/2){
                    poi.setStatus(AcceptanceStatus.DECLINED);
                }
            }
        }else{
            if(isUserOwnerOfRoadtrip(token, roadtripId)){
                if(vote.equals("upvote")){
                    poi.setStatus(AcceptanceStatus.ACCEPTED);
                }else if(vote.equals("downvote")){
                    poi.setStatus(AcceptanceStatus.DECLINED);
                }
            }
        }

    }

    public void deleteVote(String token, Long roadtripId, Long poiId) {
        if(!isUserMemberOfRoadtrip(token, roadtripId)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not a member of the roadtrip");
        }
        List<PointOfInterest> pois = pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
        PointOfInterest poi = new PointOfInterest();
        User user = userRepository.findByToken(token);
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));
        RoadtripSettings setting = roadtrip.getRoadtripSettings();

        for(PointOfInterest temp : pois){
            if(temp.getPoiId() == poiId){
                poi = temp;
                break;
            }
        }
        List<Long> upvotes = poi.getUpvotes();
        List<Long> downvotes = poi.getDownvotes();
        String vote = "";

        if(upvotes.contains(user.getUserId())){
               upvotes.remove(user.getUserId());
               poi.setUpvotes(upvotes);
               vote = "upvote";
        }
        if(downvotes.contains(user.getUserId())){
               downvotes.remove(user.getUserId());
               poi.setDownvotes(downvotes);
                vote = "downvote";
        }

        if(setting.getDecisionProcess()== DecisionProcess.MAJORITY){
            if(upvotes.size() > (roadtrip.getRoadtripMembers().size()+1)/2){
                poi.setStatus(AcceptanceStatus.ACCEPTED);
            }else if(downvotes.size() > (roadtrip.getRoadtripMembers().size()+1)/2){
                poi.setStatus(AcceptanceStatus.DECLINED);
            }
        }else{
            if(isUserOwnerOfRoadtrip(token, roadtripId)){
                poi.setStatus(AcceptanceStatus.PENDING);
            }
        }

    }

    public boolean isUserMemberOfRoadtrip(String token, Long roadtripId) {
        // Find the user by token
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    
        // Find the roadtrip by ID
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));
    
        // Check if the user is the owner of the roadtrip
        if (roadtrip.getOwner().equals(user)) {
            return true;
        }
    
        // Check if the user is a member of the roadtrip
        if (roadtrip.getRoadtripMembers() != null && roadtrip.getRoadtripMembers().stream()
                .anyMatch(member -> member.getUser().equals(user))) {
            return true;
        }
    
        return false;
    }

    public boolean isUserOwnerOfRoadtrip(String token, Long roadtripId) {
        // Find the user by token
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    
        // Find the roadtrip by ID
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));
    
        // Check if the user is the owner of the roadtrip
        if (roadtrip.getOwner().equals(user)) {
            return true;
        }
    
        return false;
    }

    public void decisionProcessUpdated(DecisionProcess process, PointOfInterest poi, Roadtrip roadtrip, Long userId){
        List<Long> upvotes = poi.getUpvotes();
        List<Long> downvotes = poi.getDownvotes();

        List<RoadtripMember> roadtripMembers = roadtrip.getRoadtripMembers();
        int voteCount = 0;
        for(RoadtripMember member : roadtripMembers){
            if(member.getInvitationStatus() == InvitationStatus.ACCEPTED){
                voteCount++;
            }
        }
        if(process == DecisionProcess.MAJORITY){
            if(upvotes.size() > (voteCount/2)){
                poi.setStatus(AcceptanceStatus.ACCEPTED);
            }else if(downvotes.size() > (voteCount/2)){
                poi.setStatus(AcceptanceStatus.DECLINED);
            }
        }else{
            if(upvotes.contains(userId)){
                poi.setStatus(AcceptanceStatus.ACCEPTED);
        }else if(downvotes.contains(userId)){
                poi.setStatus(AcceptanceStatus.DECLINED);
            }
        }
    }

    public void calculateStatus(String token, PointOfInterest poi, Long roadtripId) {
        // TODO Auto-generated method stub
        User user = userRepository.findByToken(token);
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));
        RoadtripSettings setting = roadtrip.getRoadtripSettings();

        List<Long> upvotes = poi.getUpvotes();
        List<Long> downvotes = poi.getDownvotes();

        List<RoadtripMember> roadtripMembers = roadtrip.getRoadtripMembers();
        int voteCount = 1;
        for(RoadtripMember member : roadtripMembers){
            if(member.getInvitationStatus() == InvitationStatus.ACCEPTED){
                voteCount++;
            }
        }
        if(setting.getDecisionProcess() == DecisionProcess.MAJORITY){
            if(upvotes.size() >= (voteCount/2)){
                poi.setStatus(AcceptanceStatus.ACCEPTED);
            }else if(downvotes.size() >= (voteCount/2)){
                poi.setStatus(AcceptanceStatus.DECLINED);
            }
        }else if(isUserMemberOfRoadtrip(token, roadtripId)){
            if(upvotes.contains(user.getUserId())){
                poi.setStatus(AcceptanceStatus.ACCEPTED);
        }else if(downvotes.contains(user.getUserId())){
                poi.setStatus(AcceptanceStatus.DECLINED);
            }
        }
    }

}
