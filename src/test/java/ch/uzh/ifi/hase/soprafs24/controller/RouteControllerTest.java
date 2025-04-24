package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.TravelMode;
import ch.uzh.ifi.hase.soprafs24.entity.Route;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RouteGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoutePostDTO;
import ch.uzh.ifi.hase.soprafs24.service.RouteService;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;

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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteController.class)
@ActiveProfiles("test")
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteService routeService;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @Test
    void createRoute_validInput_routeCreated() throws Exception {
        // given
        Route route = new Route();
        route.setRouteId(1L);
        route.setStartId(1L);
        route.setEndId(2L);
        route.setTravelMode(TravelMode.DRIVING_CAR);
        route.setDistance(1000.0f);
        route.setTravelTime(300.0f);
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        RoutePostDTO routePostDTO = new RoutePostDTO();
        routePostDTO.setStartId(1L);
        routePostDTO.setEndId(2L);
        routePostDTO.setTravelMode(TravelMode.DRIVING_CAR);

        given(routeService.createRoute(anyString(), anyLong(), any()))
                .willReturn(route);

        // when/then
        MockHttpServletRequestBuilder postRequest = post("/roadtrips/{roadtripId}/routes", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(routePostDTO))
                .header("Authorization", "testToken");

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.routeId", is(route.getRouteId().intValue())))
                .andExpect(jsonPath("$.startId", is(route.getStartId().intValue())))
                .andExpect(jsonPath("$.endId", is(route.getEndId().intValue())))
                .andExpect(jsonPath("$.travelMode", is(route.getTravelMode().toString())));
    }

    @Test
    void createRoute_invalidInput_throwsBadRequest() throws Exception {
        // given
        RoutePostDTO routePostDTO = new RoutePostDTO();
        routePostDTO.setStartId(1L);
        routePostDTO.setEndId(2L);
        routePostDTO.setTravelMode(TravelMode.DRIVING_CAR);
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        given(routeService.createRoute(anyString(), anyLong(), any()))
                .willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // when/then
        MockHttpServletRequestBuilder postRequest = post("/roadtrips/{roadtripId}/routes", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(routePostDTO))
                .header("Authorization", "testToken");

        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRoutes_validInput_routesReturned() throws Exception {
        // given
        Route route1 = new Route();
        route1.setRouteId(1L);
        route1.setStartId(1L);
        route1.setEndId(2L);
        route1.setTravelMode(TravelMode.DRIVING_CAR);
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        Route route2 = new Route();
        route2.setRouteId(2L);
        route2.setStartId(2L);
        route2.setEndId(3L);
        route2.setTravelMode(TravelMode.CYCLING_REGULAR);

        List<Route> allRoutes = Arrays.asList(route1, route2);

        given(routeService.getAllRoutes(anyString(), anyLong()))
                .willReturn(allRoutes);

        // when/then
        MockHttpServletRequestBuilder getRequest = get("/roadtrips/{roadtripId}/routes", 1L)
                .header("Authorization", "testToken");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].routeId", is(route1.getRouteId().intValue())))
                .andExpect(jsonPath("$[1].routeId", is(route2.getRouteId().intValue())));
    }

    @Test
    void deleteRoute_success() throws Exception {
        // when/then
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
        MockHttpServletRequestBuilder deleteRequest = delete("/roadtrips/{roadtripId}/routes/{routeId}", 1L, 1L)
                .header("Authorization", "testToken");

        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteRoute_notFound() throws Exception {
        // given
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(routeService).deleteRoute(anyString(), anyLong(), anyLong());
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        // when/then
        MockHttpServletRequestBuilder deleteRequest = delete("/roadtrips/{roadtripId}/routes/{routeId}", 1L, 999L)
                .header("Authorization", "testToken");

        mockMvc.perform(deleteRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRoutes_success() throws Exception {
        // when/then
        MockHttpServletRequestBuilder deleteRequest = delete("/roadtrips/{roadtripId}/routes", 1L)
                .header("Authorization", "testToken");
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    void updateRoute_validInput_routeUpdated() throws Exception {
        // given
        Route route = new Route();
        route.setRouteId(1L);
        route.setStartId(2L);
        route.setEndId(3L);
        route.setTravelMode(TravelMode.CYCLING_REGULAR);
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        RoutePostDTO routePostDTO = new RoutePostDTO();
        routePostDTO.setStartId(2L);
        routePostDTO.setEndId(3L);
        routePostDTO.setTravelMode(TravelMode.CYCLING_REGULAR);

        given(routeService.updateRoute(anyString(), anyLong(), anyLong(), any()))
                .willReturn(route);

        // when/then
        MockHttpServletRequestBuilder putRequest = put("/roadtrips/{roadtripId}/routes/{routeId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(routePostDTO))
                .header("Authorization", "testToken");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeId", is(route.getRouteId().intValue())))
                .andExpect(jsonPath("$.startId", is(route.getStartId().intValue())))
                .andExpect(jsonPath("$.endId", is(route.getEndId().intValue())))
                .andExpect(jsonPath("$.travelMode", is(route.getTravelMode().toString())));
    }

    @Test
    void updateRoute_notFound_throwsNotFound() throws Exception {
        // given
        RoutePostDTO routePostDTO = new RoutePostDTO();
        routePostDTO.setStartId(1L);
        routePostDTO.setEndId(2L);
        routePostDTO.setTravelMode(TravelMode.DRIVING_CAR);
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        given(routeService.updateRoute(anyString(), anyLong(), anyLong(), any()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when/then
        MockHttpServletRequestBuilder putRequest = put("/roadtrips/{roadtripId}/routes/{routeId}", 1L, 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(routePostDTO))
                .header("Authorization", "testToken");

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
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