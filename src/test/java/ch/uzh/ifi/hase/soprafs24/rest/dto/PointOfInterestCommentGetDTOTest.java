package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PointOfInterestCommentGetDTOTest {

    private PointOfInterestCommentGetDTO commentGetDTO;
    private PointOfInterest pointOfInterest;
    private GeometryFactory geometryFactory;

    @BeforeEach
    public void setup() {
        commentGetDTO = new PointOfInterestCommentGetDTO();
        geometryFactory = new GeometryFactory();

        // Create a point of interest for testing
        pointOfInterest = new PointOfInterest();
        pointOfInterest.setPoiId(1L);
        pointOfInterest.setName("Test POI");
        Point point = geometryFactory.createPoint(new Coordinate(10.0, 20.0));
        pointOfInterest.setCoordinate(point);
        pointOfInterest.setCreatorId(2L);

        Roadtrip roadtrip = new Roadtrip();
        roadtrip.setRoadtripId(3L);
        pointOfInterest.setRoadtrip(roadtrip);
    }

    @Test
    public void testSetAndGetCommentId() {
        // Arrange
        Long commentId = 1L;

        // Act
        commentGetDTO.setCommentId(commentId);

        // Assert
        assertEquals(commentId, commentGetDTO.getCommentId());
    }

    @Test
    public void testSetAndGetPoi() {
        // Act
        commentGetDTO.setPoi(pointOfInterest);

        // Assert
        assertEquals(pointOfInterest, commentGetDTO.getPoi());
        assertEquals(1L, commentGetDTO.getPoi().getPoiId());
        assertEquals("Test POI", commentGetDTO.getPoi().getName());
    }

    @Test
    public void testSetAndGetAuthorId() {
        // Arrange
        Long authorId = 5L;

        // Act
        commentGetDTO.setAuthorId(authorId);

        // Assert
        assertEquals(authorId, commentGetDTO.getAuthorId());
    }

    @Test
    public void testSetAndGetComment() {
        // Arrange
        String comment = "This is a test comment";

        // Act
        commentGetDTO.setComment(comment);

        // Assert
        assertEquals(comment, commentGetDTO.getComment());
    }

    @Test
    public void testSetAndGetCreationDate() {
        // Arrange
        LocalDate creationDate = LocalDate.of(2025, 1, 1);

        // Act
        commentGetDTO.setCreationDate(creationDate);

        // Assert
        assertEquals(creationDate, commentGetDTO.getCreationDate());
    }

    @Test
    public void testNullValues() {
        // Act
        commentGetDTO.setCommentId(null);
        commentGetDTO.setPoi(null);
        commentGetDTO.setAuthorId(null);
        commentGetDTO.setComment(null);
        commentGetDTO.setCreationDate(null);

        // Assert
        assertNull(commentGetDTO.getCommentId());
        assertNull(commentGetDTO.getPoi());
        assertNull(commentGetDTO.getAuthorId());
        assertNull(commentGetDTO.getComment());
        assertNull(commentGetDTO.getCreationDate());
    }
}
