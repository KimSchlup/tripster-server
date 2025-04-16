package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripMemberPutDTO;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripMemberService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoadtripMemberController.class)
@ActiveProfiles("test")
public class RoadtripMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoadtripMemberService roadtripMemberService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @Test
    public void createRoadtripMember_validInput_success() throws Exception {
        // given
        String token = "test-token";

        Long roadtripId = 1L;
        Long invitedUserId = 2L;
        String invitedUsername = "test_name";

        User roadtripOwner = new User();
        roadtripOwner.setUserId(3L);
        roadtripOwner.setToken(token);

        Roadtrip roadtrip = new Roadtrip();
        roadtrip.setRoadtripId(roadtripId);
        roadtrip.setOwner(roadtripOwner);

        RoadtripMemberPK pk = new RoadtripMemberPK(invitedUserId, roadtripId);
        RoadtripMember roadtripMember = new RoadtripMember();
        roadtripMember.setRoadtripMemberId(pk);
        roadtripMember.setRoadtrip(roadtrip);
        roadtripMember.setInvitationStatus(InvitationStatus.PENDING);

        RoadtripMemberPostDTO postDTO = new RoadtripMemberPostDTO();
        postDTO.setUsername(invitedUsername);

        // mock all services used on controller and authentication
        given(userService.getUserByToken(token)).willReturn(roadtripOwner);
        given(roadtripMemberService.createRoadtripMember(Mockito.any(), Mockito.any(), Mockito.any()))
                .willReturn(roadtripMember);
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        // when
        MockHttpServletRequestBuilder postRequest = post("/roadtrips/{roadtripId}/members", roadtripId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(postDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roadtripId", is(roadtripId.intValue())))
                .andExpect(jsonPath("$.userId", is(invitedUserId.intValue())))
                .andExpect(jsonPath("$.invitationStatus", is("PENDING")));
    }

    @Test
    public void givenRoadtripMembers_whenGetRoadtripMembers_thenReturnJsonArrayt() throws Exception {

        // given
        String token = "test-token";
        Long roadtripId = 1L;

        User member = new User();
        member.setUserId(2L);

        Roadtrip roadtrip = new Roadtrip();
        roadtrip.setRoadtripId(roadtripId);

        RoadtripMember memberEntity = new RoadtripMember();
        memberEntity.setUser(member);
        memberEntity.setRoadtrip(roadtrip);
        memberEntity.setRoadtripMemberId(new RoadtripMemberPK(member.getUserId(), roadtripId));
        memberEntity.setInvitationStatus(InvitationStatus.ACCEPTED);

        // mock all services used on controller and authentication
        given(roadtripMemberService.getRoadtripMembers(roadtripId))
                .willReturn(Collections.singletonList(memberEntity));
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        MockHttpServletRequestBuilder getRequest = get("/roadtrips/{roadtripId}/members", roadtripId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(member.getUserId().intValue())))
                .andExpect(jsonPath("$[0].roadtripId", is(roadtripId.intValue())))
                .andExpect(jsonPath("$[0].invitationStatus", is("ACCEPTED")));
    }

    @Test
    public void updateRoadtripMember_validInput_success() throws Exception {

        // given
        String token = "test-token";
        Long roadtripId = 1L;
        Long userId = 2L;

        RoadtripMemberPutDTO putDTO = new RoadtripMemberPutDTO();
        putDTO.setInvitationStatus(InvitationStatus.ACCEPTED);

        User updatingUser = new User();
        updatingUser.setUserId(1L);
        updatingUser.setToken(token);

        given(userService.getUserByToken(token)).willReturn(updatingUser);
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        Mockito.doNothing().when(roadtripMemberService)
                .updateRoadtripMember(Mockito.eq(roadtripId), Mockito.eq(updatingUser), Mockito.any(),
                        Mockito.eq(userId));

        // when
        MockHttpServletRequestBuilder putRequest = put("/roadtrips/{roadtripId}/members/{userId}", roadtripId, userId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(putDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteRoadtripMember_validInput_success() throws Exception {
        // given
        String token = "test-token";
        Long roadtripId = 1L;
        Long userId = 2L;

        User deletingUser = new User();
        deletingUser.setUserId(1L);
        deletingUser.setToken(token);

        given(userService.getUserByToken(token)).willReturn(deletingUser);
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        Mockito.doNothing().when(roadtripMemberService)
                .deleteRoadtripMember(roadtripId, deletingUser, userId);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/roadtrips/{roadtripId}/members/{userId}", roadtripId,
                userId)
                .header("Authorization", token);

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());
    }

    public String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
