package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import org.locationtech.jts.geom.Polygon;

import ch.uzh.ifi.hase.soprafs24.constant.BasemapType;
import ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess;

@Entity
@Table(name = "roadtrip_settings")
public class RoadtripSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long roadtripSettingsId;

    @OneToOne
    @JoinColumn(name = "roadtrip_id")
    private Roadtrip roadtrip;

    @Column
    private BasemapType basemapType;

    @Column
    private DecisionProcess decisionProcess;

    @Column(columnDefinition = "geometry(Polygon, 4326)")
    private Polygon boundingBox;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    public Long getRoadtripSettingsId() {
        return roadtripSettingsId;
    }

    public void setRoadtripSettingsId(Long roadtripSettingsId) {
        this.roadtripSettingsId = roadtripSettingsId;
    }

    public Roadtrip getRoadtrip() {
        return roadtrip;
    }

    public void setRoadtrip(Roadtrip roadtrip) {
        this.roadtrip = roadtrip;
    }

    public BasemapType getBasemapType() {
        return basemapType;
    }

    public void setBasemapType(BasemapType basemapType) {
        this.basemapType = basemapType;
    }

    public DecisionProcess getDecisionProcess() {
        return decisionProcess;
    }

    public void setDecisionProcess(DecisionProcess decisionProcess) {
        this.decisionProcess = decisionProcess;
    }

    public Polygon getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Polygon boundingBox) {
        this.boundingBox = boundingBox;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}
