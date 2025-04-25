package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;

@Entity
@Table(name = "point_of_interest")
public class PointOfInterest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long poiId;

    @ManyToOne
    @JoinColumn(name = "roadtrip_id")
    private Roadtrip roadtrip;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point coordinate;

    @Column
    private String description;

    @Column
    private PoiCategory category;

    @Column
    private PoiPriority priority;

    @Column(nullable = false)
    private Long creatorId;

    @Column
    private AcceptanceStatus status;

    @Column
    private Integer eligibleVoteCount;

    @Column
    private List<Long> upvotes = new ArrayList<>();

    @Column
    private List<Long> downvotes = new ArrayList<>();

    @OneToMany(mappedBy = "poi", 
              cascade = CascadeType.ALL, 
              fetch = FetchType.EAGER,  // Change from LAZY to EAGER
              orphanRemoval = true)
    private List<PointOfInterestComment> comments = new ArrayList<>();

    // Getters and Setters
    public Long getPoiId() {
        return poiId;
    }

    public void setPoiId(Long poiId) {
        this.poiId = poiId;
    }

    public Roadtrip getRoadtrip() {
        return roadtrip;
    }

    public void setRoadtrip(Roadtrip roadtrip) {
        this.roadtrip = roadtrip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PoiCategory getCategory() {
        return category;
    }

    public void setCategory(PoiCategory category) {
        this.category = category;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public AcceptanceStatus getStatus() {
        return status;
    }

    public void setStatus(AcceptanceStatus status) {
        this.status = status;
    }

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

    public List<Long> getUpvotes() {
        return upvotes;
    }

    // Setter for upvotes
    public void setUpvotes(List<Long> upvotes) {
        this.upvotes = upvotes;
    }

    // Getter for downvotes
    public List<Long> getDownvotes() {
        return downvotes;
    }

    // Setter for downvotes
    public void setDownvotes(List<Long> downvotes) {
        this.downvotes = downvotes;
    }

    public List<PointOfInterestComment> getPointOfInterestComment() {
        return comments;
    }

    public void setPointOfInterestComments(List<PointOfInterestComment> comments) {
        this.comments = comments;
    }

    public void addComment(PointOfInterestComment comment) {
        this.comments.add(comment);
        comment.setPoi(this);
    }

    public void removeComment(PointOfInterestComment comment) {
        this.comments.remove(comment);
        comment.setPoi(null);
    }

}
