package ch.uzh.ifi.hase.soprafs24.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checklist")
public class Checklist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "roadtrip_id")
    private Long roadtripId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "roadtrip_id")
    private Roadtrip roadtrip;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistElement> checklistElements = new ArrayList<>();

    // Getters and Setters
    public Long getRoadtripId() {
        return roadtripId;
    }

    public void setRoadtripId(Long roadtripId) {
        this.roadtripId = roadtripId;
    }

    public Roadtrip getRoadtrip() {
        return roadtrip;
    }

    public void setRoadtrip(Roadtrip roadtrip) {
        this.roadtrip = roadtrip;
    }

    public List<ChecklistElement> getChecklistElements() {
        return checklistElements;
    }

    public void setChecklistElements(List<ChecklistElement> checklistElements) {
        this.checklistElements = checklistElements;
    }
}
