package ch.uzh.ifi.hase.soprafs24.rest.dto;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PointOfInterestPostDTOTest {

    private PointOfInterestPostDTO pointOfInterestPostDTO;

    @BeforeEach
    public void setup() {
        pointOfInterestPostDTO = new PointOfInterestPostDTO();
    }

    @Test
    public void testNameGetterSetter() {
        String name = "New POI";

        pointOfInterestPostDTO.setName(name);

        assertEquals(name, pointOfInterestPostDTO.getName());
    }

    @Test
    public void testCoordinateGetterSetter() {
        JsonNode coordinate = JsonNodeFactory.instance.objectNode().put("longitude", 8.681495).put("latitude", 49.41461);

        pointOfInterestPostDTO.setCoordinate(coordinate);

        assertEquals(coordinate, pointOfInterestPostDTO.getCoordinate());
    }

    @Test
    public void testDescriptionGetterSetter() {
        String description = "A beautiful place to visit";

        pointOfInterestPostDTO.setDescription(description);

        assertEquals(description, pointOfInterestPostDTO.getDescription());
    }

    @Test
    public void testCategoryGetterSetter() {
        PoiCategory category = PoiCategory.SIGHTSEEING;

        pointOfInterestPostDTO.setCategory(category);

        assertEquals(category, pointOfInterestPostDTO.getCategory());
    }

    @Test
    public void testCreatorIdGetterSetter() {
        Long creatorId = 789L;

        pointOfInterestPostDTO.setCreatorId(creatorId);

        assertEquals(creatorId, pointOfInterestPostDTO.getCreatorId());
    }

    @Test
    public void testStatusGetterSetter() {
        AcceptanceStatus status = AcceptanceStatus.PENDING;

        pointOfInterestPostDTO.setStatus(status);

        assertEquals(status, pointOfInterestPostDTO.getStatus());
    }

    @Test
    public void testEligibleVoteCountGetterSetter() {
        Integer eligibleVoteCount = 15;

        pointOfInterestPostDTO.setEligibleVoteCount(eligibleVoteCount);

        assertEquals(eligibleVoteCount, pointOfInterestPostDTO.getEligibleVoteCount());
    }

    @Test
    public void testPriorityGetterSetter() {
        PoiPriority priority = PoiPriority.MEDIUM;

        pointOfInterestPostDTO.setPriority(priority);

        assertEquals(priority, pointOfInterestPostDTO.getPriority());
    }
}

