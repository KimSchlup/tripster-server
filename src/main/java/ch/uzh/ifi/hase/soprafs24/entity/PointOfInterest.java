package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.locationtech.jts.geom.Point;
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

}
