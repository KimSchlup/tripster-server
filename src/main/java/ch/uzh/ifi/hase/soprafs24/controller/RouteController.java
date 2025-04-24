package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Route;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoutePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RouteGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RouteDeleteDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.RouteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // POST: Create a new route
    @PostMapping("/roadtrips/{roadtripId}/routes")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RouteGetDTO createRoute(@RequestHeader("Authorization") String token, @RequestBody RoutePostDTO routePostDTO, @PathVariable Long roadtripId) {
        Route routeInput = DTOMapper.INSTANCE.convertRoutePostDTOToEntity(routePostDTO);
        Route createdRoute = routeService.createRoute(token, roadtripId, routeInput);
        return DTOMapper.INSTANCE.convertEntityToRouteGetDTO(createdRoute);
    }

    // GET: Retrieve all routes
    @GetMapping("/roadtrips/{roadtripId}/routes")
    @ResponseStatus(HttpStatus.OK)
    public List<RouteGetDTO> getAllRoutes(@RequestHeader("Authorization") String token, @PathVariable Long roadtripId) {
        List<Route> routes = routeService.getAllRoutes(token, roadtripId);
        return routes.stream()
                .map(DTOMapper.INSTANCE::convertEntityToRouteGetDTO)
                .collect(Collectors.toList());
    }

    // DELETE: Delete a route
    @DeleteMapping("/roadtrips/{roadtripId}/routes/{routeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoute(@RequestHeader("Authorization") String token, @PathVariable Long roadtripId, @PathVariable Long routeId) {
        routeService.deleteRoute(token, roadtripId, routeId);
    }

    @DeleteMapping("/roadtrips/{roadtripId}/routes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoutes(@RequestHeader("Authorization") String token, @PathVariable Long roadtripId) {
        routeService.deleteRoutes(token, roadtripId);
    }
    
    // PUT: Update an existing route
    @PutMapping("/roadtrips/{roadtripId}/routes/{routeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public RouteGetDTO updateRoute(
            @RequestHeader("Authorization") String token,
            @PathVariable Long roadtripId,
            @PathVariable Long routeId,
            @RequestBody RoutePostDTO routePostDTO) {
        
        Route routeInput = DTOMapper.INSTANCE.convertRoutePostDTOToEntity(routePostDTO);
        Route updatedRoute = routeService.updateRoute(token, roadtripId, routeId, routeInput);
        return DTOMapper.INSTANCE.convertEntityToRouteGetDTO(updatedRoute);
    }
}
