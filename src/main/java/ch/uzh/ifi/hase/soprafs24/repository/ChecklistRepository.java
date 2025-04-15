package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Checklist;
import ch.uzh.ifi.hase.soprafs24.entity.ChecklistElement;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    //find by roadtripId
    @Query("SELECT c FROM Checklist c WHERE c.roadtrip.roadtripId = :roadtripId")
    List<Checklist> findByRoadtripId(@Param("roadtripId") Long roadtripId);

    //check if checklist exists for roadtrip
    boolean existsByRoadtripId(Long roadtripId);
}
