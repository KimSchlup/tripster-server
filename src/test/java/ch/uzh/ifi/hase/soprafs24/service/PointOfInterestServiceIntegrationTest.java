package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PointOfInterestServiceIntegrationTest {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoadtripRepository roadtripRepository;

        @Autowired
        private RoadtripMemberRepository roadtripMemberRepository;

        @Autowired
        private PointOfInterestRepository pointOfInterestRepository;

        @Autowired
        private UserService userService;

        @Autowired
        private RoadtripService roadtripService;

        @Autowired
        private PointOfInterestService pointOfInterestService;

        private GeometryFactory geometryFactory = new GeometryFactory();

        @BeforeEach
        public void setup() {
                pointOfInterestRepository.deleteAll();
                roadtripMemberRepository.deleteAll();
                roadtripRepository.deleteAll();
                userRepository.deleteAll();
        }

        private User createTestUser(String username) {
                User user = new User();
                user.setUsername(username);
                user.setPassword("password");
                user.setFirstName("Test");
                user.setLastName("User");
                user.setCreationDate(LocalDate.now());
                user.setStatus(UserStatus.ONLINE);
                user.setToken("token-" + UUID.randomUUID());
                return userService.createUser(user);
        }

        private Roadtrip createTestRoadtrip(User user) {
                Roadtrip roadtrip = new Roadtrip();
                roadtrip.setName("Test Roadtrip");
                roadtrip.setDescription("Test Description");
                return roadtripService.createRoadtrip(roadtrip, user.getToken());
        }

        private Point createCoordinate(double x, double y) {
                return geometryFactory.createPoint(new Coordinate(x, y));
        }

        private PointOfInterest createTestPOI(Roadtrip roadtrip, User creator) {
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                poi.setPriority(PoiPriority.HIGH);
                poi.setRoadtrip(roadtrip);
                poi.setCreatorId(creator.getUserId());
                poi.setCoordinate(createCoordinate(8.5417, 47.3769));
                return pointOfInterestRepository.save(poi);
        }

        @Test
        public void getPointOfInterests_returnsAllPois() {
                // Create test user and POI
                User user = createTestUser("user1");
                Roadtrip roadtrip = createTestRoadtrip(user);
                createTestPOI(roadtrip, user);

                // When
                List<PointOfInterest> pois = pointOfInterestService.getPointOfInterests();

                // Then
                assertFalse(pois.isEmpty());
                assertTrue(pois.stream().anyMatch(p -> p.getName().equals("Test POI")));
        }

        @Test
        public void getPointOfInterestsByRoadTrip_userIsMember_returnsPois() {
                // Create test user and POI
                User user = createTestUser("user2");
                Roadtrip roadtrip = createTestRoadtrip(user);
                createTestPOI(roadtrip, user);

                // When
                List<PointOfInterest> pois = pointOfInterestService.getPointOfInterestsByRoadTrip(user.getToken(),
                                roadtrip.getRoadtripId());

                // Then
                assertEquals(1, pois.size());
                assertEquals("Test POI", pois.get(0).getName());
        }

        @Test
        public void getPointOfInterestsByRoadTrip_userNotMember_throwsUnauthorized() {
                // Create test users and roadtrip
                User owner = createTestUser("owner");
                User outsider = createTestUser("outsider");
                Roadtrip roadtrip = createTestRoadtrip(owner);

                // When/Then
                assertThrows(ResponseStatusException.class, () -> pointOfInterestService
                                .getPointOfInterestsByRoadTrip(outsider.getToken(), roadtrip.getRoadtripId()));
        }

        @Test
        public void createPointOfInterest_validInputs_success() {
                // Create test user and roadtrip
                User user = createTestUser("user3");
                Roadtrip roadtrip = createTestRoadtrip(user);

                // Create point of interest with all required fields
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                poi.setCoordinate(createCoordinate(8.5417, 47.3769));

                // When
                PointOfInterest createdPoi = pointOfInterestService.createPointOfInterest(poi, roadtrip.getRoadtripId(),
                                user.getToken());

                // Then
                assertNotNull(createdPoi);
                assertNotNull(createdPoi.getPoiId());
                assertEquals("Test POI", createdPoi.getName());
                assertEquals("Test Description", createdPoi.getDescription());
                assertEquals(PoiCategory.SIGHTSEEING, createdPoi.getCategory());
                assertEquals(PoiPriority.LOW, createdPoi.getPriority()); // Default value
                assertEquals(AcceptanceStatus.PENDING, createdPoi.getStatus()); // Default value
                assertEquals(user.getUserId(), createdPoi.getCreatorId());
                assertEquals(roadtrip.getRoadtripId(), createdPoi.getRoadtrip().getRoadtripId());
        }

        @Test
        public void createPointOfInterest_userNotMember_throwsUnauthorized() {
                // Create test users and roadtrip
                User owner = createTestUser("owner2");
                User outsider = createTestUser("outsider2");
                Roadtrip roadtrip = createTestRoadtrip(owner);

                // Create point of interest with all required fields
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                poi.setCoordinate(createCoordinate(8.5417, 47.3769));

                // When/Then
                assertThrows(ResponseStatusException.class, () -> pointOfInterestService.createPointOfInterest(poi,
                                roadtrip.getRoadtripId(), outsider.getToken()));
        }

        @Test
        public void getPointOfInterestByID_validId_returnsPoi() {
                // Create test user and POI
                User user = createTestUser("user4");
                Roadtrip roadtrip = createTestRoadtrip(user);
                PointOfInterest poi = pointOfInterestService.createPointOfInterest(createTestPOI(roadtrip, user),
                                roadtrip.getRoadtripId(), user.getToken());

                // When
                PointOfInterest found = pointOfInterestService.getPointOfInterestByID(user.getToken(),
                                roadtrip.getRoadtripId(), poi.getPoiId());

                // Then
                assertNotNull(found);
                assertEquals(poi.getPoiId(), found.getPoiId());
                assertEquals("Test POI", found.getName());
        }

        @Test
        public void deletePointOfInterest_validInputs_success() {
                // Create test user and POI
                User user = createTestUser("user5");
                Roadtrip roadtrip = createTestRoadtrip(user);
                PointOfInterest poi = pointOfInterestService.createPointOfInterest(createTestPOI(roadtrip, user),
                                roadtrip.getRoadtripId(), user.getToken());

                // When
                pointOfInterestService.deletePointOfInterest(user.getToken(), roadtrip.getRoadtripId(), poi.getPoiId());

                // Then
                assertFalse(pointOfInterestRepository.existsById(poi.getPoiId()));
        }

        @Test
        public void isInsideBoundingBox_pointInside_returnsTrue() {
                // Create a bounding box polygon (simple square around Zurich)
                Coordinate[] boundingBoxCoords = new Coordinate[] {
                                new Coordinate(8.5, 47.3),
                                new Coordinate(8.6, 47.3),
                                new Coordinate(8.6, 47.4),
                                new Coordinate(8.5, 47.4),
                                new Coordinate(8.5, 47.3)
                };
                Polygon boundingBox = geometryFactory.createPolygon(boundingBoxCoords);

                // Create a PointOfInterest inside the bounding box
                PointOfInterest poi = new PointOfInterest();
                Point point = createCoordinate(8.5417, 47.3769); // Zurich
                poi.setCoordinate(point);

                // Call the method under test
                boolean result = pointOfInterestService.isInsideBoundingBox(poi, boundingBox);

                // Then
                assertTrue(result, "Expected point to be inside the bounding box");
        }

        @Test
        public void isInsideBoundingBox_pointOutside_returnsFalse() {
                // Create bounding box
                Coordinate[] boundingBoxCoords = new Coordinate[] {
                                new Coordinate(8.5, 47.3),
                                new Coordinate(8.6, 47.3),
                                new Coordinate(8.6, 47.4),
                                new Coordinate(8.5, 47.4),
                                new Coordinate(8.5, 47.3)
                };
                Polygon boundingBox = geometryFactory.createPolygon(boundingBoxCoords);

                // Create point outside
                PointOfInterest poi = new PointOfInterest();
                Point point = createCoordinate(8.7, 47.5); // Outside
                poi.setCoordinate(point);

                // When
                boolean result = pointOfInterestService.isInsideBoundingBox(poi, boundingBox);

                // Then
                assertFalse(result, "Expected point to be outside the bounding box");
        }

        @Test
        public void isInsideBoundingBox_emptyBoundingBox() {
                // Create empty bounding box
                Polygon boundingBox = geometryFactory.createPolygon();

                // Create point
                PointOfInterest poi = new PointOfInterest();
                Point point = createCoordinate(8.7, 47.5); // Inside, since empty
                poi.setCoordinate(point);

                // When
                boolean result = pointOfInterestService.isInsideBoundingBox(poi, boundingBox);

                // Then
                assertTrue(result, "Expected point to be inside the bounding box");
        }
}
