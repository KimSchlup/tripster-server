package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.Priority;
import ch.uzh.ifi.hase.soprafs24.constant.ChecklistCategory;

public class ChecklistElementGetDTO {
    private Long checklistElementId;
    private String name;
    private Boolean isCompleted;
    private Long assignedUserId; // Assuming you only send the ID
    private Priority priority;
    private ChecklistCategory category;

    // Getters and Setters
    public Long getChecklistElementId() {
        return checklistElementId;
    }

    public void setChecklistElementId(Long checklistElementId) {
        this.checklistElementId = checklistElementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public ChecklistCategory getCategory() {
        return category;
    }

    public void setCategory(ChecklistCategory category) {
        this.category = category;
    }
}

