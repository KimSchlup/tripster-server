package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.ChecklistCategory;
import ch.uzh.ifi.hase.soprafs24.constant.Priority;
import ch.uzh.ifi.hase.soprafs24.entity.Checklist;
import ch.uzh.ifi.hase.soprafs24.entity.ChecklistElement;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChecklistElementPostDTO;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;
import ch.uzh.ifi.hase.soprafs24.service.ChecklistService;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.RequestBuilder;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChecklistElementRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(ChecklistController.class)
@ActiveProfiles("test")

public class ChecklistControllerTest {

    @Autowired
    private MockMvc mockMvc;
  
    @MockBean
    private ChecklistService checklistService;
  
    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @MockBean
    private RoadtripRepository roadtripRepository;

    @MockBean
    private RoadtripMemberRepository roadtripMemberRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ChecklistElementRepository checklistElementRepository;

    @Test
    public void get_checklist_success() throws Exception {
        // given
        Long roadtripId = 1L;
        String token = "mock-token";

        Checklist mockChecklist = new Checklist();
        mockChecklist.setRoadtripId(roadtripId);

        //mock
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        doNothing().when(checklistService).checkAccessRights(roadtripId, token);
        given(checklistService.getChecklistByRoadtripId(roadtripId)).willReturn(mockChecklist);

        // when
        RequestBuilder getRequest = get("/roadtrips/" + roadtripId + "/checklist")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roadtripId", is(mockChecklist.getRoadtripId().intValue())));
    }

    @Test
    public void forbiddenAccess_get_checklist() throws Exception {
        // given
        Long roadtripId = 1L;
        String token = "mock-token";

        Checklist mockChecklist = new Checklist();
        mockChecklist.setRoadtripId(roadtripId);

        //mock
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        //given(checklistService.checkAccessRights(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN)).when(checklistService).checkAccessRights(anyLong(), any());
        given(checklistService.getChecklistByRoadtripId(roadtripId)).willReturn(mockChecklist);

        // when
        RequestBuilder getRequest = get("/roadtrips/" + roadtripId + "/checklist")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
            .andExpect(status().isForbidden());
    }


    @Test
    public void updateChecklist_NotFound() throws Exception {
        // given
        Long roadtripId = 1L;
        String token = "mock-token";

        Checklist mockChecklist = new Checklist();
        mockChecklist.setRoadtripId(roadtripId);

        //mock
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        doNothing().when(checklistService).checkAccessRights(roadtripId, token);
        given(checklistService.getChecklistByRoadtripId(roadtripId)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when
        RequestBuilder getRequest = get("/roadtrips/" + roadtripId + "/checklist")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
            .andExpect(status().isNotFound());
    }

    @Test
    public void addChecklistElement_success() throws Exception {
        // given
        Long roadtripId = 1L;
        String token = "mock-token";

        ChecklistElementPostDTO checklistElementPostDTO = new ChecklistElementPostDTO();
        checklistElementPostDTO.setName("Test Element");
        checklistElementPostDTO.setCategory(ChecklistCategory.TASK);
        checklistElementPostDTO.setPriority(Priority.HIGH);

        ChecklistElement mockChecklistElement = new ChecklistElement();
        mockChecklistElement.setChecklistElementId(1L);
        mockChecklistElement.setName("Test Element");
        mockChecklistElement.setCategory(ChecklistCategory.TASK);
        mockChecklistElement.setPriority(Priority.HIGH);
        mockChecklistElement.setIsCompleted(false);

        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
        doNothing().when(checklistService).checkAccessRights(roadtripId, token);
        given(checklistService.addChecklistElement(anyLong(), any(ChecklistElement.class)))
            .willReturn(mockChecklistElement);

        // when
        RequestBuilder postRequest = post("/roadtrips/" + roadtripId + "/checklist")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(checklistElementPostDTO));

        // then
        mockMvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.checklistElementId").value(1))
            .andExpect(jsonPath("$.name").value("Test Element"))
            .andExpect(jsonPath("$.category").value(ChecklistCategory.TASK.toString()))
            .andExpect(jsonPath("$.priority").value(Priority.HIGH.toString()))
            .andExpect(jsonPath("$.isCompleted").value(false));
    }

    //Test update checklistElement
    @Test
    public void updateChecklistElement_success() throws Exception {
        // given
        Long roadtripId = 1L;
        Long checklistelementId = 1L;
        String token = "mock-token";

        ChecklistElementPostDTO checklistElementPostDTO = new ChecklistElementPostDTO();
        checklistElementPostDTO.setName("Updated Element");
        checklistElementPostDTO.setCategory(ChecklistCategory.TASK);
        checklistElementPostDTO.setPriority(Priority.HIGH);
        checklistElementPostDTO.setIsCompleted(true);

        ChecklistElement mockChecklistElement = new ChecklistElement();
        mockChecklistElement.setChecklistElementId(checklistelementId);
        mockChecklistElement.setName("Updated Element");
        mockChecklistElement.setCategory(ChecklistCategory.TASK);
        mockChecklistElement.setPriority(Priority.HIGH);
        mockChecklistElement.setIsCompleted(true);

        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
        doNothing().when(checklistService).checkAccessRights(roadtripId, token);
        doNothing().when(checklistService).updateChecklistElement(any(ChecklistElement.class), eq(checklistelementId), eq(roadtripId));

        // when
        RequestBuilder putRequest = put("/roadtrips/" + roadtripId + "/checklist/" + checklistelementId)
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(checklistElementPostDTO));

        // then
        mockMvc.perform(putRequest)
            .andExpect(status().isNoContent());
    }
    //delete
    @Test
    public void deleteChecklistElement_success() throws Exception {
        // given
        Long roadtripId = 1L;
        Long checklistelementId = 1L;
        String token = "mock-token";
    
        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
        doNothing().when(checklistService).checkAccessRights(roadtripId, token);
        doNothing().when(checklistService).deleteChecklistElement(checklistelementId);
    
        // when
        RequestBuilder deleteRequest = delete("/roadtrips/" + roadtripId + "/checklist/" + checklistelementId)
            .header("Authorization", token);
    
        // then
        mockMvc.perform(deleteRequest)
            .andExpect(status().isNoContent());
    }

    // Helper method to convert object to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
