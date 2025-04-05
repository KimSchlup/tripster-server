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
@Table(name = "user_preferences")
public class UserPreferences implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column
  private String distanceMetric;
  @Column
  private String temperatureMetric;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getDistanceMetric() {
    return distanceMetric;
  }

  public void setDistanceMetric(String distanceMetric) {
    this.distanceMetric = distanceMetric;
  }

  public String getTemperatureMetric() {
    return temperatureMetric;
  }

  public void setTemperatureMetric(String temperatureMetric) {
    this.temperatureMetric = temperatureMetric;
  }
}
