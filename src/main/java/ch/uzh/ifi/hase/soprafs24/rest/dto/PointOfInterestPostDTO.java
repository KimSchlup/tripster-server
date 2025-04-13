package ch.uzh.ifi.hase.soprafs24.rest.dto;

import org.locationtech.jts.geom.Point;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;

public class PointOfInterestPostDTO {

    private String name;
    private Point coordinate;
    private String description;
    private PoiCategory category;
    private Long creatorId;
    private AcceptanceStatus status;
    private Integer eligibleVoteCount;
    private PoiPriority priority;

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for coordinate
    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }

    // Getter and Setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and Setter for category
    public PoiCategory getCategory() {
        return category;
    }

    public void setCategory(PoiCategory category) {
        this.category = category;
    }

    // Getter and Setter for creatorId
    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    // Getter and Setter for status
    public AcceptanceStatus getStatus() {
        return status;
    }

    public void setStatus(AcceptanceStatus status) {
        this.status = status;
    }

    // Getter and Setter for eligibleVoteCount
    public Integer getEligibleVoteCount() {
        return eligibleVoteCount;
    }

    public void setEligibleVoteCount(Integer eligibleVoteCount) {
        this.eligibleVoteCount = eligibleVoteCount;
    }
    
    public PoiPriority getPriority() {
        return priority;
    }

    public void setPriority(PoiPriority priority) {
        this.priority = priority;
    }

}
