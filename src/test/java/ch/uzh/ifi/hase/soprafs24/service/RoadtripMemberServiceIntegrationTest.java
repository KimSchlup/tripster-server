package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.User;
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
public class RoadtripMemberServiceIntegrationTest {

    @Autowired
    private RoadtripRepository roadtripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoadtripMemberRepository roadtripMemberRepository;

    @Autowired
    private RoadtripMemberService roadtripMemberService;

    @Autowired
    private UserService userService;

    @Autowired
    private ch.uzh.ifi.hase.soprafs24.repository.PointOfInterestRepository pointOfInterestRepository;

    @BeforeEach
    public void setup() {
        // Delete in correct order to avoid foreign key constraint violations
        pointOfInterestRepository.deleteAll();
        roadtripMemberRepository.deleteAll();
        roadtripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getRoadtripMembers_validRoadtripId_returnsMembers() {
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
        testRoadtrip.setOwner(createdOwner);
        Roadtrip createdRoadtrip = roadtripRepository.save(testRoadtrip);

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

        // When
        List<RoadtripMember> members = roadtripMemberService.getRoadtripMembers(createdRoadtrip.getRoadtripId());

        // Then
        assertEquals(1, members.size());
        assertEquals(createdMember.getUserId(), members.get(0).getUser().getUserId());
        assertEquals(InvitationStatus.ACCEPTED, members.get(0).getInvitationStatus());
    }

    @Test
    public void createRoadtripMember_validInputs_success() {
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

        User invitedUser = new User();
        invitedUser.setUsername("invited");
        invitedUser.setPassword("password");
        invitedUser.setFirstName("Invited");
        invitedUser.setLastName("User");
        invitedUser.setCreationDate(java.time.LocalDate.now());
        invitedUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        invitedUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        userService.createUser(invitedUser);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setOwner(createdOwner);
        Roadtrip createdRoadtrip = roadtripRepository.save(testRoadtrip);

        // When
        RoadtripMember createdMember = roadtripMemberService.createRoadtripMember(
                createdRoadtrip.getRoadtripId(), createdOwner, "invited");

        // Then
        assertNotNull(createdMember);
        assertEquals("invited", createdMember.getUser().getUsername());
        assertEquals(createdRoadtrip.getRoadtripId(), createdMember.getRoadtrip().getRoadtripId());
        assertEquals(InvitationStatus.PENDING, createdMember.getInvitationStatus());
    }

    @Test
    public void createRoadtripMember_userNotOwner_throwsForbidden() {
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

        User nonOwner = new User();
        nonOwner.setUsername("nonOwner");
        nonOwner.setPassword("password");
        nonOwner.setFirstName("NonOwner");
        nonOwner.setLastName("User");
        nonOwner.setCreationDate(java.time.LocalDate.now());
        nonOwner.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        nonOwner.setToken("token-" + java.util.UUID.randomUUID().toString());
        User createdNonOwner = userService.createUser(nonOwner);

        User invitedUser = new User();
        invitedUser.setUsername("invited");
        invitedUser.setPassword("password");
        invitedUser.setFirstName("Invited");
        invitedUser.setLastName("User");
        invitedUser.setCreationDate(java.time.LocalDate.now());
        invitedUser.setStatus(ch.uzh.ifi.hase.soprafs24.constant.UserStatus.ONLINE);
        invitedUser.setToken("token-" + java.util.UUID.randomUUID().toString());
        userService.createUser(invitedUser);

        // Create roadtrip
        Roadtrip testRoadtrip = new Roadtrip();
        testRoadtrip.setName("Test Roadtrip");
        testRoadtrip.setOwner(createdOwner);
        Roadtrip createdRoadtrip = roadtripRepository.save(testRoadtrip);

        // When/Then
        assertThrows(ResponseStatusException.class, () -> {
            roadtripMemberService.createRoadtripMember(
                    createdRoadtrip.getRoadtripId(), createdNonOwner, "invited");
        });
    }

    @Test
    public void updateRoadtripMember_validInputs_success() {
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
        testRoadtrip.setOwner(createdOwner);
        Roadtrip createdRoadtrip = roadtripRepository.save(testRoadtrip);

        // Create roadtrip member
        RoadtripMemberPK pk = new RoadtripMemberPK();
        pk.setUserId(createdMember.getUserId());
        pk.setRoadtripId(createdRoadtrip.getRoadtripId());

        RoadtripMember roadtripMember = new RoadtripMember();
        roadtripMember.setRoadtripMemberId(pk);
        roadtripMember.setUser(createdMember);
        roadtripMember.setRoadtrip(createdRoadtrip);
        roadtripMember.setInvitationStatus(InvitationStatus.PENDING);
        roadtripMemberRepository.save(roadtripMember);

        // Update member
        RoadtripMember updatedMember = new RoadtripMember();
        updatedMember.setInvitationStatus(InvitationStatus.ACCEPTED);

        // When
        roadtripMemberService.updateRoadtripMember(
                createdRoadtrip.getRoadtripId(), createdMember, updatedMember, createdMember.getUserId());

        // Then
        RoadtripMember retrievedMember = roadtripMemberRepository.findById(pk).orElse(null);
        assertNotNull(retrievedMember);
        assertEquals(InvitationStatus.ACCEPTED, retrievedMember.getInvitationStatus());
    }

    @Test
    public void deleteRoadtripMember_validInputs_success() {
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
        testRoadtrip.setOwner(createdOwner);
        Roadtrip createdRoadtrip = roadtripRepository.save(testRoadtrip);

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

        // When
        roadtripMemberService.deleteRoadtripMember(
                createdRoadtrip.getRoadtripId(), createdOwner, createdMember.getUserId());

        // Then
        assertFalse(roadtripMemberRepository.existsById(pk));
    }
}
