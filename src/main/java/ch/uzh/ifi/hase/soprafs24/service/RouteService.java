package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.TravelMode;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.Route;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RouteRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import java.util.ArrayList;
    import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final RoadtripRepository roadTripRepository;
    private final PointOfInterestRepository pointOfInterestRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public RouteService(RouteRepository routeRepository,
                       @Qualifier("userRepository") UserRepository userRepository,
                       @Qualifier("roadtripRepository") RoadtripRepository roadTripRepository,
                       @Qualifier("pointOfInterestRepository") PointOfInterestRepository pointOfInterestRepository,
                       RestTemplate restTemplate) {
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
        this.roadTripRepository = roadTripRepository;
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.restTemplate = restTemplate;
    }

    // Create a new route
    public Route createRoute(String token, Long roadtripId, Route route) {
        // Find existing route first
        Optional<Route> existingRoute = routeRepository.findByRoadtrip_RoadtripId(roadtripId)
                .stream()
                .filter(r -> r.getStartId().equals(route.getStartId()) 
                         && r.getEndId().equals(route.getEndId()) 
                         && r.getTravelMode() == route.getTravelMode())
                .findFirst();

        // If route exists, update its status and return it
        if (existingRoute.isPresent()) {
            Route foundRoute = existingRoute.get();
            updateRouteStatus(foundRoute);
            return foundRoute;
        }

        // If no existing route found, continue with creating a new one
        List<PointOfInterest> pois = pointOfInterestRepository.findByRoadtrip_RoadtripId(roadtripId);
        Long startId = route.getStartId();
        Long endId = route.getEndId();
        PointOfInterest startPoi = null;
        PointOfInterest endPoi = null;
        String travelMode = convertFromTravelMode(route.getTravelMode());
        Roadtrip roadtrip = roadTripRepository.findById(roadtripId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));
        route.setRoadtrip(roadtrip);

        for (PointOfInterest poi : pois) {
            if (startId.equals(poi.getPoiId())) {
                startPoi = poi;
            }
            if (endId.equals(poi.getPoiId())) {
                endPoi = poi;
            }
        }

        if (startPoi == null || endPoi == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid start or end point");
        }

        // Prepare coordinates for the API request
        String payload = String.format(
            "{\"coordinates\":[[%f,%f],[%f,%f]]," +
            "\"profile\":\"%s\"," +
            "\"format\":\"geojson\"," + // Change to geojson format
            "\"units\":\"m\"," +
            "\"geometry_simplify\":false," + // Don't simplify geometry
            "\"instructions\":false}",  // We don't need turn-by-turn instructions
            startPoi.getCoordinate().getY(), startPoi.getCoordinate().getX(), // Swap X/Y to match lon/lat format
            endPoi.getCoordinate().getY(), endPoi.getCoordinate().getX(),
            travelMode
        );

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "5b3ce3597851110001cf6248fee290d1e11b43ce857cfa00b93e3708");
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");

        // Log the request details
        logger.debug("Request URL: {}", "https://api.openrouteservice.org/v2/directions/" + travelMode);
        logger.debug("Request payload: {}", payload);
        logger.debug("Request headers: {}", headers);

        // Log the exact URL and payload being sent
        logger.info("OpenRouteService URL: {}", "https://api.openrouteservice.org/v2/directions/" + travelMode);
        logger.info("Request payload: {}", payload);

        // Log request details
        logger.info("OpenRouteService Request URL: {}", "https://api.openrouteservice.org/v2/directions/" + travelMode);
        logger.info("Request payload: {}", payload);

        // Send API request
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.openrouteservice.org/v2/directions/" + travelMode,
                entity,
                String.class
        );

        // Log response
        logger.info("Response Status: {}", response.getStatusCode());
        logger.info("Response Body: {}", response.getBody());

        // Log the response
        logger.debug("Response status: {}", response.getStatusCode());
        logger.debug("Response body: {}", response.getBody());

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Failed to fetch route from OpenRouteService. Status: " + response.getStatusCode());
        }

        // Enhanced logging of the OpenRouteService response
        logger.info("Full OpenRouteService Response: {}", response.getBody());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            // Debug log the response structure
            logger.debug("Response structure: {}", rootNode.toString());

            // For OpenRouteService GeoJSON response, we need to look for 'routes' array
            if (!rootNode.has("routes") || rootNode.get("routes").size() == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "No routes found in OpenRouteService response");
            }

            JsonNode firstRoute = rootNode.get("routes").get(0);
            
            // Get summary information
            if (!firstRoute.has("summary")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Missing summary in route response");
            }
            
            JsonNode summary = firstRoute.get("summary");
            float distance = (float) summary.get("distance").asDouble();
            float duration = (float) summary.get("duration").asDouble();

            // Get geometry
            if (!firstRoute.has("geometry")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Missing geometry in route response");
            }

            // Convert the encoded polyline geometry
            String encodedGeometry = firstRoute.get("geometry").asText();
            LineString routeGeometry = decodePolyline(encodedGeometry);

            // Set route properties
            route.setRoute(routeGeometry);
            route.setDistance(distance);
            route.setTravelTime(duration);
            route.setRoadtrip(roadtrip);
            updateRouteStatus(route);
            route.setTravelMode(route.getTravelMode());
            route.setStartId(startId);
            route.setEndId(endId);

            return routeRepository.save(route);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse OpenRouteService response: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Failed to parse route response: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating route: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error creating route: " + e.getMessage());
        }
    }

    // Retrieve all routes
    public List<Route> getAllRoutes(String token, Long roadtripId) {
        User user = userRepository.findByToken(token);
        Optional<Roadtrip> roadtrip = roadTripRepository.findById(roadtripId);
        RoadtripMember roadtripMember = roadtrip.get().getRoadtripMembers().stream()
                .filter(member -> member.getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "User is not a member of the roadtrip"));
        
        // Get all routes and update their status before returning
        List<Route> routes = routeRepository.findByRoadtrip_RoadtripId(roadtripId);
        routes.forEach(this::updateRouteStatus);
        
        return routes;
    }

    

    public TravelMode convertToTravelMode(String travelMode){
        if (travelMode.equals("driving-car")) {
            return TravelMode.DRIVING_CAR;
        } else if (travelMode.equals("cycling-regular")) {
            return TravelMode.CYCLING_REGULAR;
        } else if (travelMode.equals("foot-walking")) {
            return TravelMode.FOOT_WALKING;
        } else if(travelMode.equals("public-transport")){
            return TravelMode.PUBLIC_TRANSPORT;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid travel mode");
        }
    }

    public String convertFromTravelMode(TravelMode travelMode) {
        if (travelMode == TravelMode.DRIVING_CAR) {
            return "driving-car";
        } else if (travelMode == TravelMode.CYCLING_REGULAR) {
            return "cycling-regular";
        } else if (travelMode == TravelMode.FOOT_WALKING) {
            return "foot-walking";
        } else if (travelMode == TravelMode.PUBLIC_TRANSPORT) {
            return "public-transport";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid travel mode");
        }
    }

    private void updateRouteStatus(Route route) {
        PointOfInterest startPoi = pointOfInterestRepository.findById(route.getStartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Start POI not found"));
        
        PointOfInterest endPoi = pointOfInterestRepository.findById(route.getEndId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "End POI not found"));
    
        // If either POI is rejected, the route is rejected
        if (startPoi.getStatus() == AcceptanceStatus.DECLINED || 
            endPoi.getStatus() == AcceptanceStatus.DECLINED) {
            route.setStatus(AcceptanceStatus.DECLINED);
        }
        // If both POIs are accepted, the route is accepted
        else if (startPoi.getStatus() == AcceptanceStatus.ACCEPTED && 
                 endPoi.getStatus() == AcceptanceStatus.ACCEPTED) {
            route.setStatus(AcceptanceStatus.ACCEPTED);
        }
        // Otherwise (if either or both are PENDING), the route stays/becomes PENDING
        else {
            route.setStatus(AcceptanceStatus.PENDING);
        }
    
        routeRepository.save(route);
    }

    

    private LineString decodePolyline(String encoded) {
        List<Coordinate> coordinates = new ArrayList<>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < encoded.length()) {
            int b;
            int shift = 0;
            int result = 0;
            
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            // Convert to decimal degrees
            double latitude = lat * 1e-5;
            double longitude = lng * 1e-5;
            coordinates.add(new Coordinate(longitude, latitude));
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createLineString(coordinates.toArray(new Coordinate[0]));
    }

    public void deleteRoute(String token, Long roadtripId, Long routeId) {
        // Verify user exists and is authenticated
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // Check if the roadtrip exists
        Roadtrip roadtrip = roadTripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Roadtrip not found"));

        // Find and verify the user's membership status
        RoadtripMember member = roadtrip.getRoadtripMembers().stream()
                .filter(m -> m.getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "User is not a member of this roadtrip"));

        // Check if member has accepted the roadtrip invitation
        if (member.getInvitationStatus() != InvitationStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "User must accept the roadtrip invitation before deleting routes");
        }

        // Find the route and verify it belongs to the roadtrip
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Route not found"));

        if (!route.getRoadtrip().getRoadtripId().equals(roadtripId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Route does not belong to this roadtrip");
        }

        // Delete the route
        routeRepository.deleteById(routeId);
    }


    public void deleteRoutes(String token, Long roadtripId) {
        // Verify user exists and is authenticated
        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        // Check if the roadtrip exists
        Roadtrip roadtrip = roadTripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Roadtrip not found"));

        // Find and verify the user's membership status
        RoadtripMember member = roadtrip.getRoadtripMembers().stream()
                .filter(m -> m.getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "User is not a member of this roadtrip"));

        // Check if member has accepted the roadtrip invitation
        if (member.getInvitationStatus() != InvitationStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "User must accept the roadtrip invitation before deleting routes");
        }

        // Get all routes for this roadtrip
        List<Route> routes = routeRepository.findByRoadtrip_RoadtripId(roadtripId);
        
        if (routes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "No routes found for this roadtrip");
        }

        // Delete all routes
        routeRepository.deleteAll(routes);

        logger.info("Deleted {} routes for roadtrip {}", routes.size(), roadtripId);
    }
}