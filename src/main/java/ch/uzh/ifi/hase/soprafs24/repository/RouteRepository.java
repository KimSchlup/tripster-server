package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    // Check if a route exists by startId and endId
    boolean existsByStartIdAndEndId(Long startId, Long endId);

    // Delete a route by startId and endId
    void deleteByStartIdAndEndId(Long startId, Long endId);

    // Find all routes by a specific PointOfInterest ID
    List<Route> findByStartIdOrEndId(Long startId, Long endId);

    // Find all routes by a specific Roadtrip ID
    List<Route> findByRoadtrip_RoadtripId(Long roadtripId);  // Simplified version
}