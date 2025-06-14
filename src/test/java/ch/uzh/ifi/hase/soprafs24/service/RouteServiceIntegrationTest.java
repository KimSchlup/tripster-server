package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.TravelMode;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.Route;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RouteRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

@WebAppConfiguration
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RouteServiceIntegrationTest {

    @Autowired
    private RouteService routeService;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoadtripRepository roadtripRepository;

    @Autowired
    private PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    private RoadtripMemberRepository roadtripMemberRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoadtripService roadtripService;

    @MockBean
    private RestTemplate restTemplate;

    private User createdUser;
    private Roadtrip createdRoadtrip;
    private PointOfInterest createdStartPoi;
    private PointOfInterest createdEndPoi;

    @BeforeEach
    public void setup() {
        // Clear all repositories
        routeRepository.deleteAll();
        pointOfInterestRepository.deleteAll();
        roadtripMemberRepository.deleteAll();
        roadtripRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreationDate(java.time.LocalDate.now());
        testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        this.createdUser = userService.createUser(testUser);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        this.createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

        // Make user a member of the roadtrip
        makeUserRoadtripMember(createdUser, createdRoadtrip);

        // Create POIs
        GeometryFactory geometryFactory = new GeometryFactory();
        
        // Create start POI
        PointOfInterest startPoi = new PointOfInterest();
        startPoi.setName("Start POI");
        startPoi.setDescription("Start Description");
        startPoi.setCategory(PoiCategory.SIGHTSEEING);
        startPoi.setRoadtrip(createdRoadtrip);
        Point startPoint = geometryFactory.createPoint(new Coordinate(8.681495, 49.41461));
        startPoi.setCoordinate(startPoint);
        startPoi.setCreatorId(createdUser.getUserId());
        this.createdStartPoi = pointOfInterestRepository.save(startPoi);

        // Create end POI
        PointOfInterest endPoi = new PointOfInterest();
        endPoi.setName("End POI");
        endPoi.setDescription("End Description");
        endPoi.setCategory(PoiCategory.SIGHTSEEING);
        endPoi.setRoadtrip(createdRoadtrip);
        Point endPoint = geometryFactory.createPoint(new Coordinate(8.687872, 49.420318));
        endPoi.setCoordinate(endPoint);
        endPoi.setCreatorId(createdUser.getUserId());
        this.createdEndPoi = pointOfInterestRepository.save(endPoi);

        // Setup mock OpenRouteService response
        String mockResponse = """
            {
                "routes": [{
                    "summary": {
                        "distance": 1408.8,
                        "duration": 281.9
                    },
                    "geometry": "_p~iF~ps|U_ulLnnqC_mqNvxq`@"
                }]
            }
            """;
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(
            anyString(),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(mockResponseEntity);
    }

    @Test
    public void createRoute_validInputs_success() {
        // Create route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);

        // When
        Route createdRoute = routeService.createRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), route);

        // Then
        assertNotNull(createdRoute);
        assertEquals(createdStartPoi.getPoiId(), createdRoute.getStartId());
        assertEquals(createdEndPoi.getPoiId(), createdRoute.getEndId());
        assertEquals(TravelMode.DRIVING_CAR, createdRoute.getTravelMode());
        assertEquals(1408.8f, createdRoute.getDistance());
        assertEquals(281.9f, createdRoute.getTravelTime());
        assertNotNull(createdRoute.getRoute());
    }

    @Test
    public void createRoute_duplicateRoute_returnsExisting() {
        // Create first route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);
        Route firstRoute = routeService.createRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), route);

        // Create second identical route
        Route duplicateRoute = new Route();
        duplicateRoute.setStartId(createdStartPoi.getPoiId());
        duplicateRoute.setEndId(createdEndPoi.getPoiId());
        duplicateRoute.setTravelMode(TravelMode.DRIVING_CAR);
        Route secondRoute = routeService.createRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), duplicateRoute);

        // Then
        assertEquals(firstRoute.getRouteId(), secondRoute.getRouteId());
        assertEquals(firstRoute.getStartId(), secondRoute.getStartId());
        assertEquals(firstRoute.getEndId(), secondRoute.getEndId());
        assertEquals(firstRoute.getTravelMode(), secondRoute.getTravelMode());
    }

    @Test
    public void getAllRoutes_validInputs_success() {
        // Create route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);
        routeService.createRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), route);

        // When
        List<Route> routes = routeService.getAllRoutes(createdUser.getToken(), createdRoadtrip.getRoadtripId());

        // Then
        assertNotNull(routes);
        assertEquals(1, routes.size());
        Route retrievedRoute = routes.get(0);
        assertEquals(createdStartPoi.getPoiId(), retrievedRoute.getStartId());
        assertEquals(createdEndPoi.getPoiId(), retrievedRoute.getEndId());
    }

    @Test
    public void deleteRoute_validInputs_success() {
        // Create route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);
        Route createdRoute = routeService.createRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), route);

        // When
        routeService.deleteRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), createdRoute.getRouteId());

        // Then
        List<Route> remainingRoutes = routeService.getAllRoutes(createdUser.getToken(), createdRoadtrip.getRoadtripId());
        assertTrue(remainingRoutes.isEmpty());
    }

    @Test
    public void updateRouteStatus_poiStatusChanges_routeStatusUpdated() {
        // Create POIs with pending status
        createdStartPoi.setStatus(AcceptanceStatus.PENDING);
        createdEndPoi.setStatus(AcceptanceStatus.PENDING);
        pointOfInterestRepository.save(createdStartPoi);
        pointOfInterestRepository.save(createdEndPoi);

        // Create route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);
        Route createdRoute = routeService.createRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), route);

        // Initial status should be PENDING
        assertEquals(AcceptanceStatus.PENDING, createdRoute.getStatus());

        // Update POI statuses to ACCEPTED
        createdStartPoi.setStatus(AcceptanceStatus.ACCEPTED);
        createdEndPoi.setStatus(AcceptanceStatus.ACCEPTED);
        pointOfInterestRepository.save(createdStartPoi);
        pointOfInterestRepository.save(createdEndPoi);

        // Get updated route
        List<Route> routes = routeService.getAllRoutes(createdUser.getToken(), createdRoadtrip.getRoadtripId());
        Route updatedRoute = routes.get(0);

        // Then
        assertEquals(AcceptanceStatus.ACCEPTED, updatedRoute.getStatus());
    }

    @Test
    public void createRoute_invalidPoi_throwsException() {
        // Create route with non-existent POI ID
        Route route = new Route();
        route.setStartId(9999L);
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);

        // Then
        assertThrows(ResponseStatusException.class, () -> {
            routeService.createRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), route);
        });
    }

    @Test
    public void createRoute_unauthorizedUser_throwsException() {
        // Create unauthorized user
        User unauthorizedUser = new User();
        unauthorizedUser.setUsername("unauthorized");
        unauthorizedUser.setPassword("password");
        unauthorizedUser.setFirstName("Unauthorized");
        unauthorizedUser.setLastName("User");
        unauthorizedUser.setCreationDate(java.time.LocalDate.now());
        unauthorizedUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        unauthorizedUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        userService.createUser(unauthorizedUser);

        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);

        // Then
        assertThrows(ResponseStatusException.class, () -> {
            routeService.createRoute(unauthorizedUser.getToken(), createdRoadtrip.getRoadtripId(), route);
        });
    }

    @Test
    public void updateRouteStatus_mixedPoiStatus_statusPending() {
        // Create POIs with different statuses
        createdStartPoi.setStatus(AcceptanceStatus.ACCEPTED);
        createdEndPoi.setStatus(AcceptanceStatus.PENDING);
        pointOfInterestRepository.save(createdStartPoi);
        pointOfInterestRepository.save(createdEndPoi);

        // Create route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);
        Route createdRoute = routeService.createRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), route);

        // Get updated route
        List<Route> routes = routeService.getAllRoutes(createdUser.getToken(), createdRoadtrip.getRoadtripId());
        Route updatedRoute = routes.get(0);

        // Then
        assertEquals(AcceptanceStatus.PENDING, updatedRoute.getStatus());
    }

    @Test
    public void createRoute_differentTravelModes_success() {
        // Test all travel modes
        TravelMode[] modes = {TravelMode.DRIVING_CAR, TravelMode.CYCLING_REGULAR, 
                             TravelMode.FOOT_WALKING};
        
        for (TravelMode mode : modes) {
            Route route = new Route();
            route.setStartId(createdStartPoi.getPoiId());
            route.setEndId(createdEndPoi.getPoiId());
            route.setTravelMode(mode);
            
            Route createdRoute = routeService.createRoute(createdUser.getToken(), 
                                                        createdRoadtrip.getRoadtripId(), 
                                                        route);
            
            assertEquals(mode, createdRoute.getTravelMode());
        }
    }

    @Test
    public void deleteRoute_invalidRouteId_throwsException() {
        assertThrows(ResponseStatusException.class, () -> {
            routeService.deleteRoute(createdUser.getToken(), createdRoadtrip.getRoadtripId(), 9999L);
        });
    }

    @Test
    public void deleteRoute_unauthorizedUser_throwsException() {
        // Create route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);
        Route createdRoute = routeService.createRoute(createdUser.getToken(), 
                                                    createdRoadtrip.getRoadtripId(), 
                                                    route);

        // Create unauthorized user
        User unauthorizedUser = new User();
        unauthorizedUser.setUsername("unauthorized");
        unauthorizedUser.setPassword("password");
        unauthorizedUser.setFirstName("Unauthorized");
        unauthorizedUser.setLastName("User");
        unauthorizedUser.setCreationDate(java.time.LocalDate.now());
        unauthorizedUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        unauthorizedUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        userService.createUser(unauthorizedUser);

        // Then
        String unauthorizedToken = unauthorizedUser.getToken();
        Long routeId = createdRoute.getRouteId();
        assertThrows(ResponseStatusException.class, () -> {
            routeService.deleteRoute(unauthorizedToken, createdRoadtrip.getRoadtripId(), routeId);
        });
    }

    @Test
    public void updateRoute_validInputs_success() {
        // Create initial route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);
        Route createdRoute = routeService.createRoute(createdUser.getToken(), 
                createdRoadtrip.getRoadtripId(), route);

        // Create updated route data
        Route updatedRoute = new Route();
        updatedRoute.setStartId(createdEndPoi.getPoiId());  // Swap start and end
        updatedRoute.setEndId(createdStartPoi.getPoiId());
        updatedRoute.setTravelMode(TravelMode.CYCLING_REGULAR);  // Change travel mode

        // When
        Route result = routeService.updateRoute(createdUser.getToken(), 
                createdRoadtrip.getRoadtripId(), 
                createdRoute.getRouteId(), 
                updatedRoute);

        // Then
        assertNotNull(result);
        assertEquals(updatedRoute.getStartId(), result.getStartId());
        assertEquals(updatedRoute.getEndId(), result.getEndId());
        assertEquals(updatedRoute.getTravelMode(), result.getTravelMode());
        assertNotNull(result.getRoute());
        assertTrue(result.getDistance() > 0);
        assertTrue(result.getTravelTime() > 0);
    }

    @Test
    public void updateRoute_invalidRouteId_throwsException() {
        Route updatedRoute = new Route();
        updatedRoute.setStartId(createdStartPoi.getPoiId());
        updatedRoute.setEndId(createdEndPoi.getPoiId());
        updatedRoute.setTravelMode(TravelMode.DRIVING_CAR);

        assertThrows(ResponseStatusException.class, () -> {
            routeService.updateRoute(createdUser.getToken(), 
                    createdRoadtrip.getRoadtripId(), 
                    999L,  // Non-existent route ID
                    updatedRoute);
        });
    }

    @Test
    public void updateRoute_unauthorizedUser_throwsException() {
        // Create initial route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);
        Route createdRoute = routeService.createRoute(createdUser.getToken(), 
                createdRoadtrip.getRoadtripId(), route);

        // Create unauthorized user
        User unauthorizedUser = new User();
        unauthorizedUser.setUsername("unauthorized");
        unauthorizedUser.setPassword("password");
        unauthorizedUser.setFirstName("Unauthorized");
        unauthorizedUser.setLastName("User");
        unauthorizedUser.setCreationDate(java.time.LocalDate.now());
        unauthorizedUser.setStatus(UserStatus.ONLINE);
        unauthorizedUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        userService.createUser(unauthorizedUser);

        // Create update data
        Route updatedRoute = new Route();
        updatedRoute.setStartId(createdEndPoi.getPoiId());
        updatedRoute.setEndId(createdStartPoi.getPoiId());
        updatedRoute.setTravelMode(TravelMode.CYCLING_REGULAR);

        // Then
        assertThrows(ResponseStatusException.class, () -> {
            routeService.updateRoute(unauthorizedUser.getToken(), 
                    createdRoadtrip.getRoadtripId(), 
                    createdRoute.getRouteId(), 
                    updatedRoute);
        });
    }

    @Test
    public void updateRoute_invalidPOIs_throwsException() {
        // Create initial route
        Route route = new Route();
        route.setStartId(createdStartPoi.getPoiId());
        route.setEndId(createdEndPoi.getPoiId());
        route.setTravelMode(TravelMode.DRIVING_CAR);
        Route createdRoute = routeService.createRoute(createdUser.getToken(), 
                createdRoadtrip.getRoadtripId(), route);

        // Create update with invalid POIs
        Route updatedRoute = new Route();
        updatedRoute.setStartId(999L);  // Non-existent POI
        updatedRoute.setEndId(998L);    // Non-existent POI
        updatedRoute.setTravelMode(TravelMode.CYCLING_REGULAR);

        assertThrows(ResponseStatusException.class, () -> {
            routeService.updateRoute(createdUser.getToken(), 
                    createdRoadtrip.getRoadtripId(), 
                    createdRoute.getRouteId(), 
                    updatedRoute);
        });
    }

    // Helper method to add in RouteServiceIntegrationTest
    private void makeUserRoadtripMember(User user, Roadtrip roadtrip) {
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(user.getUserId());
        pk.setRoadtripId(roadtrip.getRoadtripId());

        RoadtripMember roadtripMember = new RoadtripMember();
        roadtripMember.setRoadtripMemberId(pk);
        roadtripMember.setUser(user);
        roadtripMember.setRoadtrip(roadtrip);
        roadtripMember.setInvitationStatus(InvitationStatus.ACCEPTED);
        roadtripMemberRepository.save(roadtripMember);
        roadtripMemberRepository.flush();
    }

    @Test
    public void deleteRoutes_roadtripNotFound_throwsException() {
        Long nonExistentRoadtripId = 9999L;
    
        assertThrows(ResponseStatusException.class, () -> {
            routeService.deleteRoutes(createdUser.getToken(), nonExistentRoadtripId);
        });
    }

    @Test
    public void deleteRoutes_userNotMember_throwsException() {
        // Create a new roadtrip with a different owner
        Roadtrip newRoadtrip = new Roadtrip();
        newRoadtrip.setName("Lonely Roadtrip");
        newRoadtrip.setDescription("Nobody is here");
        newRoadtrip.setOwner(createdUser);
        Roadtrip savedRoadtrip = roadtripRepository.saveAndFlush(newRoadtrip);
    
        assertThrows(ResponseStatusException.class, () -> {
            routeService.deleteRoutes(createdUser.getToken(), savedRoadtrip.getRoadtripId());
        });
    }
    

    @Test
    public void deleteRoutes_userNotAcceptedInvitation_throwsException() {
        Roadtrip anotherRoadtrip = new Roadtrip();
        anotherRoadtrip.setName("Pending Trip");
        anotherRoadtrip.setDescription("Still waiting");
        anotherRoadtrip.setOwner(createdUser); 
        Roadtrip savedTrip = roadtripRepository.saveAndFlush(anotherRoadtrip);
    
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(createdUser.getUserId());
        pk.setRoadtripId(savedTrip.getRoadtripId());
    
        RoadtripMember member = new RoadtripMember();
        member.setRoadtripMemberId(pk);
        member.setUser(createdUser);
        member.setRoadtrip(savedTrip);
        member.setInvitationStatus(InvitationStatus.PENDING);
        roadtripMemberRepository.saveAndFlush(member);
    
        assertThrows(ResponseStatusException.class, () -> {
            routeService.deleteRoutes(createdUser.getToken(), savedTrip.getRoadtripId());
        });
    }
    
    @Test
    public void deleteRoutes_noRoutesFound_throwsException() {
        // Create new roadtrip and make user a member
        Roadtrip trip = new Roadtrip();
        trip.setName("Empty Trip");
        trip.setDescription("No routes yet");
        Roadtrip savedTrip = roadtripService.createRoadtrip(trip, createdUser.getToken());
        makeUserRoadtripMember(createdUser, savedTrip);
    
        assertThrows(ResponseStatusException.class, () -> {
            routeService.deleteRoutes(createdUser.getToken(), savedTrip.getRoadtripId());
        });
    }

    @Test
    public void convertToTravelMode_drivingCar_returnsDRIVING_CAR() {
        // When
        TravelMode result = routeService.convertToTravelMode("driving-car");
    
        // Then
        assertEquals(TravelMode.DRIVING_CAR, result);
    }
    
    @Test
    public void convertToTravelMode_cyclingRegular_returnsCYCLING_REGULAR() {
        // When
        TravelMode result = routeService.convertToTravelMode("cycling-regular");
    
        // Then
        assertEquals(TravelMode.CYCLING_REGULAR, result);
    }
    
    @Test
    public void convertToTravelMode_footWalking_returnsFOOT_WALKING() {
        // When
        TravelMode result = routeService.convertToTravelMode("foot-walking");
    
        // Then
        assertEquals(TravelMode.FOOT_WALKING, result);
    }
    
    @Test
    public void convertToTravelMode_invalidTravelMode_throwsException() {
        // When & Then
        assertThrows(ResponseStatusException.class, () -> {
            routeService.convertToTravelMode("invalid-mode");
        });
    }
    
        
}