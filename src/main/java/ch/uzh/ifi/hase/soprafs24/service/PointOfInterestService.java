package ch.uzh.ifi.hase.soprafs24.service;

import org.slf4j.Logger;

import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
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
    private final RoadtripMemberRepository roadtripMemberRepository;
    
    private final PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    public PointOfInterestService(@Qualifier("pointOfInterestRepository") PointOfInterestRepository pointOfInterestRepository, RoadtripMemberRepository roadtripMemberRepository){
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.roadtripMemberRepository = roadtripMemberRepository;
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

}
