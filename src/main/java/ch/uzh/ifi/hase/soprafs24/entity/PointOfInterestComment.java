package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "pointOfInterestComment")
public class PointOfInterestComment implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long poiId;
  @Id
  private Long authorId;
  @Id
  private LocalDate creationDate;
  @Column
  private String comment;

  public void setPoiId(Long poiId){
    this.poiId = poiId;
  }
  public Long getPoiId(){
    return this.poiId;
  }
  public void setAuthorId(Long authosId){
    this.authorId = authosId;
  }
  public Long getAuthorId(){
    return this.authorId;
  }
  public void setCreationDate(LocalDate creationDate){
    this.creationDate = creationDate;
  }
  public LocalDate getCreationDate(){
    return this.creationDate;
  }
  public void setComment(String comment){
    this.comment = comment;
  }
  public String getComment(){
    return comment;
  }

}
