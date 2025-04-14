package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class ChecklistGetDTO {
    private Long roadtripId;
    private List<ChecklistElementGetDTO> checklistElements;

    // Getters and Setters
    public Long getRoadtripId() {
        return roadtripId;
    }

    public void setRoadtripId(Long roadtripId) {
        this.roadtripId = roadtripId;
    }

    public List<ChecklistElementGetDTO> getChecklistElements() {
        return checklistElements;
    }

    public void setChecklistElements(List<ChecklistElementGetDTO> checklistElements) {
        this.checklistElements = checklistElements;
    }
}
