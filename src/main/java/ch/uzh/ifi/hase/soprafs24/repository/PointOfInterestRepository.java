package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {

    // Find Poi by it's id
    PointOfInterest findByPoiId(Long poiId);

    // Find all Poi's by it's creatorId
    List<PointOfInterest> findByCreatorId(Long creatorId);

    // Find all Poi's of a trip
    List<PointOfInterest> findByRoadtripId(Long roadtripId);

    // Find all Poi's by their name
    List<PointOfInterest> findByName(String name);

    // Find all Poi's of a Roadtrip and with respective status
    List<PointOfInterest> findByRoadtripAndStatus(Roadtrip roadtrip, AcceptanceStatus status);

}