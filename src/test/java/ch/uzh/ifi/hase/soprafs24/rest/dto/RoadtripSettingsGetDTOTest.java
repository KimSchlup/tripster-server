package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.BasemapType;
import ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RoadtripSettingsGetDTOTest {

    private RoadtripSettingsGetDTO roadtripSettingsGetDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        roadtripSettingsGetDTO = new RoadtripSettingsGetDTO();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSetAndGetRoadtripSettingsId() {
        // Arrange
        Long roadtripSettingsId = 1L;

        // Act
        roadtripSettingsGetDTO.setRoadtripSettingsId(roadtripSettingsId);

        // Assert
        assertEquals(roadtripSettingsId, roadtripSettingsGetDTO.getRoadtripSettingsId());
    }

    @Test
    public void testSetAndGetRoadtripId() {
        // Arrange
        Long roadtripId = 2L;

        // Act
        roadtripSettingsGetDTO.setRoadtripId(roadtripId);

        // Assert
        assertEquals(roadtripId, roadtripSettingsGetDTO.getRoadtripId());
    }

    @Test
    public void testSetAndGetBasemapType() {
        // Arrange
        BasemapType basemapType = BasemapType.SATELLITE;

        // Act
        roadtripSettingsGetDTO.setBasemapType(basemapType);

        // Assert
        assertEquals(basemapType, roadtripSettingsGetDTO.getBasemapType());
    }

    @Test
    public void testSetAndGetDecisionProcess() {
        // Arrange
        DecisionProcess decisionProcess = DecisionProcess.MAJORITY;

        // Act
        roadtripSettingsGetDTO.setDecisionProcess(decisionProcess);

        // Assert
        assertEquals(decisionProcess, roadtripSettingsGetDTO.getDecisionProcess());
    }

    @Test
    public void testSetAndGetBoundingBox() throws Exception {
        // Arrange
        ObjectNode boundingBox = objectMapper.createObjectNode();
        boundingBox.put("type", "Polygon");
        boundingBox.putArray("coordinates")
                .addArray()
                .add(objectMapper.createArrayNode().add(0).add(0))
                .add(objectMapper.createArrayNode().add(0).add(1))
                .add(objectMapper.createArrayNode().add(1).add(1))
                .add(objectMapper.createArrayNode().add(1).add(0))
                .add(objectMapper.createArrayNode().add(0).add(0));

        // Act
        roadtripSettingsGetDTO.setBoundingBox(boundingBox);

        // Assert
        assertEquals(boundingBox, roadtripSettingsGetDTO.getBoundingBox());
        assertEquals("Polygon", roadtripSettingsGetDTO.getBoundingBox().get("type").asText());
    }

    @Test
    public void testSetAndGetStartDate() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 1, 1);

        // Act
        roadtripSettingsGetDTO.setStartDate(startDate);

        // Assert
        assertEquals(startDate, roadtripSettingsGetDTO.getStartDate());
    }

    @Test
    public void testSetAndGetEndDate() {
        // Arrange
        LocalDate endDate = LocalDate.of(2025, 1, 10);

        // Act
        roadtripSettingsGetDTO.setEndDate(endDate);

        // Assert
        assertEquals(endDate, roadtripSettingsGetDTO.getEndDate());
    }

    @Test
    public void testAllBasemapTypeValues() {
        // Act & Assert
        for (BasemapType basemapType : BasemapType.values()) {
            roadtripSettingsGetDTO.setBasemapType(basemapType);
            assertEquals(basemapType, roadtripSettingsGetDTO.getBasemapType());
        }
    }

    @Test
    public void testAllDecisionProcessValues() {
        // Act & Assert
        for (DecisionProcess decisionProcess : DecisionProcess.values()) {
            roadtripSettingsGetDTO.setDecisionProcess(decisionProcess);
            assertEquals(decisionProcess, roadtripSettingsGetDTO.getDecisionProcess());
        }
    }

    @Test
    public void testNullValues() {
        // Act
        roadtripSettingsGetDTO.setRoadtripSettingsId(null);
        roadtripSettingsGetDTO.setRoadtripId(null);
        roadtripSettingsGetDTO.setBasemapType(null);
        roadtripSettingsGetDTO.setDecisionProcess(null);
        roadtripSettingsGetDTO.setBoundingBox(null);
        roadtripSettingsGetDTO.setStartDate(null);
        roadtripSettingsGetDTO.setEndDate(null);

        // Assert
        assertNull(roadtripSettingsGetDTO.getRoadtripSettingsId());
        assertNull(roadtripSettingsGetDTO.getRoadtripId());
        assertNull(roadtripSettingsGetDTO.getBasemapType());
        assertNull(roadtripSettingsGetDTO.getDecisionProcess());
        assertNull(roadtripSettingsGetDTO.getBoundingBox());
        assertNull(roadtripSettingsGetDTO.getStartDate());
        assertNull(roadtripSettingsGetDTO.getEndDate());
    }
}
