package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "userPreferences")
public class UserPreferences implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  // @GeneratedValue
  private Long userId;
  @Column
  private String distanceMetric;
  @Column
  private String temperatureMetric;

  public Long getId() {
    return userId;
  }
  public void setId(Long userId) {
    this.userId = userId;
  }
  public String getDistanceMetric(){
    return distanceMetric;
  }
  public void setDistanceMetric(String distanceMetric){
    this.distanceMetric = distanceMetric;
  }
  public String getTemperatureMetric(){
    return temperatureMetric;
  }
  public void setTemperatureMetric(String temperatureMetric){
    this.temperatureMetric = temperatureMetric;
  }
}
