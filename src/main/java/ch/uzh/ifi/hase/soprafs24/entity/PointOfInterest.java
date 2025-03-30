package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

import org.locationtech.jts.geom.Point;

import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;
import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "pointOfInterest")
public class PointOfInterest implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long poiId;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  @JdbcTypeCode(SqlTypes.GEOMETRY)
  private Point coordinate;
  @Column
  private String description;
  @Column
  private PoiCategory category;
  @Column
  private PoiPriority priority;
  @Column
  private Long creatorId;
  @Column
  private AcceptanceStatus status;
  @Column
  private ArrayList<TripProjectMember> upvotes;
  @Column
  private ArrayList<TripProjectMember> downvotes;
  @Column
  private Integer eligibleVoteCount;


  public void setPoiId(Long poiId){
    this.poiId = poiId;
  }
  public Long getPoiId(){
    return this.poiId;
  }
  public void setName(String name){
    this.name = name;
  }
  public String getName(){
    return this.name;
  }
  public void setCoordinate(Point coordinate){
    this.coordinate = coordinate;
  }
  public Point getCoordinate(){
    return this.coordinate;
  }
  public void setDescription(String description){
    this.description = description;
  }
  public String getDescription(){
    return this.description;
  }
  public void setCategory(PoiCategory category){
    this.category = category;
  }
  public PoiCategory getCategory(){
    return this.category;
  }
  public void setPriority(PoiPriority priority){
    this.priority = priority;
  }
  public PoiPriority getPriority(){
    return this.priority;
  }
  public void setCreatorId(Long creatorId){
    this.creatorId = creatorId;
  }
  public Long getCreatorId(){
    return this.creatorId;
  }
  public void setStatus(AcceptanceStatus status){
    this.status = status;
  }
  public AcceptanceStatus getStatus(){
    return this.status;
  }
  public void setUpvote(TripProjectMember upvote){
    if(this.upvotes == null){
      this.upvotes = new ArrayList<TripProjectMember>();
    }
    this.upvotes.add(upvote);
  }
  public ArrayList<TripProjectMember> getUpvotes(){
    return this.upvotes;
  }
  public void setDownvote(TripProjectMember downvote){
    if(this.downvotes == null){
      this.downvotes = new ArrayList<TripProjectMember>();
    }
    this.downvotes.add(downvote);
  }
  public ArrayList<TripProjectMember> getDownvotes(){
    return this.downvotes;
  }
  public void setEligibleVoteCount(Integer count){
    this.eligibleVoteCount = count;
  }
  public Integer getEligibleVoteCount(){
    return this.eligibleVoteCount;
  }

}