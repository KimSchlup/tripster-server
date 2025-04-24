package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.PoiCategory;
import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterest;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.VotePutDTO;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;
import ch.uzh.ifi.hase.soprafs24.service.PointOfInterestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointOfInterestController.class)
@ActiveProfiles("test")
class PointOfInterestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointOfInterestService pointOfInterestService;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PointOfInterestController pointOfInterestController;

    @BeforeEach
    void setup() throws Exception {
        // Setup authentication for all tests
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        
        // Configure MockMvc with the interceptor
        mockMvc = MockMvcBuilders.standaloneSetup(pointOfInterestController)
                .addInterceptors(authenticationInterceptor)
                .build();
    }

    // @Test
    // void createPOI_validInput_poiCreated() throws Exception {
    //     // given
    //     PointOfInterest poi = new PointOfInterest();
    //     poi.setPoiId(1L);
    //     poi.setName("Test POI");
    //     poi.setCategory(PoiCategory.SIGHTSEEING);

    //     ObjectMapper mapper = new ObjectMapper();
    //     JsonNode coordinateNode = mapper.createObjectNode()
    //             .put("longitude", 8.5417)
    //             .put("latitude", 47.3769);

    //     PointOfInterestPostDTO poiPostDTO = new PointOfInterestPostDTO();
    //     poiPostDTO.setName("Test POI");
    //     poiPostDTO.setCategory(PoiCategory.SIGHTSEEING);
    //     poiPostDTO.setCoordinate(coordinateNode);

    //     given(pointOfInterestService.createPointOfInterest(any(PointOfInterest.class), eq(1L), eq("testToken")))
    //             .willReturn(poi);

    //     // when/then
    //     MockHttpServletRequestBuilder postRequest = post("/roadtrips/{roadtripId}/pois", 1L)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(poiPostDTO))
    //             .header("Authorization", "testToken");

    //     mockMvc.perform(postRequest)
    //             .andExpect(status().isCreated())
    //             .andExpect(jsonPath("$.poiId", is(poi.getPoiId().intValue())))
    //             .andExpect(jsonPath("$.name", is(poi.getName())));
    // }

    // @Test
    // void updatePOI_validInput_success() throws Exception {
    //     // given
    //     ObjectMapper mapper = new ObjectMapper();
    //     JsonNode coordinateNode = mapper.createObjectNode()
    //             .put("longitude", 8.5417)
    //             .put("latitude", 47.3769);

    //     PointOfInterest existingPoi = new PointOfInterest();
    //     existingPoi.setPoiId(1L);
    //     existingPoi.setName("Original POI");

    //     PointOfInterestPostDTO poiPostDTO = new PointOfInterestPostDTO();
    //     poiPostDTO.setName("Updated POI");
    //     poiPostDTO.setCategory(PoiCategory.SIGHTSEEING);
    //     poiPostDTO.setCoordinate(coordinateNode);

    //     // Mock both service methods
    //     given(pointOfInterestService.getPointOfInterestByID(eq("testToken"), eq(1L), eq(1L)))
    //             .willReturn(existingPoi);
    //     doNothing().when(pointOfInterestService).updatePointOfInterest(any(), any());

    //     // when/then
    //     MockHttpServletRequestBuilder putRequest = put("/roadtrips/{roadtripId}/pois/{poiId}", 1L, 1L)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(poiPostDTO))
    //             .header("Authorization", "testToken");

    //     mockMvc.perform(putRequest)
    //             .andExpect(status().isNoContent());
    // }

    @Test
    void deletePOI_success() throws Exception {
        // given
        doNothing().when(pointOfInterestService).deletePointOfInterest(anyString(), anyLong(), anyLong());

        // when/then
        mockMvc.perform(delete("/roadtrips/{roadtripId}/pois/{poiId}", 1L, 1L)
                .header("Authorization", "testToken"))
                .andExpect(status().isNoContent());
    }

    @Test
    void castVote_success() throws Exception {
        // given
        VotePutDTO votePutDTO = new VotePutDTO();
        votePutDTO.setVote("upvote");

        doNothing().when(pointOfInterestService).castVote(anyString(), anyLong(), anyLong(), anyString());

        // when/then
        MockHttpServletRequestBuilder putRequest = put("/roadtrips/{roadtripId}/pois/{poiId}/votes", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(votePutDTO))
                .header("Authorization", "testToken");

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVote_success() throws Exception {
        // given
        doNothing().when(pointOfInterestService).deleteVote(anyString(), anyLong(), anyLong());

        // when/then
        mockMvc.perform(delete("/roadtrips/{roadtripId}/pois/{poiId}/votes", 1L, 1L)
                .header("Authorization", "testToken"))
                .andExpect(status().isOk());
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}