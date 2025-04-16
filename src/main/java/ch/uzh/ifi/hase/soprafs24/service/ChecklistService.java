package ch.uzh.ifi.hase.soprafs24.service;

import jakarta.transaction.Transactional;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.constant.Priority;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Checklist;
import ch.uzh.ifi.hase.soprafs24.entity.ChecklistElement;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.repository.ChecklistRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChecklistElementRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistElementPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.constant.ChecklistCategory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@Transactional
public class ChecklistService {

  private final ChecklistRepository checklistRepository;
  private final RoadtripRepository roadtripRepository;
  private final UserRepository userRepository;
  private final ChecklistElementRepository checklistElementRepository;
  private static final Logger log = LoggerFactory.getLogger(ChecklistService.class);

  @Autowired
  public ChecklistService(ChecklistRepository checklistRepository, RoadtripRepository roadtripRepository, UserRepository userRepository, ChecklistElementRepository checklistElementRepository) {
      this.checklistRepository = checklistRepository;
      this.roadtripRepository = roadtripRepository;
      this.userRepository = userRepository;
      this.checklistElementRepository = checklistElementRepository;
  }

  public Checklist getChecklistByRoadtripId(Long roadtripId) {
    return checklistRepository.findByRoadtripId(roadtripId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checklist not found for this roadtrip"));
}

public ChecklistElement addChecklistElement(Long roadtripId, ChecklistElement element) {
    // Retrieve the Checklist entity associated with the roadtrip
    Checklist checklist = checklistRepository.findByRoadtripId(roadtripId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Checklist not found for this roadtrip"));

    // Set the checklist for the element
    element.setChecklist(checklist);
    System.out.println(element.getAssignedUser().getUsername());

    // Check if assignedUser is provided and valid
    if (element.getAssignedUser() != null && element.getAssignedUser().getUsername() != null) {
        String assignedUsername = element.getAssignedUser().getUsername();

        // Retrieve userId by username
        Long userId = userRepository.findIdByUsername(assignedUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Retrieve the User entity using userId
        User assignedUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Ensure the User entity is managed
        assignedUser = userRepository.save(assignedUser);

        element.setAssignedUser(assignedUser);
    } else {
        element.setAssignedUser(null); // Ensure assignedUser is null if not provided
    }

    //Check if isCompleted is set, if not put on default false
    if (element.getIsCompleted() == null){
        element.setIsCompleted(false);
    }

    // Save the checklist element
    return checklistElementRepository.save(element);
    
}

    // public Checklist createChecklist(Long roadtripId, Checklist newChecklist) {
    //     // Verify if a checklist already exists for the roadtrip
    //     checkifChecklistexists(newChecklist);

    //     // Retrieve the Roadtrip entity
    //     Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
    //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

    //     // Set the roadtrip to the checklist
    //     newChecklist.setRoadtrip(roadtrip);

    //     // Ensure each ChecklistElement has a valid assignedUser if provided
    //     for (ChecklistElementPostDTO elementDTO : newChecklist.getChecklistElements()) {
    //         ChecklistElement element = DTOMapper.INSTANCE.convertChecklistElementPostDTOToEntity(elementDTO);
    //         element.setChecklist(newChecklist);

    //         // Check if assignedUser is provided and valid
    //         if (elementDTO.getAssignedUser() != null && !elementDTO.getAssignedUser().isEmpty()) {
    //             User assignedUser = userRepository.findByUsername(elementDTO.getAssignedUser())
    //                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    //             element.setAssignedUser(assignedUser);
    //         } else {
    //             element.setAssignedUser(null); // Ensure assignedUser is null if not provided
    //         }

    //         // Add the converted element back to the checklist
    //         newChecklist.getChecklistElements().add(element);
    //     }
    //     // Save the checklist
    //     newChecklist = checklistRepository.save(newChecklist);
    //     checklistRepository.flush();

    //     // Log the creation (uncomment if you have a logger configured)
    //     // log.debug("Created Information for Checklist: {}", newChecklist);

    //     return newChecklist;
    // }

    //Helper method to check if checklist already exists for roadtrip
    public void checkifChecklistexists(Checklist checklisTtoBeCreated){
        Boolean checklistByRoadtripId = checklistRepository.existsByRoadtripId(checklisTtoBeCreated.getRoadtripId());

        if (checklistByRoadtripId != false){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Checklist already exists for this roadtrip");
            }
  }

  //Helper method to verify access rights

}
