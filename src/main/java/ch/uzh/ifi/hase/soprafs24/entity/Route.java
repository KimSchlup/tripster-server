package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

import org.locationtech.jts.geom.Point;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;
import ch.uzh.ifi.hase.soprafs24.constant.PoiStatus;
import ch.uzh.ifi.hase.soprafs24.constant.TravelMode;

import org.locationtech.jts.geom.LineString;
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
@Table(name = "route")
public class Route implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long startPoiId;

  @Id
  private Long endPoiId;

  @Column(nullable = false)
  @JdbcTypeCode(SqlTypes.GEOMETRY)
  private LineString geometry;

  @Column
  private Float distance;

  @Column
  private Float travelTime;

  @Column
  private TravelMode travelMode;

  @Column
  private AcceptanceStatus status;

  public void setStartPoiId(Long startPoiId){
    this.startPoiId = startPoiId;
  }
  public Long getStartPoiId(){
    return this.startPoiId;
  }
  public void setEndPoiId(Long endPoiId){
    this.endPoiId = endPoiId;
  }
  public Long getEndPoiId(){
    return this.endPoiId;
  }
  public void setGeometry(LineString geometry){
    this.geometry = geometry;
  }
  public LineString getGeometry(){
    return this.geometry;
  }
  public void setDistance(Float distance){
    this.distance = distance;
  }
  public Float getDistance(){
    return this.distance;
  }
  public void setTravelTime(Float travelTime){
    this.travelTime = travelTime;
  }
  public Float getTravelTime(){
    return this.travelTime;
  }
  public void getTravelMode(TravelMode travelMode){
    this.travelMode = travelMode;
  }
  public TravelMode getTravelMode(){
    return this.travelMode;
  }
  public void setAcceptanceStatus(AcceptanceStatus status){
    this.status = status;
  }
  public AcceptanceStatus getAcceptanceStatus(){
    return this.status;
  }

}