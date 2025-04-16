package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Checklist;
import ch.uzh.ifi.hase.soprafs24.entity.ChecklistElement;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistElementGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistElementPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.ChecklistService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/roadtrips")
public class ChecklistController{
    private final ChecklistService checklistService;

    ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
        }

    @GetMapping("/{roadtripId}/checklist")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChecklistGetDTO getChecklist(@PathVariable Long roadtripId) {
        // Retrieve the checklist for the given roadtripId
        Checklist checklist = checklistService.getChecklistByRoadtripId(roadtripId);

        // Convert to API representation
        return DTOMapper.INSTANCE.convertEntityToChecklistGetDTO(checklist);
    }
    
    //Post a new checklist element
    @PostMapping("/{roadtripId}/checklist")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ChecklistElementGetDTO addChecklistElement(@PathVariable Long roadtripId, @RequestBody ChecklistElementPostDTO checklistElementPostDTO) {

        // Add checklist element to the existing checklist
        ChecklistElement element = DTOMapper.INSTANCE.convertChecklistElementPostDTOToEntity(checklistElementPostDTO);
        ChecklistElement createdElement = checklistService.addChecklistElement(roadtripId, element);

        // Convert to API representation
        return DTOMapper.INSTANCE.convertEntityToChecklistElementGetDTO(createdElement);
    }

    //Update a checklist element
}