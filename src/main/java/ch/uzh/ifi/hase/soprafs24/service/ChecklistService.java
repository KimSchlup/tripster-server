package ch.uzh.ifi.hase.soprafs24.service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Checklist;
import ch.uzh.ifi.hase.soprafs24.entity.ChecklistElement;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.repository.ChecklistRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChecklistElementRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;


@Service
@Transactional
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final RoadtripRepository roadtripRepository;
    private final RoadtripMemberRepository roadtripMemberRepository;
    private final UserRepository userRepository;
    private final ChecklistElementRepository checklistElementRepository;

    @Autowired
    public ChecklistService(ChecklistRepository checklistRepository, RoadtripRepository roadtripRepository, RoadtripMemberRepository roadtripMemberRepository, UserRepository userRepository, ChecklistElementRepository checklistElementRepository) {
        this.checklistRepository = checklistRepository;
        this.roadtripRepository = roadtripRepository;
        this.userRepository = userRepository;
        this.checklistElementRepository = checklistElementRepository;
        this.roadtripMemberRepository = roadtripMemberRepository;
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


        // Check if assignedUser is provided and valid
        if (element.getAssignedUser() != null && element.getAssignedUser().getUsername() != null) {
            String assignedUsername = element.getAssignedUser().getUsername();

            // Retrieve userId by username
            Long userId = userRepository.findIdByUsername(assignedUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            // Retrieve the User entity using userId
            User assignedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            
            //ensure that assigned User is part of the roadtrip
            checkIfAssignedUserIsInRoadtrip(roadtripId, assignedUser);

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

    public void updateChecklistElement(ChecklistElement updatedElement, Long elementId, Long roadtripId){
        ChecklistElement element = this.checklistElementRepository.findById(elementId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ChecklistElement not found"));
        
            if (updatedElement.getName() != null) {
                element.setName(updatedElement.getName());
            }
            if (updatedElement.getIsCompleted() != null) {
                element.setIsCompleted(updatedElement.getIsCompleted());
            }
            if (updatedElement.getCategory() != null) {
                element.setCategory(updatedElement.getCategory());
            }
            if (updatedElement.getPriority() != null) {
                element.setPriority(updatedElement.getPriority());
            }

        // Update assigned user if provided
        if (updatedElement.getAssignedUser() != null){
            if (updatedElement.getAssignedUser().getUsername() != null) {
                String assignedUsername = updatedElement.getAssignedUser().getUsername();

                // Retrieve userId by username
                Long assignedUserId = userRepository.findIdByUsername(assignedUsername)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

                // Retrieve the User entity using userId
                User assignedUser = userRepository.findById(assignedUserId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

                //ensure that assigned User is part of the roadtrip
                checkIfAssignedUserIsInRoadtrip(roadtripId, assignedUser);
                
                // Ensure the User entity is managed
                assignedUser = userRepository.save(assignedUser);

                element.setAssignedUser(assignedUser);
                } else {
                    element.setAssignedUser(null);
                }
            }
        }

    public void deleteChecklistElement(Long checklistelementId){
        ChecklistElement checklistElement = this.checklistElementRepository.findById(checklistelementId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        
        this.checklistElementRepository.delete(checklistElement);
        this.checklistElementRepository.flush();
    }

    //Helper method to check if checklist already exists for roadtrip
    public void checkAccessRights(long roadtripId, String token){
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));
        User user = userRepository.findByToken(token);

        RoadtripMember roadtripMember = roadtripMemberRepository.findByUserAndRoadtrip(user, roadtrip);
        //check if user is a member of this roadtrip
        if (roadtripMember == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowd to access this resource");
        }
    }

  //Helper method to verify if assignedUser is part of the roadtrip
    public void checkIfAssignedUserIsInRoadtrip(long roadtripId, User user){
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        RoadtripMember roadtripMember = roadtripMemberRepository.findByUserAndRoadtrip(user, roadtrip);

        //check if user is a member of this roadtrip
        if (roadtripMember == null ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The user you tried to assign is not part of this roadtrip");
        }  
    }




}
