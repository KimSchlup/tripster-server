package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

import ch.uzh.ifi.hase.soprafs24.entity.Route;

public class RoutePutDTO {
    private List<Route> order;

    public void setOrder(List<Route> order) {
        this.order = order;
    }
    public List<Route> getOrder() {
        return order;
    }
}