package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.ChecklistCategory;
import ch.uzh.ifi.hase.soprafs24.constant.Priority;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChecklistElementPostDTOTest {

    @Test
    public void testSetAndGetName() {
        // Arrange
        ChecklistElementPostDTO checklistElementPostDTO = new ChecklistElementPostDTO();
        String name = "Test Checklist Element";

        // Act
        checklistElementPostDTO.setName(name);

        // Assert
        assertEquals(name, checklistElementPostDTO.getName());
    }

    @Test
    public void testSetAndGetIsCompleted() {
        // Arrange
        ChecklistElementPostDTO checklistElementPostDTO = new ChecklistElementPostDTO();
        Boolean isCompleted = true;

        // Act
        checklistElementPostDTO.setIsCompleted(isCompleted);

        // Assert
        assertEquals(isCompleted, checklistElementPostDTO.getIsCompleted());
    }

    @Test
    public void testSetAndGetAssignedUser() {
        // Arrange
        ChecklistElementPostDTO checklistElementPostDTO = new ChecklistElementPostDTO();
        String assignedUser = "testUser";

        // Act
        checklistElementPostDTO.setAssignedUser(assignedUser);

        // Assert
        assertEquals(assignedUser, checklistElementPostDTO.getAssignedUser());
    }

    @Test
    public void testSetAndGetPriority() {
        // Arrange
        ChecklistElementPostDTO checklistElementPostDTO = new ChecklistElementPostDTO();
        Priority priority = Priority.HIGH;

        // Act
        checklistElementPostDTO.setPriority(priority);

        // Assert
        assertEquals(priority, checklistElementPostDTO.getPriority());
    }

    @Test
    public void testSetAndGetCategory() {
        // Arrange
        ChecklistElementPostDTO checklistElementPostDTO = new ChecklistElementPostDTO();
        ChecklistCategory category = ChecklistCategory.TASK;

        // Act
        checklistElementPostDTO.setCategory(category);

        // Assert
        assertEquals(category, checklistElementPostDTO.getCategory());
    }

    @Test
    public void testAllPriorityValues() {
        // Arrange
        ChecklistElementPostDTO checklistElementPostDTO = new ChecklistElementPostDTO();

        // Act & Assert
        for (Priority priority : Priority.values()) {
            checklistElementPostDTO.setPriority(priority);
            assertEquals(priority, checklistElementPostDTO.getPriority());
        }
    }

    @Test
    public void testAllCategoryValues() {
        // Arrange
        ChecklistElementPostDTO checklistElementPostDTO = new ChecklistElementPostDTO();

        // Act & Assert
        for (ChecklistCategory category : ChecklistCategory.values()) {
            checklistElementPostDTO.setCategory(category);
            assertEquals(category, checklistElementPostDTO.getCategory());
        }
    }
}
