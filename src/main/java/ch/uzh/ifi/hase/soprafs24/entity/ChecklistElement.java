package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Locale.Category;

import ch.uzh.ifi.hase.soprafs24.constant.Priority;
import ch.uzh.ifi.hase.soprafs24.constant.ChecklistCategory;

@Entity
@Table(name = "checklist_element")
public class ChecklistElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checklistElementId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private Boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id", nullable = true)
    private User assignedUser;

    @Column(nullable = false)
    private Priority priority;

    @Column(nullable = false)
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

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
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
