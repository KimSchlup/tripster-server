package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Checklist;
import ch.uzh.ifi.hase.soprafs24.entity.ChecklistElement;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface ChecklistElementRepository extends JpaRepository<ChecklistElement, Long> {
    // public List<ChecklistElement> getChecklistElementsByUsername(String username) {
    //     return checklistElementRepository.findByAssignedUserUsername(username);
    // }
}
