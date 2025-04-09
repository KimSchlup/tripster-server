package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripPostDTO;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripService;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RoadtripControllerTest
 * This is a WebMvcTest which allows to test the RoadtripController i.e.
 * GET/POST
 * request without actually sending them over the network.
 * This tests if the RoadtripController works.
 */
@WebMvcTest(RoadtripController.class)
@ActiveProfiles("dev")
public class RoadtripControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private RoadtripService roadtripService;

  @MockBean
  private AuthenticationInterceptor authenticationInterceptor;

  @Test
  public void createRoadtrip_validInput_roadtripCreated() throws Exception {
    // given
    String token = "test-token";

    User testUser = new User();
    testUser.setUserId(100L);
    testUser.setUsername("testuser");
    testUser.setToken("1");

    Roadtrip roadtrip = new Roadtrip();
    roadtrip.setRoadtripId(1L);
    roadtrip.setName("Test User");
    roadtrip.setOwner(testUser);
    roadtrip.setDescription("Test Description");

    RoadtripPostDTO roadtripPostDTO = new RoadtripPostDTO();
    roadtripPostDTO.setName("Test Roadtrip");
    roadtripPostDTO.setDescription("Test Description");

    given(roadtripService.createRoadtrip(Mockito.any(), Mockito.any())).willReturn(roadtrip);
    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/roadtrips")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(asJsonString(roadtripPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.roadtripId", is(roadtrip.getRoadtripId().intValue())))
        .andExpect(jsonPath("$.name", is(roadtrip.getName())))
        .andExpect(jsonPath("$.description", is(roadtrip.getDescription())));
  }

  @Test
  public void deleteRoadtrip_validId_roadtripDeleted() throws Exception {
    // given
    Long roadtripId = 52L;
    String token = "test-token";
    User mockUser = new User();
    mockUser.setUserId(42L); // Assume this user is the owner

    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any()))
        .willReturn(true);
    given(userService.getUserByToken(token))
        .willReturn(mockUser);

    // when/then -> perform delete request and expect 204 No Content
    mockMvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .delete("/roadtrips/{roadtripId}", roadtripId)
            .header("Authorization", token))
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
