package ch.uzh.ifi.hase.soprafs24.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.RoadtripSettings;

@Repository("roadtripSettingsRepository")
public interface RoadtripSettingsRepository extends JpaRepository<RoadtripSettings, Long> {
    Optional<RoadtripSettings> findByRoadtrip_RoadtripId(Long roadtripId);
}
