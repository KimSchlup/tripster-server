package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.AcceptanceStatus;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.constant.PoiPriority;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

        @BeforeEach
        public void setup() {
                pointOfInterestRepository.deleteAll();
                roadtripMemberRepository.deleteAll();
                roadtripRepository.deleteAll();
                userRepository.deleteAll();
        }

        @Test
        public void getPointOfInterests_returnsAllPois() {
                // Create test user with all required fields
                User testUser = new User();
                testUser.setUsername("testUsername");
                testUser.setPassword("password");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setCreationDate(java.time.LocalDate.now());
                testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdUser = userService.createUser(testUser);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

                // Create point of interest with all required fields
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                poi.setPriority(PoiPriority.HIGH);
                poi.setRoadtrip(createdRoadtrip);
                poi.setCreatorId(createdUser.getUserId());
                // Create a Point geometry for the coordinate
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769)); // Zurich coordinates
                poi.setCoordinate(point);
                pointOfInterestRepository.save(poi);

                // When
                List<PointOfInterest> pois = pointOfInterestService.getPointOfInterests();

                // Then
                assertFalse(pois.isEmpty());
                assertTrue(pois.stream().anyMatch(p -> p.getName().equals("Test POI")));
        }

        @Test
        public void getPointOfInterestsByRoadTrip_userIsMember_returnsPois() {
                // Create test user with all required fields
                User testUser = new User();
                testUser.setUsername("testUsername");
                testUser.setPassword("password");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setCreationDate(java.time.LocalDate.now());
                testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdUser = userService.createUser(testUser);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

                // Create point of interest with all required fields
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                poi.setPriority(PoiPriority.HIGH);
                poi.setRoadtrip(createdRoadtrip);
                poi.setCreatorId(createdUser.getUserId());
                // Create a Point geometry for the coordinate
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769)); // Zurich coordinates
                poi.setCoordinate(point);
                pointOfInterestRepository.save(poi);

                // When
                List<PointOfInterest> pois = pointOfInterestService.getPointOfInterestsByRoadTrip(
                                createdUser.getToken(), createdRoadtrip.getRoadtripId());

                // Then
                assertEquals(1, pois.size());
                assertEquals("Test POI", pois.get(0).getName());
        }

        @Test
        public void getPointOfInterestsByRoadTrip_userNotMember_throwsUnauthorized() {
                // Create test users with all required fields
                User owner = new User();
                owner.setUsername("owner");
                owner.setPassword("password");
                owner.setFirstName("Owner");
                owner.setLastName("User");
                owner.setCreationDate(java.time.LocalDate.now());
                owner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                owner.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdOwner = userService.createUser(owner);

                User nonMember = new User();
                nonMember.setUsername("nonMember");
                nonMember.setPassword("password");
                nonMember.setFirstName("NonMember");
                nonMember.setLastName("User");
                nonMember.setCreationDate(java.time.LocalDate.now());
                nonMember.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                nonMember.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdNonMember = userService.createUser(nonMember);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdOwner.getToken());

                // When/Then
                assertThrows(ResponseStatusException.class, () -> {
                        pointOfInterestService.getPointOfInterestsByRoadTrip(
                                        createdNonMember.getToken(), createdRoadtrip.getRoadtripId());
                });
        }

        @Test
        public void createPointOfInterest_validInputs_success() {
                // Create test user with all required fields
                User testUser = new User();
                testUser.setUsername("testUsername");
                testUser.setPassword("password");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setCreationDate(java.time.LocalDate.now());
                testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdUser = userService.createUser(testUser);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

                // Create point of interest with all required fields
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                // Create a Point geometry for the coordinate
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769)); // Zurich coordinates
                poi.setCoordinate(point);

                // When
                PointOfInterest createdPoi = pointOfInterestService.createPointOfInterest(
                                poi, createdRoadtrip.getRoadtripId(), createdUser.getToken());

                // Then
                assertNotNull(createdPoi);
                assertNotNull(createdPoi.getPoiId());
                assertEquals("Test POI", createdPoi.getName());
                assertEquals("Test Description", createdPoi.getDescription());
                assertEquals(PoiCategory.SIGHTSEEING, createdPoi.getCategory());
                assertEquals(PoiPriority.LOW, createdPoi.getPriority()); // Default value
                assertEquals(AcceptanceStatus.PENDING, createdPoi.getStatus()); // Default value
                assertEquals(createdUser.getUserId(), createdPoi.getCreatorId());
                assertEquals(createdRoadtrip.getRoadtripId(), createdPoi.getRoadtrip().getRoadtripId());
        }

        @Test
        public void createPointOfInterest_userNotMember_throwsUnauthorized() {
                // Create test users with all required fields
                User owner = new User();
                owner.setUsername("owner");
                owner.setPassword("password");
                owner.setFirstName("Owner");
                owner.setLastName("User");
                owner.setCreationDate(java.time.LocalDate.now());
                owner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                owner.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdOwner = userService.createUser(owner);

                User nonMember = new User();
                nonMember.setUsername("nonMember");
                nonMember.setPassword("password");
                nonMember.setFirstName("NonMember");
                nonMember.setLastName("User");
                nonMember.setCreationDate(java.time.LocalDate.now());
                nonMember.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                nonMember.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdNonMember = userService.createUser(nonMember);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdOwner.getToken());

                // Create point of interest with all required fields
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                // Create a Point geometry for the coordinate
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769)); // Zurich coordinates
                poi.setCoordinate(point);

                // When/Then
                assertThrows(ResponseStatusException.class, () -> {
                        pointOfInterestService.createPointOfInterest(
                                        poi, createdRoadtrip.getRoadtripId(), createdNonMember.getToken());
                });
        }

        @Test
        public void getPointOfInterestByID_validId_returnsPoi() {
                // Create test user with all required fields
                User testUser = new User();
                testUser.setUsername("testUsername");
                testUser.setPassword("password");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setCreationDate(java.time.LocalDate.now());
                testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdUser = userService.createUser(testUser);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

                // Create point of interest with all required fields
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                // Create a Point geometry for the coordinate
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769)); // Zurich coordinates
                poi.setCoordinate(point);
                PointOfInterest createdPoi = pointOfInterestService.createPointOfInterest(
                                poi, createdRoadtrip.getRoadtripId(), createdUser.getToken());

                // When
                PointOfInterest retrievedPoi = pointOfInterestService.getPointOfInterestByID(
                                createdUser.getToken(), createdRoadtrip.getRoadtripId(), createdPoi.getPoiId());

                // Then
                assertNotNull(retrievedPoi);
                assertEquals(createdPoi.getPoiId(), retrievedPoi.getPoiId());
                assertEquals("Test POI", retrievedPoi.getName());
        }

        @Test
        public void deletePointOfInterest_validInputs_success() {
                // Create test user with all required fields
                User testUser = new User();
                testUser.setUsername("testUsername");
                testUser.setPassword("password");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setCreationDate(java.time.LocalDate.now());
                testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdUser = userService.createUser(testUser);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

                // Create point of interest with all required fields
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                // Create a Point geometry for the coordinate
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769)); // Zurich coordinates
                poi.setCoordinate(point);
                PointOfInterest createdPoi = pointOfInterestService.createPointOfInterest(
                                poi, createdRoadtrip.getRoadtripId(), createdUser.getToken());

                // When
                pointOfInterestService.deletePointOfInterest(
                                createdUser.getToken(), createdRoadtrip.getRoadtripId(), createdPoi.getPoiId());

                // Then
                assertFalse(pointOfInterestRepository.existsById(createdPoi.getPoiId()));
        }

        @Test
        public void castVote_upvote_success() {
                // Create test users with all required fields
                User owner = new User();
                owner.setUsername("owner");
                owner.setPassword("password");
                owner.setFirstName("Owner");
                owner.setLastName("User");
                owner.setCreationDate(java.time.LocalDate.now());
                owner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                owner.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdOwner = userService.createUser(owner);

                User member = new User();
                member.setUsername("member");
                member.setPassword("password");
                member.setFirstName("Member");
                member.setLastName("User");
                member.setCreationDate(java.time.LocalDate.now());
                member.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                member.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdMember = userService.createUser(member);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdOwner.getToken());

                // Create roadtrip member
                RoadtripMemberPK pk = new RoadtripMemberPK();
                pk.setUserId(createdMember.getUserId());
                pk.setRoadtripId(createdRoadtrip.getRoadtripId());

                RoadtripMember roadtripMember = new RoadtripMember();
                roadtripMember.setRoadtripMemberId(pk);
                roadtripMember.setUser(createdMember);
                roadtripMember.setRoadtrip(createdRoadtrip);
                roadtripMember.setInvitationStatus(InvitationStatus.ACCEPTED);
                roadtripMemberRepository.save(roadtripMember);

                // Create point of interest with all required fields
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                // Create a Point geometry for the coordinate
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769)); // Zurich coordinates
                poi.setCoordinate(point);
                PointOfInterest createdPoi = pointOfInterestService.createPointOfInterest(
                                poi, createdRoadtrip.getRoadtripId(), createdOwner.getToken());

                // When
                pointOfInterestService.castVote(
                                createdMember.getToken(), createdRoadtrip.getRoadtripId(), createdPoi.getPoiId(),
                                "upvote");

                // Then
                PointOfInterest updatedPoi = pointOfInterestRepository.findById(createdPoi.getPoiId()).orElse(null);
                assertNotNull(updatedPoi);
                assertNotNull(updatedPoi.getUpvotes());
                assertTrue(updatedPoi.getUpvotes().contains(createdMember.getUserId()));
        }

        @Test
        public void deleteVote_validInputs_success() {
                // Create test users with all required fields
                User testUser = new User();
                testUser.setUsername("testUsername");
                testUser.setPassword("password");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setCreationDate(java.time.LocalDate.now());
                testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdUser = userService.createUser(testUser);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

                // Create POI
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769));
                poi.setCoordinate(point);
                PointOfInterest createdPoi = pointOfInterestService.createPointOfInterest(
                                poi, createdRoadtrip.getRoadtripId(), createdUser.getToken());

                // Cast vote first
                pointOfInterestService.castVote(
                                createdUser.getToken(), createdRoadtrip.getRoadtripId(), createdPoi.getPoiId(), "upvote");

                // When
                pointOfInterestService.deleteVote(
                                createdUser.getToken(), createdRoadtrip.getRoadtripId(), createdPoi.getPoiId());

                // Then
                PointOfInterest updatedPoi = pointOfInterestService.getPointOfInterestByID(
                                createdUser.getToken(), createdRoadtrip.getRoadtripId(), createdPoi.getPoiId());
                assertNotNull(updatedPoi);
                assertTrue(updatedPoi.getUpvotes().isEmpty());
        }

        @Test
        public void updatePointOfInterest_validInputs_success() {
                // Create test user
                User testUser = new User();
                testUser.setUsername("testUsername");
                testUser.setPassword("password");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setCreationDate(java.time.LocalDate.now());
                testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdUser = userService.createUser(testUser);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

                // Make user a member of the roadtrip
                RoadtripMemberPK pk = new RoadtripMemberPK();
                pk.setUserId(createdUser.getUserId());
                pk.setRoadtripId(createdRoadtrip.getRoadtripId());

                RoadtripMember roadtripMember = new RoadtripMember();
                roadtripMember.setRoadtripMemberId(pk);
                roadtripMember.setUser(createdUser);
                roadtripMember.setRoadtrip(createdRoadtrip);
                roadtripMember.setInvitationStatus(InvitationStatus.ACCEPTED);
                roadtripMemberRepository.save(roadtripMember);
                roadtripMemberRepository.flush();

                // Create original POI
                PointOfInterest originalPoi = new PointOfInterest();
                originalPoi.setName("Original POI");
                originalPoi.setDescription("Original Description");
                originalPoi.setCategory(PoiCategory.FOOD);
                originalPoi.setPriority(PoiPriority.LOW);
                originalPoi.setStatus(AcceptanceStatus.PENDING);
                originalPoi.setRoadtrip(createdRoadtrip);

                // Set coordinates
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769));
                originalPoi.setCoordinate(point);

                // Save original POI
                PointOfInterest createdPoi = pointOfInterestService.createPointOfInterest(
                                originalPoi, createdRoadtrip.getRoadtripId(), createdUser.getToken());

                // Create updated POI with new values
                PointOfInterest updatedPoi = new PointOfInterest();
                updatedPoi.setPoiId(createdPoi.getPoiId());
                updatedPoi.setName("Updated POI");
                updatedPoi.setDescription("Updated Description");
                updatedPoi.setCategory(PoiCategory.SIGHTSEEING);  // Actually change the category
                updatedPoi.setPriority(PoiPriority.HIGH);

                // When
                pointOfInterestService.updatePointOfInterest(createdPoi, updatedPoi);

                // Then
                PointOfInterest retrievedPoi = pointOfInterestService.getPointOfInterestByID(
                                createdUser.getToken(), createdRoadtrip.getRoadtripId(), createdPoi.getPoiId());

                // Verify all updated fields
                assertNotNull(retrievedPoi);
                assertEquals("Updated POI", retrievedPoi.getName());
                assertEquals("Updated Description", retrievedPoi.getDescription());
                assertEquals(PoiCategory.SIGHTSEEING, retrievedPoi.getCategory());
                assertEquals(PoiPriority.HIGH, retrievedPoi.getPriority());
                
                // Verify unchanged fields remain the same
                assertEquals(createdPoi.getPoiId(), retrievedPoi.getPoiId());
                assertEquals(createdPoi.getRoadtrip().getRoadtripId(), retrievedPoi.getRoadtrip().getRoadtripId());
                assertEquals(createdPoi.getCreatorId(), retrievedPoi.getCreatorId());
                assertEquals(point, retrievedPoi.getCoordinate());
        }

        @Test
        public void calculateStatus_majorityVoting_success() {

                // Create test users
                User owner = new User();
                owner.setUsername("owner");
                owner.setPassword("password");
                owner.setFirstName("Owner");
                owner.setLastName("User");
                owner.setCreationDate(java.time.LocalDate.now());
                owner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                owner.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdOwner = userService.createUser(owner);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdOwner.getToken());

                // Create POI
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769));
                poi.setCoordinate(point);
                PointOfInterest createdPoi = pointOfInterestService.createPointOfInterest(
                                poi, createdRoadtrip.getRoadtripId(), createdOwner.getToken());

                // Cast vote
                pointOfInterestService.castVote(
                                createdOwner.getToken(), createdRoadtrip.getRoadtripId(), createdPoi.getPoiId(), "upvote");

                // When
                pointOfInterestService.calculateStatus(
                                createdOwner.getToken(), createdPoi, createdRoadtrip.getRoadtripId());

                // Then
                PointOfInterest updatedPoi = pointOfInterestService.getPointOfInterestByID(
                                createdOwner.getToken(), createdRoadtrip.getRoadtripId(), createdPoi.getPoiId());
                assertEquals(AcceptanceStatus.ACCEPTED, updatedPoi.getStatus());
        }

        @Test
        public void isUserMemberOfRoadtrip_userIsMember_returnsTrue() {
                // Create test user with all required fields
                User testUser = new User();
                testUser.setUsername("testUsername");
                testUser.setPassword("password");
                testUser.setFirstName("Test");
                testUser.setLastName("User");
                testUser.setCreationDate(java.time.LocalDate.now());
                testUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                testUser.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdUser = userService.createUser(testUser);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdUser.getToken());

                // When
                boolean isMember = pointOfInterestService.isUserMemberOfRoadtrip(
                                createdUser.getToken(), createdRoadtrip.getRoadtripId());

                // Then
                assertTrue(isMember);
        }

        @Test
        public void isUserMemberOfRoadtrip_userNotMember_returnsFalse() {
                // Create test users with all required fields
                User owner = new User();
                owner.setUsername("owner");
                owner.setPassword("password");
                owner.setFirstName("Owner");
                owner.setLastName("User");
                owner.setCreationDate(java.time.LocalDate.now());
                owner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                owner.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdOwner = userService.createUser(owner);

                User nonMember = new User();
                nonMember.setUsername("nonMember");
                nonMember.setPassword("password");
                nonMember.setFirstName("NonMember");
                nonMember.setLastName("User");
                nonMember.setCreationDate(java.time.LocalDate.now());
                nonMember.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                nonMember.setToken("token-" + java.util.UUID.randomUUID().toString());
                User createdNonMember = userService.createUser(nonMember);

                // Create roadtrip
                Roadtrip testRoadtrip = new Roadtrip();
                testRoadtrip.setName("Test Roadtrip");
                testRoadtrip.setDescription("Test Description");
                Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdOwner.getToken());

                // When
                boolean isMember = pointOfInterestService.isUserMemberOfRoadtrip(
                                createdNonMember.getToken(), createdRoadtrip.getRoadtripId());

                // Then
                assertFalse(isMember);
        }
}
