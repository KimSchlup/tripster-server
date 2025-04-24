package ch.uzh.ifi.hase.soprafs24.rest.dto;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class PointOfInterestGetDTOTest {

    private PointOfInterestGetDTO pointOfInterestGetDTO;

    @BeforeEach
    public void setup() {
        pointOfInterestGetDTO = new PointOfInterestGetDTO();
    }

    @Test
    public void testPoiIdGetterSetter() {
        Long poiId = 123L;

        pointOfInterestGetDTO.setPoiId(poiId);

        assertEquals(poiId, pointOfInterestGetDTO.getPoiId());
    }

    @Test
    public void testNameGetterSetter() {
        String name = "Test POI";

        pointOfInterestGetDTO.setName(name);

        assertEquals(name, pointOfInterestGetDTO.getName());
    }

    @Test
    public void testCoordinateGetterSetter() {
        JsonNode coordinate = JsonNodeFactory.instance.objectNode().put("longitude", 8.681495).put("latitude", 49.41461);

        pointOfInterestGetDTO.setCoordinate(coordinate);

        assertEquals(coordinate, pointOfInterestGetDTO.getCoordinate());
    }

    @Test
    public void testDescriptionGetterSetter() {
        String description = "A point of interest";

        pointOfInterestGetDTO.setDescription(description);

        assertEquals(description, pointOfInterestGetDTO.getDescription());
    }

    @Test
    public void testCategoryGetterSetter() {
        PoiCategory category = PoiCategory.SIGHTSEEING;

        pointOfInterestGetDTO.setCategory(category);

        assertEquals(category, pointOfInterestGetDTO.getCategory());
    }

    @Test
    public void testCreatorIdGetterSetter() {
        Long creatorId = 456L;

        pointOfInterestGetDTO.setCreatorId(creatorId);

        assertEquals(creatorId, pointOfInterestGetDTO.getCreatorId());
    }

    @Test
    public void testStatusGetterSetter() {
        AcceptanceStatus status = AcceptanceStatus.ACCEPTED;

        pointOfInterestGetDTO.setStatus(status);

        assertEquals(status, pointOfInterestGetDTO.getStatus());
    }

    @Test
    public void testEligibleVoteCountGetterSetter() {
        Integer eligibleVoteCount = 10;

        pointOfInterestGetDTO.setEligibleVoteCount(eligibleVoteCount);

        assertEquals(eligibleVoteCount, pointOfInterestGetDTO.getEligibleVoteCount());
    }

    @Test
    public void testPriorityGetterSetter() {
        PoiPriority priority = PoiPriority.HIGH;

        pointOfInterestGetDTO.setPriority(priority);

        assertEquals(priority, pointOfInterestGetDTO.getPriority());
    }

    @Test
    public void testUpvotesGetterSetter() {
        ArrayList<Long> upvotes = new ArrayList<>();
        upvotes.add(1L);
        upvotes.add(2L);

        pointOfInterestGetDTO.setUpvotes(upvotes);

        assertEquals(upvotes, pointOfInterestGetDTO.getUpvotes());
    }

    @Test
    public void testDownvotesGetterSetter() {
        ArrayList<Long> downvotes = new ArrayList<>();
        downvotes.add(3L);
        downvotes.add(4L);

        pointOfInterestGetDTO.setDownvotes(downvotes);

        assertEquals(downvotes, pointOfInterestGetDTO.getDownvotes());
    }
}
