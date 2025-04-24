package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PointOfInterestCommentPostDTOTest {

    private PointOfInterestCommentPostDTO commentPostDTO;

    @BeforeEach
    public void setup() {
        commentPostDTO = new PointOfInterestCommentPostDTO();
    }

    @Test
    public void testSetAndGetComment() {
        // Arrange
        String comment = "This is a test comment";

        // Act
        commentPostDTO.setComment(comment);

        // Assert
        assertEquals(comment, commentPostDTO.getComment());
    }

    @Test
    public void testSetAndGetEmptyComment() {
        // Arrange
        String comment = "";

        // Act
        commentPostDTO.setComment(comment);

        // Assert
        assertEquals(comment, commentPostDTO.getComment());
    }

    @Test
    public void testSetAndGetNullComment() {
        // Act
        commentPostDTO.setComment(null);

        // Assert
        assertNull(commentPostDTO.getComment());
    }

    @Test
    public void testSetAndGetLongComment() {
        // Arrange
        StringBuilder longCommentBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longCommentBuilder.append("a");
        }
        String longComment = longCommentBuilder.toString();

        // Act
        commentPostDTO.setComment(longComment);

        // Assert
        assertEquals(longComment, commentPostDTO.getComment());
        assertEquals(1000, commentPostDTO.getComment().length());
    }

    @Test
    public void testSetAndGetSpecialCharactersComment() {
        // Arrange
        String specialComment = "!@#$%^&*()_+{}|:\"<>?[];',./`~";

        // Act
        commentPostDTO.setComment(specialComment);

        // Assert
        assertEquals(specialComment, commentPostDTO.getComment());
    }
}
