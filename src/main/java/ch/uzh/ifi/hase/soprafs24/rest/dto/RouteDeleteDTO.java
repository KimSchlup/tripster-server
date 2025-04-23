package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class RouteDeleteDTO {
    private Long startId;
    private Long endId;

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
}