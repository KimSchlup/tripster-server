package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class ChecklistPostDTO {
    private List<ChecklistElementPostDTO> checklistElements;

    // Getters and Setters
    public List<ChecklistElementPostDTO> getChecklistElements() {
        return checklistElements;
    }

    public void setChecklistElements(List<ChecklistElementPostDTO> checklistElements) {
        this.checklistElements = checklistElements;
    }
}
