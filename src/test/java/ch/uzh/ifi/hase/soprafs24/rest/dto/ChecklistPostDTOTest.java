package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.ChecklistCategory;
import ch.uzh.ifi.hase.soprafs24.constant.Priority;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChecklistPostDTOTest {

    @Test
    public void testSetAndGetChecklistElements() {
        // Arrange
        ChecklistPostDTO checklistPostDTO = new ChecklistPostDTO();
        List<ChecklistElementPostDTO> checklistElements = new ArrayList<>();

        ChecklistElementPostDTO element1 = new ChecklistElementPostDTO();
        element1.setName("Element 1");
        element1.setIsCompleted(false);
        element1.setAssignedUser("user1");
        element1.setPriority(Priority.HIGH);
        element1.setCategory(ChecklistCategory.TASK);

        ChecklistElementPostDTO element2 = new ChecklistElementPostDTO();
        element2.setName("Element 2");
        element2.setIsCompleted(true);
        element2.setAssignedUser("user2");
        element2.setPriority(Priority.MEDIUM);
        element2.setCategory(ChecklistCategory.ITEM);

        checklistElements.add(element1);
        checklistElements.add(element2);

        // Act
        checklistPostDTO.setChecklistElements(checklistElements);

        // Assert
        assertEquals(checklistElements, checklistPostDTO.getChecklistElements());
        assertEquals(2, checklistPostDTO.getChecklistElements().size());

        // Verify the elements are correctly stored
        ChecklistElementPostDTO retrievedElement1 = checklistPostDTO.getChecklistElements().get(0);
        assertEquals("Element 1", retrievedElement1.getName());
        assertEquals(false, retrievedElement1.getIsCompleted());
        assertEquals("user1", retrievedElement1.getAssignedUser());
        assertEquals(Priority.HIGH, retrievedElement1.getPriority());
        assertEquals(ChecklistCategory.TASK, retrievedElement1.getCategory());

        ChecklistElementPostDTO retrievedElement2 = checklistPostDTO.getChecklistElements().get(1);
        assertEquals("Element 2", retrievedElement2.getName());
        assertEquals(true, retrievedElement2.getIsCompleted());
        assertEquals("user2", retrievedElement2.getAssignedUser());
        assertEquals(Priority.MEDIUM, retrievedElement2.getPriority());
        assertEquals(ChecklistCategory.ITEM, retrievedElement2.getCategory());
    }

    @Test
    public void testSetAndGetEmptyChecklistElements() {
        // Arrange
        ChecklistPostDTO checklistPostDTO = new ChecklistPostDTO();
        List<ChecklistElementPostDTO> checklistElements = new ArrayList<>();

        // Act
        checklistPostDTO.setChecklistElements(checklistElements);

        // Assert
        assertNotNull(checklistPostDTO.getChecklistElements());
        assertEquals(0, checklistPostDTO.getChecklistElements().size());
    }

    @Test
    public void testSetAndGetNullChecklistElements() {
        // Arrange
        ChecklistPostDTO checklistPostDTO = new ChecklistPostDTO();

        // Act
        checklistPostDTO.setChecklistElements(null);

        // Assert
        assertNull(checklistPostDTO.getChecklistElements());
    }

    @Test
    public void testModifyChecklistElements() {
        // Arrange
        ChecklistPostDTO checklistPostDTO = new ChecklistPostDTO();
        List<ChecklistElementPostDTO> checklistElements = new ArrayList<>();

        ChecklistElementPostDTO element = new ChecklistElementPostDTO();
        element.setName("Original Name");
        checklistElements.add(element);

        checklistPostDTO.setChecklistElements(checklistElements);

        // Act
        checklistPostDTO.getChecklistElements().get(0).setName("Modified Name");

        // Assert
        assertEquals("Modified Name", checklistPostDTO.getChecklistElements().get(0).getName());
    }

    @Test
    public void testAddChecklistElement() {
        // Arrange
        ChecklistPostDTO checklistPostDTO = new ChecklistPostDTO();
        List<ChecklistElementPostDTO> checklistElements = new ArrayList<>();
        checklistPostDTO.setChecklistElements(checklistElements);

        ChecklistElementPostDTO newElement = new ChecklistElementPostDTO();
        newElement.setName("New Element");

        // Act
        checklistPostDTO.getChecklistElements().add(newElement);

        // Assert
        assertEquals(1, checklistPostDTO.getChecklistElements().size());
        assertEquals("New Element", checklistPostDTO.getChecklistElements().get(0).getName());
    }
}
