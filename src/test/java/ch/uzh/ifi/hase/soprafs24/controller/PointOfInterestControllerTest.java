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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        // Clear and reconfigure mockMvc before each test
        mockMvc = null;
        
        // Setup authentication for all tests - be more specific with the mock
        when(authenticationInterceptor.preHandle(any(HttpServletRequest.class), 
                                              any(HttpServletResponse.class), 
                                              any(Object.class)))
            .thenReturn(true);
        
        // Configure MockMvc with both interceptor and content negotiation
        mockMvc = MockMvcBuilders.standaloneSetup(pointOfInterestController)
                .addInterceptors(authenticationInterceptor)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

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

    @Test
    void createPointOfInterest_success() throws Exception {
        // given
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setName("Test POI");
        pointOfInterest.setCategory(PoiCategory.FOOD);
        pointOfInterest.setCreatorId(1L);
        
        PointOfInterestPostDTO pointOfInterestPostDTO = new PointOfInterestPostDTO();
        pointOfInterestPostDTO.setName("Test POI");
        pointOfInterestPostDTO.setCategory(PoiCategory.FOOD);
        pointOfInterestPostDTO.setCreatorId(1L);

        given(pointOfInterestService.createPointOfInterest(any(), anyLong(), anyString()))
                .willReturn(pointOfInterest);

        // when/then
        MockHttpServletRequestBuilder postRequest = post("/roadtrips/{roadtripId}/pois", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pointOfInterestPostDTO))
                .header("Authorization", "testToken");

        mockMvc.perform(postRequest)
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void getPointOfInterests_success() throws Exception {
        // given
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setName("Test POI");
        pointOfInterest.setCategory(PoiCategory.FOOD);

        List<PointOfInterest> allPois = Collections.singletonList(pointOfInterest);

        given(pointOfInterestService.getPointOfInterestsByRoadTrip(anyString(), anyLong()))
                .willReturn(allPois);
        doNothing().when(pointOfInterestService).calculateStatus(anyString(), any(), anyLong());

        // when/then
        mockMvc.perform(get("/roadtrips/{roadtripId}/pois", 1L)
                .header("Authorization", "testToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(pointOfInterest.getName())));
    }

    @Test
    void updatePointOfInterest_success() throws Exception {
        // given
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setName("Updated POI");
        pointOfInterest.setCategory(PoiCategory.FOOD);
        pointOfInterest.setCreatorId(1L);
        
        PointOfInterestPostDTO pointOfInterestPostDTO = new PointOfInterestPostDTO();
        pointOfInterestPostDTO.setName("Updated POI");
        pointOfInterestPostDTO.setCategory(PoiCategory.FOOD);
        pointOfInterestPostDTO.setCreatorId(1L);

        given(pointOfInterestService.getPointOfInterestByID(anyString(), anyLong(), anyLong()))
                .willReturn(pointOfInterest);
        doNothing().when(pointOfInterestService).updatePointOfInterest(any(), any());

        // when/then
        MockHttpServletRequestBuilder putRequest = put("/roadtrips/{roadtripId}/pois/{poiId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(pointOfInterestPostDTO))
                .header("Authorization", "testToken");

        mockMvc.perform(putRequest)
                .andDo(print())
                .andExpect(status().isNoContent());
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