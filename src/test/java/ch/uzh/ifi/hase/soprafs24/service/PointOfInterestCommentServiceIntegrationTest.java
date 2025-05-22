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

        private User createTestUser(String username) {
                // Create test user with all required fields
                User user = new User();
                user.setUsername(username);
                user.setPassword("password");
                user.setFirstName("First");
                user.setLastName("Last");
                user.setCreationDate(LocalDate.now());
                user.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
                user.setToken("token-" + java.util.UUID.randomUUID());
                return userService.createUser(user);
        }

        private Roadtrip createTestRoadtrip(User user) {
                // Create roadtrip
                Roadtrip roadtrip = new Roadtrip();
                roadtrip.setName("Test Roadtrip");
                roadtrip.setDescription("Test Description");
                return roadtripService.createRoadtrip(roadtrip, user.getToken());
        }

        private PointOfInterest createTestPoi(Roadtrip roadtrip, User user) {
                // Create POI
                PointOfInterest poi = new PointOfInterest();
                poi.setName("Test POI");
                poi.setDescription("Test Description");
                poi.setCategory(PoiCategory.SIGHTSEEING);
                org.locationtech.jts.geom.GeometryFactory geometryFactory = new org.locationtech.jts.geom.GeometryFactory();
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                                new org.locationtech.jts.geom.Coordinate(8.5417, 47.3769));
                poi.setCoordinate(point);
                return pointOfInterestService.createPointOfInterest(poi, roadtrip.getRoadtripId(), user.getToken());
        }

        private void addUserAsMemberToRoadtrip(User user, Roadtrip roadtrip, InvitationStatus status) {
                // Add user as roadtrip member
                RoadtripMemberPK pk = new RoadtripMemberPK();
                pk.setUserId(user.getUserId());
                pk.setRoadtripId(roadtrip.getRoadtripId());
                RoadtripMember member = new RoadtripMember();
                member.setRoadtripMemberId(pk);
                member.setUser(user);
                member.setRoadtrip(roadtrip);
                member.setInvitationStatus(status);
                roadtripMemberRepository.save(member);
                roadtripMemberRepository.flush();
        }

        @Test
        public void addComment_validInputs_success() {
                // Given
                User createdUser = createTestUser("testUsername");
                Roadtrip createdRoadtrip = createTestRoadtrip(createdUser);
                PointOfInterest createdPoi = createTestPoi(createdRoadtrip, createdUser);

                // When
                String commentText = "Test Comment";
                PointOfInterestComment comment = pointOfInterestCommentService.addComment(
                                createdUser.getToken(), commentText, createdPoi.getPoiId(),
                                createdRoadtrip.getRoadtripId());

                // Then
                assertNotNull(comment);
                assertEquals(commentText, comment.getComment());
                assertEquals(createdUser.getUserId(), comment.getAuthorId());
                assertEquals(createdPoi.getPoiId(), comment.getPoi().getPoiId());
                assertNotNull(comment.getCreationDate());
        }

        @Test
        public void getComments_validInputs_returnsComments() {
                // Given
                User createdUser = createTestUser("testUsername");
                Roadtrip createdRoadtrip = createTestRoadtrip(createdUser);
                PointOfInterest createdPoi = createTestPoi(createdRoadtrip, createdUser);
                addUserAsMemberToRoadtrip(createdUser, createdRoadtrip, InvitationStatus.ACCEPTED);

                // Add a comment
                String commentText = "Test Comment";
                pointOfInterestCommentService.addComment(
                                createdUser.getToken(), commentText, createdPoi.getPoiId(),
                                createdRoadtrip.getRoadtripId());

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
                // Given
                User createdUser = createTestUser("testUsername");
                Roadtrip createdRoadtrip = createTestRoadtrip(createdUser);
                PointOfInterest createdPoi = createTestPoi(createdRoadtrip, createdUser);
                addUserAsMemberToRoadtrip(createdUser, createdRoadtrip, InvitationStatus.ACCEPTED);

                // Create comment
                String commentText = "Test Comment";
                PointOfInterestComment createdComment = pointOfInterestCommentService.addComment(
                                createdUser.getToken(), commentText, createdPoi.getPoiId(),
                                createdRoadtrip.getRoadtripId());

                // When
                pointOfInterestCommentService.deleteComment(
                                createdUser.getToken(), createdComment.getCommentId(), createdPoi.getPoiId(),
                                createdRoadtrip.getRoadtripId());

                // Then
                List<PointOfInterestComment> comments = pointOfInterestCommentService.getComment(
                                createdUser.getToken(), createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());
                assertTrue(comments.isEmpty());
        }

        @Test
        public void addComment_userNotMember_throwsException() {
                // Given
                User createdOwner = createTestUser("owner");
                User createdNonMember = createTestUser("nonMember");
                Roadtrip createdRoadtrip = createTestRoadtrip(createdOwner);
                PointOfInterest createdPoi = createTestPoi(createdRoadtrip, createdOwner);
                addUserAsMemberToRoadtrip(createdOwner, createdRoadtrip, InvitationStatus.ACCEPTED);

                // When/Then
                assertThrows(ResponseStatusException.class, () -> {
                        pointOfInterestCommentService.addComment(
                                        createdNonMember.getToken(), "Test Comment", createdPoi.getPoiId(),
                                        createdRoadtrip.getRoadtripId());
                });
        }

        @Test
        public void deleteComment_userNotAuthor_throwsException() {
                // Given
                User createdAuthor = createTestUser("author");
                User createdOtherUser = createTestUser("other");
                Roadtrip createdRoadtrip = createTestRoadtrip(createdAuthor);
                addUserAsMemberToRoadtrip(createdOtherUser, createdRoadtrip, InvitationStatus.ACCEPTED);
                PointOfInterest createdPoi = createTestPoi(createdRoadtrip, createdAuthor);

                // Create comment as author
                String commentText = "Test Comment";
                PointOfInterestComment createdComment = pointOfInterestCommentService.addComment(
                                createdAuthor.getToken(), commentText, createdPoi.getPoiId(),
                                createdRoadtrip.getRoadtripId());

                // When/Then - try to delete comment as other user
                assertThrows(ResponseStatusException.class, () -> {
                        pointOfInterestCommentService.deleteComment(
                                        createdOtherUser.getToken(), createdComment.getCommentId(),
                                        createdPoi.getPoiId(), createdRoadtrip.getRoadtripId());
                });
        }
}
