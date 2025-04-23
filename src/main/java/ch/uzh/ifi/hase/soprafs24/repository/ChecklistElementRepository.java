package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.ChecklistElement;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistElementRepository extends JpaRepository<ChecklistElement, Long> {

}
