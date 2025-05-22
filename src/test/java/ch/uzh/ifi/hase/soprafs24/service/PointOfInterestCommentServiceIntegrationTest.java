package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PointOfInterestCommentServiceIntegrationTest {

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
    
    @Autowired
    private PointOfInterestCommentService pointOfInterestCommentService;

    @BeforeEach
    public void setup() {
        pointOfInterestRepository.deleteAll();
        roadtripMemberRepository.deleteAll();
        roadtripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void addComment_validInputs_success() {
        // Create test user
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreationDate(LocalDate.now());
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

        // When
        String commentText = "Test Comment";
        PointOfInterestComment comment = pointOfInterestCommentService.addComment(
                createdUser.getToken(), commentText, createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());

        // Then
        assertNotNull(comment);
        assertEquals(commentText, comment.getComment());
        assertEquals(createdUser.getUserId(), comment.getAuthorId());
        assertEquals(createdPoi.getPoiId(), comment.getPoi().getPoiId());
        assertNotNull(comment.getCreationDate());
    }

    @Test
    public void getComments_validInputs_returnsComments() {
        // Create test user
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreationDate(LocalDate.now());
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

        // Add user as roadtrip member
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(createdUser.getUserId());
        pk.setRoadtripId(createdRoadtrip.getRoadtripId());
        RoadtripMember member = new RoadtripMember();
        member.setRoadtripMemberId(pk);
        member.setUser(createdUser);
        member.setRoadtrip(createdRoadtrip);
        member.setInvitationStatus(InvitationStatus.ACCEPTED);
        roadtripMemberRepository.save(member);
        roadtripMemberRepository.flush();

        // Add a comment
        String commentText = "Test Comment";
        PointOfInterestComment createdComment = pointOfInterestCommentService.addComment(
                createdUser.getToken(), commentText, createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());

        // When
        List<PointOfInterestComment> comments = pointOfInterestCommentService.getComment(
                createdUser.getToken(), createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());

        // Then
        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
        assertEquals(commentText, comments.get(0).getComment());
        assertEquals(createdUser.getUserId(), comments.get(0).getAuthorId());
    }

    @Test
    public void deleteComment_validInputs_success() {
        // Create test user
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreationDate(LocalDate.now());
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

        // Add user as roadtrip member
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(createdUser.getUserId());
        pk.setRoadtripId(createdRoadtrip.getRoadtripId());
        RoadtripMember member = new RoadtripMember();
        member.setRoadtripMemberId(pk);
        member.setUser(createdUser);
        member.setRoadtrip(createdRoadtrip);
        member.setInvitationStatus(InvitationStatus.ACCEPTED);
        roadtripMemberRepository.save(member);
        roadtripMemberRepository.flush();

        // Create comment
        String commentText = "Test Comment";
        PointOfInterestComment createdComment = pointOfInterestCommentService.addComment(
                createdUser.getToken(), commentText, createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());

        // When
        pointOfInterestCommentService.deleteComment(
                createdUser.getToken(), createdComment.getCommentId(), createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());

        // Then
        List<PointOfInterestComment> comments = pointOfInterestCommentService.getComment(
                createdUser.getToken(), createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());
        assertTrue(comments.isEmpty());
    }

    @Test
    public void addComment_userNotMember_throwsException() {
        // Create owner
        User owner = new User();
        owner.setUsername("owner");
        owner.setPassword("password");
        owner.setFirstName("Owner");
        owner.setLastName("User");
        owner.setCreationDate(LocalDate.now());
        owner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        owner.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdOwner = userService.createUser(owner);

        // Create non-member
        User nonMember = new User();
        nonMember.setUsername("nonMember");
        nonMember.setPassword("password");
        nonMember.setFirstName("NonMember");
        nonMember.setLastName("User");
        nonMember.setCreationDate(LocalDate.now());
        nonMember.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        nonMember.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdNonMember = userService.createUser(nonMember);

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

        // Add owner as roadtrip member
        RoadtripMemberPK ownerPk = new RoadtripMemberPK();
        ownerPk.setUserId(createdOwner.getUserId());
        ownerPk.setRoadtripId(createdRoadtrip.getRoadtripId());
        RoadtripMember ownerMember = new RoadtripMember();
        ownerMember.setRoadtripMemberId(ownerPk);
        ownerMember.setUser(createdOwner);
        ownerMember.setRoadtrip(createdRoadtrip);
        ownerMember.setInvitationStatus(InvitationStatus.ACCEPTED);
        roadtripMemberRepository.save(ownerMember);
        roadtripMemberRepository.flush();

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            pointOfInterestCommentService.addComment(
                    createdNonMember.getToken(), "Test Comment", createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());
        });
    }

    @Test
    public void deleteComment_userNotAuthor_throwsException() {
        // Create users
        User author = new User();
        author.setUsername("author");
        author.setPassword("password");
        author.setFirstName("Author");
        author.setLastName("User");
        author.setCreationDate(LocalDate.now());
        author.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        author.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdAuthor = userService.createUser(author);

        User otherUser = new User();
        otherUser.setUsername("other");
        otherUser.setPassword("password");
        otherUser.setFirstName("Other");
        otherUser.setLastName("User");
        otherUser.setCreationDate(LocalDate.now());
        otherUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        otherUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdOtherUser = userService.createUser(otherUser);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setDescription("Test Description");
        Roadtrip createdRoadtrip = roadtripService.createRoadtrip(testRoadtrip, createdAuthor.getToken());

        // Add other user as member
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(createdOtherUser.getUserId());
        pk.setRoadtripId(createdRoadtrip.getRoadtripId());
        RoadtripMember member = new RoadtripMember();
        member.setRoadtripMemberId(pk);
        member.setUser(createdOtherUser);
        member.setRoadtrip(createdRoadtrip);
        member.setInvitationStatus(InvitationStatus.ACCEPTED);
        roadtripMemberRepository.save(member);

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
                poi, createdRoadtrip.getRoadtripId(), createdAuthor.getToken());

        // Create comment as author
        String commentText = "Test Comment";
        PointOfInterestComment createdComment = pointOfInterestCommentService.addComment(
                createdAuthor.getToken(), commentText, createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());

        // When/Then - try to delete comment as other user
        assertThrows(ResponseStatusException.class, () -> {
            pointOfInterestCommentService.deleteComment(
                    createdOtherUser.getToken(), createdComment.getCommentId(), 
                    createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());
        });
    }
}