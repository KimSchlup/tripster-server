package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;
import org.locationtech.jts.geom.LineString;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.TravelMode;

@Entity
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id", nullable = false, unique = true)
    private Long routeId;

    @Column(nullable = false)
    private Long startId; // ID of the starting PointOfInterest

    @Column(nullable = false)
    private Long endId; // ID of the ending PointOfInterest

    @ManyToOne
    @JoinColumn(name = "roadtrip_id", nullable = false)
    private Roadtrip roadtrip; // Reference to the associated Roadtrip

    @Column(nullable = false)
    private LineString route;

    @Column
    private float distance;

    @Column
    private float travelTime;

    @Column
    private TravelMode travelMode;

    @Column
    private AcceptanceStatus status;

    // Getters and Setters
    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getStartId() {
        return startId;
    }

    public void setStartId(Long startId) {
        this.startId = startId;
    }

    public Long getEndId() {
        return endId;
    }

    public void setEndId(Long endId) {
        this.endId = endId;
    }

    public Roadtrip getRoadtrip() {
        return roadtrip;
    }

    public void setRoadtrip(Roadtrip roadtrip) {
        this.roadtrip = roadtrip;
    }

    public LineString getRoute() {
        return route;
    }

    public void setRoute(LineString route) {
        this.route = route;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(float travelTime) {
        this.travelTime = travelTime;
    }

    public TravelMode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }

    public AcceptanceStatus getStatus() {
        return status;
    }

    public void setStatus(AcceptanceStatus status) {
        this.status = status;
    }
}
