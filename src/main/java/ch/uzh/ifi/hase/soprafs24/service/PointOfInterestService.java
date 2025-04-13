package ch.uzh.ifi.hase.soprafs24.service;

import org.geolatte.geom.Point;
import org.slf4j.Logger;

import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.util.List;


@Service
@Transactional
public class PointOfInterestService {

    private final Logger log = LoggerFactory.getLogger(PointOfInterestService.class);
    private final PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    public PointOfInterestService(@Qualifier("pointOfInterestRepository") PointOfInterestRepository pointOfInterestRepository){
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    public List<PointOfInterest> getPointOfInterests(){
        return this.pointOfInterestRepository.findAll();
    }

    public List<PointOfInterest> getPointOfInterestsByRoadTrip(Long roadtrip_id){
        return this.pointOfInterestRepository.findByRoadtripId(roadtrip_id);
    }

    public PointOfInterest createPointOfInterest(PointOfInterest newPointOfInterest){
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
