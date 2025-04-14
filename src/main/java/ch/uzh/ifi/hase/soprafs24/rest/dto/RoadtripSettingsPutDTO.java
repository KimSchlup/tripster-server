package ch.uzh.ifi.hase.soprafs24.rest.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class RoadtripSettingsPutDTO {
    private String basemapType;
    private String decisionProcess;
    private JsonNode boundingBox;
    private String startDate;
    private String endDate;

    public String getBasemapType() {
        return basemapType;
    }

    public void setBasemapType(String basemapType) {
        this.basemapType = basemapType;
    }

    public String getDecisionProcess() {
        return decisionProcess;
    }

    public void setDecisionProcess(String decisionProcess) {
        this.decisionProcess = decisionProcess;
    }

    public JsonNode getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(JsonNode boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
