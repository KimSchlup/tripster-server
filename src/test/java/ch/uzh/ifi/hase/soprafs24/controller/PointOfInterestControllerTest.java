package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.VotePutDTO;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;
import ch.uzh.ifi.hase.soprafs24.service.PointOfInterestService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PointOfInterestControllerTest
 * This is a WebMvcTest which allows to test the PointOfInterestController i.e.
 * GET/POST/PUT/DELETE
 * requests without actually sending them over the network.
 * This tests if the PointOfInterestController works.
 */
@WebMvcTest(PointOfInterestController.class)
@ActiveProfiles("test")
public class PointOfInterestControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private PointOfInterestService pointOfInterestService;

        @MockBean
        private UserService userService;

        @MockBean
        private AuthenticationInterceptor authenticationInterceptor;

        @MockBean
        private SimpMessagingTemplate messagingTemplate;

        private User testUser;
        private PointOfInterest testPoi;
        private String token;
        private Long roadtripId;
        private Long poiId;

        @BeforeEach
        public void setup() throws Exception {
                // Reset all mocks
                Mockito.reset(pointOfInterestService, userService, authenticationInterceptor, messagingTemplate);

                // Setup test data
                token = "test-token";
                roadtripId = 1L;
                poiId = 1L;

                testUser = new User();
                testUser.setUserId(1L);
                testUser.setUsername("testuser");
                testUser.setToken(token);

                testPoi = new PointOfInterest();
                testPoi.setPoiId(poiId);
                testPoi.setName("Test POI");
                testPoi.setCategory(PoiCategory.FOOD);
                testPoi.setCreatorId(testUser.getUserId());
                // We can't set the coordinate directly in the test since it requires a Point
                // object
                // The service layer will handle this conversion

                // Mock authentication interceptor to always return true
                given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);

                // Mock user service to return our test user
                given(userService.getUserByToken(token)).willReturn(testUser);

                // Mock isUserMemberOfRoadtrip to return true
                given(pointOfInterestService.isUserMemberOfRoadtrip(token, roadtripId)).willReturn(true);
        }

        @Test
        public void createPointOfInterest_success() throws Exception {
                // given
                // Create a valid GeoJSON coordinate as JsonNode
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode coordinateJson = mapper.createObjectNode();
                coordinateJson.put("type", "Point");
                coordinateJson.putArray("coordinates").add(8.5456).add(47.3739);

                PointOfInterestPostDTO poiPostDTO = new PointOfInterestPostDTO();
                poiPostDTO.setName("Test POI");
                poiPostDTO.setCategory(PoiCategory.FOOD);
                poiPostDTO.setCreatorId(testUser.getUserId());
                poiPostDTO.setCoordinate(coordinateJson);

                given(pointOfInterestService.createPointOfInterest(any(PointOfInterest.class), eq(roadtripId),
                                eq(token)))
                                .willReturn(testPoi);

                // when/then
                MockHttpServletRequestBuilder postRequest = post("/roadtrips/{roadtripId}/pois", roadtripId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(poiPostDTO))
                                .header("Authorization", token);

                mockMvc.perform(postRequest)
                                .andDo(print())
                                .andExpect(status().isCreated());
        }

        @Test
        public void getPointOfInterests_success() throws Exception {
                // given
                List<PointOfInterest> allPois = Collections.singletonList(testPoi);

                given(pointOfInterestService.getPointOfInterestsByRoadTrip(token, roadtripId))
                                .willReturn(allPois);
                doNothing().when(pointOfInterestService).calculateStatus(anyString(), any(), anyLong());

                // when/then
                mockMvc.perform(get("/roadtrips/{roadtripId}/pois", roadtripId)
                                .header("Authorization", token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].name", is(testPoi.getName())));
        }

        @Test
        public void updatePointOfInterest_success() throws Exception {
                // given
                // Create a valid GeoJSON coordinate as JsonNode
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode coordinateJson = mapper.createObjectNode();
                coordinateJson.put("type", "Point");
                coordinateJson.putArray("coordinates").add(8.5456).add(47.3739);

                PointOfInterestPostDTO poiPostDTO = new PointOfInterestPostDTO();
                poiPostDTO.setName("Updated POI");
                poiPostDTO.setCategory(PoiCategory.FOOD);
                poiPostDTO.setCreatorId(testUser.getUserId());
                poiPostDTO.setCoordinate(coordinateJson);

                given(pointOfInterestService.getPointOfInterestByID(token, roadtripId, poiId))
                                .willReturn(testPoi);
                doNothing().when(pointOfInterestService).updatePointOfInterest(any(PointOfInterest.class),
                                any(PointOfInterest.class));

                // when/then
                MockHttpServletRequestBuilder putRequest = put("/roadtrips/{roadtripId}/pois/{poiId}", roadtripId,
                                poiId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(poiPostDTO))
                                .header("Authorization", token);

                mockMvc.perform(putRequest)
                                .andDo(print())
                                .andExpect(status().isNoContent());
        }

        @Test
        public void deletePOI_success() throws Exception {
                // given
                doNothing().when(pointOfInterestService).deletePointOfInterest(token, roadtripId, poiId);

                // when/then
                mockMvc.perform(delete("/roadtrips/{roadtripId}/pois/{poiId}", roadtripId, poiId)
                                .header("Authorization", token))
                                .andExpect(status().isNoContent());
        }

        @Test
        public void castVote_success() throws Exception {
                // given
                VotePutDTO votePutDTO = new VotePutDTO();
                votePutDTO.setVote("upvote");

                doNothing().when(pointOfInterestService).castVote(token, roadtripId, poiId, "upvote");

                // when/then
                MockHttpServletRequestBuilder putRequest = put("/roadtrips/{roadtripId}/pois/{poiId}/votes", roadtripId,
                                poiId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(votePutDTO))
                                .header("Authorization", token);

                mockMvc.perform(putRequest)
                                .andExpect(status().isNoContent());
        }

        @Test
        public void deleteVote_success() throws Exception {
                // given
                doNothing().when(pointOfInterestService).deleteVote(token, roadtripId, poiId);

                // when/then
                mockMvc.perform(delete("/roadtrips/{roadtripId}/pois/{poiId}/votes", roadtripId, poiId)
                                .header("Authorization", token))
                                .andExpect(status().isOk());
        }

        /**
         * Helper Method to convert userPostDTO into a JSON string such that the input
         * can be processed
         * 
         * @param object
         * @return string
         */
        private String asJsonString(final Object object) {
                try {
                        return new ObjectMapper().writeValueAsString(object);
                } catch (JsonProcessingException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        String.format("The request body could not be created.%s", e.toString()));
                }
        }
}
