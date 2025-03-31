package ch.uzh.ifi.hase.soprafs24.service;

import org.slf4j.Logger;

import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

/**
 * Roadtrip Service
 * This class is the "worker" and responsible for all functionality related to
 * the rodatrip.
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller
 * 
 */



@Service
@Transactional
public class RoadtripService {

    private final Logger log = LoggerFactory.getLogger(RoadtripService.class);

    private final RoadtripRepository roadtripRepository;

    @Autowired
    public RoadtripService(@Qualifier("roadtripRepository") RoadtripRepository roadtripRepository) {
        this.roadtripRepository = roadtripRepository;
    }

    public List<Roadtrip> getRoadtrips() {
        return this.roadtripRepository.findAll();
    }

    public Roadtrip createRoadtrip(Roadtrip newRoadtrip) {

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newRoadtrip = roadtripRepository.save(newRoadtrip);
        roadtripRepository.flush();

        log.debug("Created Information for Roadtrip: {}", newRoadtrip);
        return newRoadtrip;
    }

    public void deleteRoadtrip(Long roadtripId) {
        if (!roadtripRepository.existsById(roadtripId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found");
        }
        roadtripRepository.deleteById(roadtripId);
    }


}