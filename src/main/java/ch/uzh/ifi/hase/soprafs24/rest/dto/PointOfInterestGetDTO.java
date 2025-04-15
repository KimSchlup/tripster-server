package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;

public class PointOfInterestGetDTO {
    
    private Long poiId;
    private String name;
    private JsonNode coordinate;
    private String description;
    private PoiCategory category;
    private Long creatorId;
    private AcceptanceStatus status;
    private Integer eligibleVoteCount;
    private PoiPriority priority;
    private ArrayList<Long> upvotes;
    private ArrayList<Long> downvotes;


    // Getter and Setter for poiId
    public Long getPoiId(){
        return poiId;
    }

    public void setPoiId(Long poiId){
        this.poiId = poiId;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for coordinate
    public JsonNode getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(JsonNode coordinate) {
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

    public ArrayList<Long> getUpvotes() {
        return upvotes;
    }

    // Setter for upvotes
    public void setUpvotes(ArrayList<Long> upvotes) {
        this.upvotes = upvotes;
    }

    // Getter for downvotes
    public ArrayList<Long> getDownvotes() {
        return downvotes;
    }

    // Setter for downvotes
    public void setDownvotes(ArrayList<Long> downvotes) {
        this.downvotes = downvotes;
    } 

}
