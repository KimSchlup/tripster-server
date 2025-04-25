package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.PointOfInterestComment;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PointOfInterestCommentPostDTO;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;
import ch.uzh.ifi.hase.soprafs24.service.PointOfInterestCommentService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.doThrow;

@WebMvcTest(PointOfInterestCommentController.class)
class PointOfInterestCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointOfInterestCommentService pointOfInterestCommentService;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @Test
    void addComment_validInput_commentCreated() throws Exception {
        // given
        PointOfInterestComment comment = new PointOfInterestComment();
        comment.setCommentId(1L);
        comment.setComment("Test Comment");
        comment.setAuthorId(1L);
        comment.setCreationDate(LocalDate.now());

        PointOfInterestCommentPostDTO commentPostDTO = new PointOfInterestCommentPostDTO();
        commentPostDTO.setComment("Test Comment");

        given(pointOfInterestCommentService.addComment(anyString(), anyString(), anyLong(), anyLong()))
                .willReturn(comment);

        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);

        // when/then
        MockHttpServletRequestBuilder postRequest = post("/roadtrips/{roadtripId}/pois/{poiId}/comments", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(commentPostDTO))
                .header("Authorization", "testToken");

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId", is(comment.getCommentId().intValue())))
                .andExpect(jsonPath("$.comment", is(comment.getComment())));
    }

    @Test
    void addComment_invalidInput_throwsBadRequest() throws Exception {
        // given
        PointOfInterestCommentPostDTO commentPostDTO = new PointOfInterestCommentPostDTO();
        commentPostDTO.setComment(""); // Empty comment

        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(pointOfInterestCommentService.addComment(anyString(), anyString(), anyLong(), anyLong()))
                .willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        // when/then
        MockHttpServletRequestBuilder postRequest = post("/roadtrips/{roadtripId}/pois/{poiId}/comments", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(commentPostDTO))
                .header("Authorization", "testToken");

        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_unauthorized_throwsUnauthorized() throws Exception {
        // given
        PointOfInterestCommentPostDTO commentPostDTO = new PointOfInterestCommentPostDTO();
        commentPostDTO.setComment("Test Comment");

        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(pointOfInterestCommentService.addComment(anyString(), anyString(), anyLong(), anyLong()))
                .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // when/then
        MockHttpServletRequestBuilder postRequest = post("/roadtrips/{roadtripId}/pois/{poiId}/comments", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(commentPostDTO))
                .header("Authorization", "invalid-token");

        mockMvc.perform(postRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getComments_validInput_returnsComments() throws Exception {
        // given
        PointOfInterestComment comment = new PointOfInterestComment();
        comment.setCommentId(1L);
        comment.setComment("Test Comment");

        given(pointOfInterestCommentService.getComment(anyString(), anyLong(), anyLong()))
                .willReturn(Arrays.asList(comment));

        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);

        // when/then
        mockMvc.perform(get("/roadtrips/{roadtripId}/pois/{poiId}/comments", 1L, 1L)
                .header("Authorization", "testToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].commentId", is(comment.getCommentId().intValue())));
    }

    @Test
    void getComments_unauthorized_throwsUnauthorized() throws Exception {
        // given
        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(pointOfInterestCommentService.getComment(anyString(), anyLong(), anyLong()))
                .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // when/then
        mockMvc.perform(get("/roadtrips/{roadtripId}/pois/{poiId}/comments", 1L, 1L)
                .header("Authorization", "invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getComments_notFound_throwsNotFound() throws Exception {
        // given
        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(pointOfInterestCommentService.getComment(anyString(), anyLong(), anyLong()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when/then
        mockMvc.perform(get("/roadtrips/{roadtripId}/pois/{poiId}/comments", 1L, 999L)
                .header("Authorization", "testToken"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteComment_success() throws Exception {
        // given
        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);

        // when/then
        mockMvc.perform(delete("/roadtrips/{roadtripId}/pois/{poiId}/comments/{commentId}", 1L, 1L, 1L)
                .header("Authorization", "testToken"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_unauthorized_throwsUnauthorized() throws Exception {
        // given
        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);
        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED))
                .when(pointOfInterestCommentService)
                .deleteComment(anyString(), anyLong(), anyLong(), anyLong());

        // when/then
        mockMvc.perform(delete("/roadtrips/{roadtripId}/pois/{poiId}/comments/{commentId}", 1L, 1L, 1L)
                .header("Authorization", "invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteComment_notFound_throwsNotFound() throws Exception {
        // given
        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(pointOfInterestCommentService)
                .deleteComment(anyString(), anyLong(), anyLong(), anyLong());

        // when/then
        mockMvc.perform(delete("/roadtrips/{roadtripId}/pois/{poiId}/comments/{commentId}", 1L, 1L, 999L)
                .header("Authorization", "testToken"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteComment_forbidden_throwsForbidden() throws Exception {
        // given
        given(authenticationInterceptor.preHandle(any(), any(), any())).willReturn(true);
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN))
                .when(pointOfInterestCommentService)
                .deleteComment(anyString(), anyLong(), anyLong(), anyLong());

        // when/then
        mockMvc.perform(delete("/roadtrips/{roadtripId}/pois/{poiId}/comments/{commentId}", 1L, 1L, 1L)
                .header("Authorization", "testToken"))
                .andExpect(status().isForbidden());
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