package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("roadtripRepository")
public interface RoadtripRepository extends JpaRepository<Roadtrip, Long> {
  Optional<Roadtrip> findById(Long id);
}
