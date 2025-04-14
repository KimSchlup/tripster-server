package ch.uzh.ifi.hase.soprafs24.service;


import org.slf4j.Logger;

import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class PointOfInterestService {

    private final UserRepository userRepository;

    private final RoadtripRepository roadtripRepository;

    private final Logger log = LoggerFactory.getLogger(PointOfInterestService.class);
    private final PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    public PointOfInterestService(@Qualifier("pointOfInterestRepository") PointOfInterestRepository pointOfInterestRepository, 
                                                                                RoadtripRepository roadtripRepository,
                                                                                UserRepository userRepository){
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.roadtripRepository = roadtripRepository;
        this.userRepository = userRepository;
    }

    public List<PointOfInterest> getPointOfInterests(){
        return this.pointOfInterestRepository.findAll();
    }

    public List<PointOfInterest> getPointOfInterestsByRoadTrip(Long roadtripId){
        System.out.println("looking for Pois for roadtrip: " + roadtripId);
        List<PointOfInterest> pois = this.pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
        
        return pois;
    }

    public PointOfInterest createPointOfInterest(PointOfInterest newPointOfInterest, Long roadtripId, String token){
        
        // set creatorId = userId
        User creator = userRepository.findByToken(token);
        newPointOfInterest.setCreatorId(creator.getUserId());
        
        // set POI roadtrip to the roadtrip the POI has been created in
        Optional<Roadtrip> roadtrip = roadtripRepository.findById(roadtripId);
        newPointOfInterest.setRoadtrip(roadtrip.get());


        newPointOfInterest = pointOfInterestRepository.save(newPointOfInterest);
        pointOfInterestRepository.flush();
        log.debug("Created PointOfInterest with content: {}", newPointOfInterest);
        return newPointOfInterest;
    }

    public PointOfInterest getPointOfInterestByID(Long roadtripId, Long poiId){
        List<PointOfInterest> allPois = getPointOfInterestsByRoadTrip(roadtripId);
        PointOfInterest poi = new PointOfInterest();
        for(PointOfInterest curr : allPois){
            if( poiId == curr.getPoiId()){
                poi = curr;
                log.debug("PointOfInterest with id: "+ poiId + " in roadtrip: "+roadtripId + " found." );
                return poi;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PointOfInterest with id: "+ poiId + " in roadtrip: "+roadtripId + " not found." );
    }

    public void updatePointOfInterest(PointOfInterest oldPointOfInterest, PointOfInterest newPointOfInterest){

        // excludes option to update creatorId
        if(newPointOfInterest.getName() != null){
            oldPointOfInterest.setName(newPointOfInterest.getName());
        }
        if(newPointOfInterest.getCoordinate() != null){
            oldPointOfInterest.setCoordinate(newPointOfInterest.getCoordinate());
        }
        if(newPointOfInterest.getDescription() != null){
            oldPointOfInterest.setDescription(newPointOfInterest.getDescription());
        }
        if(newPointOfInterest.getCategory() != null){
            oldPointOfInterest.setCategory(newPointOfInterest.getCategory());
        }
        if(newPointOfInterest.getStatus() != null){
            oldPointOfInterest.setStatus(newPointOfInterest.getStatus());
        }
        if(newPointOfInterest.getEligibleVoteCount() != null){
            oldPointOfInterest.setEligibleVoteCount(newPointOfInterest.getEligibleVoteCount());
        }
        if(newPointOfInterest.getPriority() != null){
            oldPointOfInterest.setPriority(newPointOfInterest.getPriority());
        }

        log.debug("PointOfInterest with id: "+ newPointOfInterest.getPoiId() + " has been updated" );   
    }

    public void deletePointOfInterest(Long poiId){
        if (!pointOfInterestRepository.existsById(poiId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PointOfInterest not found");
        }
        pointOfInterestRepository.deleteById(poiId);
    }
}
