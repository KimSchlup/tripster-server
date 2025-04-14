package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;

import ch.uzh.ifi.hase.soprafs24.constant.BasemapType;
import ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess;

public class RoadtripSettingsGetDTO {
    private Long roadtripSettingsId;
    private Long roadtripId;
    private BasemapType basemapType;
    private DecisionProcess decisionProcess;
    private JsonNode boundingBox;
    private LocalDate startDate;
    private LocalDate endDate;

    public Long getRoadtripSettingsId() {
        return roadtripSettingsId;
    }

    public void setRoadtripSettingsId(Long roadtripSettingsId) {
        this.roadtripSettingsId = roadtripSettingsId;
    }

    public Long getRoadtripId() {
        return roadtripId;
    }

    public void setRoadtripId(Long roadtripId) {
        this.roadtripId = roadtripId;
    }

    public BasemapType getBasemapType() {
        return basemapType;
    }

    public void setBasemapType(BasemapType basemapType) {
        this.basemapType = basemapType;
    }

    public DecisionProcess getDecisionProcess() {
        return decisionProcess;
    }

    public void setDecisionProcess(DecisionProcess decisionProcess) {
        this.decisionProcess = decisionProcess;
    }

    public JsonNode getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(JsonNode boundingBox) {
        this.boundingBox = boundingBox;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
