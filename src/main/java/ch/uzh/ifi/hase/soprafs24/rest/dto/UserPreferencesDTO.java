package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserPreferencesDTO {
  
  private Long userId;
  private String distanceMetric;
  private String temperatureMetric;

  public Long getUserId() {
    return userId;
  }
  public void setUserId(Long userId) {
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
