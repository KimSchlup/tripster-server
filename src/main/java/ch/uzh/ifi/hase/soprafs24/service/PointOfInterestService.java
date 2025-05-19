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
import ch.uzh.ifi.hase.soprafs24.entity.Route;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RouteRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Point;

@Service
@Transactional
public class PointOfInterestService {

    private final RouteRepository routeRepository;

    private final UserRepository userRepository;

    private final RoadtripRepository roadtripRepository;

    private final Logger log = LoggerFactory.getLogger(PointOfInterestService.class);
    private final PointOfInterestRepository pointOfInterestRepository;

    public PointOfInterestService(@Qualifier("pointOfInterestRepository") PointOfInterestRepository pointOfInterestRepository, 
                                                                                RoadtripRepository roadtripRepository,
                                                                                UserRepository userRepository, RouteRepository routeRepository){
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.roadtripRepository = roadtripRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
    }

    public List<PointOfInterest> getPointOfInterests() {
        return this.pointOfInterestRepository.findAll();
    }

    // Update getPointOfInterestsByRoadTrip
    public List<PointOfInterest> getPointOfInterestsByRoadTrip(String token, Long roadtripId) {
        verifyUserAccess(token, roadtripId);
        return this.pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
    }

    @Transactional
    // Update createPointOfInterest
    public PointOfInterest createPointOfInterest(PointOfInterest newPointOfInterest, Long roadtripId, String token) {
        verifyUserAccess(token, roadtripId);
        // Validate POI data
        if (newPointOfInterest.getName() == null || newPointOfInterest.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "POI name cannot be empty");
        }
        if (newPointOfInterest.getCoordinate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "POI coordinates are required");
        }

        // set creatorId = userId
        User user = userRepository.findByToken(token);
        newPointOfInterest.setCreatorId(user.getUserId());

        // set POI roadtrip to the roadtrip the POI has been created in
        Optional<Roadtrip> roadtrip = roadtripRepository.findById(roadtripId);
        newPointOfInterest.setRoadtrip(roadtrip.get());

        RoadtripSettings settings = roadtrip.get().getRoadtripSettings();
        if(!isInsideBoundingBox(newPointOfInterest,settings.getBoundingBox())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "POI is outside of bounding box");
        }
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

    // Update updatePointOfInterest - add token and roadtripId parameters
    @Transactional
    public void updatePointOfInterest(PointOfInterest oldPointOfInterest, PointOfInterest newPointOfInterest) {
        // Validate input
        if (newPointOfInterest.getName() != null && newPointOfInterest.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "POI name cannot be empty");
        }
        // check if POI is inside bounding box
        Roadtrip roadtrip = oldPointOfInterest.getRoadtrip();
        RoadtripSettings settings = roadtrip.getRoadtripSettings();
        if(!isInsideBoundingBox(newPointOfInterest,settings.getBoundingBox())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New POI is outside of bounding box");
        }

        // Update fields
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

        pointOfInterestRepository.save(oldPointOfInterest);
        pointOfInterestRepository.flush();
        log.debug("PointOfInterest with id: {} has been updated", oldPointOfInterest.getPoiId());
    }

    // Update deletePointOfInterest
    public void deletePointOfInterest(String token, Long roadtripId, Long poiId) {
        verifyUserAccess(token, roadtripId);
        if (!pointOfInterestRepository.existsById(poiId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PointOfInterest not found");
        }
        pointOfInterestRepository.deleteById(poiId);
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId).get();
        List<Route> routes = roadtrip.getRoutes();
        List<Long> deletable_routes = new ArrayList<>();
        for(Route route: routes){
            if(route.getStartId().equals(poiId) || route.getEndId().equals(poiId)){
                deletable_routes.add(route.getRouteId());
            }
        }
        routeRepository.deleteAllById(deletable_routes);
    }

    // Update castVote
    public void castVote(String token, Long roadtripId, Long poiId, String vote) {
        if(!(vote.equals("upvote") || vote.equals("downvote"))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Casted vote does not match requirement: "+ vote);
        }
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found with id: " +roadtripId));
                
        PointOfInterest poi = pointOfInterestRepository.findById(poiId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poi not found with id: " +poiId));


        User user = userRepository.findByToken(token);
        Long userId = user.getUserId();

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
        pointOfInterestRepository.save(poi);
        pointOfInterestRepository.flush();
        // check if acceptance status changes
        calculateStatus(token, poi, roadtripId);
    }

    // Update deleteVote
    public void deleteVote(String token, Long roadtripId, Long poiId) {
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
        pointOfInterestRepository.save(poi);
        pointOfInterestRepository.flush();
        calculateStatus(token, poi, roadtripId);
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

    public void calculateStatus(String token, PointOfInterest poi, Long roadtripId) {
        User user = userRepository.findByToken(token);
        poi = pointOfInterestRepository.findById(poi.getPoiId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PointOfInterest not found"));
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
        }else if(isUserOwnerOfRoadtrip(token, roadtripId)){
            if(upvotes.contains(user.getUserId())){
                poi.setStatus(AcceptanceStatus.ACCEPTED);
            }else if(downvotes.contains(user.getUserId())){
                poi.setStatus(AcceptanceStatus.DECLINED);
            }
        }
        // if no more votes exist, set status to pending again
        if(upvotes.size() == 0 && downvotes.size() == 0 ){
            poi.setStatus(AcceptanceStatus.PENDING);
        }

        pointOfInterestRepository.save(poi);
        pointOfInterestRepository.flush();
    }

    private void verifyUserAccess(String token, Long roadtripId) {
        if(!isUserMemberOfRoadtrip(token, roadtripId)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not a member of the roadtrip");
        }
    }

    public boolean isInsideBoundingBox(PointOfInterest poi, Polygon boundingBox){
        if(boundingBox == null || boundingBox.isEmpty()){
            return true;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(poi.getCoordinate().getX(), poi.getCoordinate().getY()));
        return boundingBox.contains(point);
    }

}
