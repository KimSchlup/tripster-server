package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserEmergencyContact;
import ch.uzh.ifi.hase.soprafs24.rest.dto.EmergencyContactGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.EmergencyContactPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.security.AuthenticationInterceptor;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserEmergencyContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @Test
    public void getUserEmergencyContacts_validInput_success() throws Exception {
        // given
        User authenticatedUser = new User();
        authenticatedUser.setUserId(1L);
        authenticatedUser.setUsername("testUser");

        User originalUser = new User();
        originalUser.setUserId(1L);
        originalUser.setUsername("testUser");

        List<UserEmergencyContact> emergencyContacts = new ArrayList<>();
        UserEmergencyContact contact1 = new UserEmergencyContact();
        contact1.setContactId(1L);
        contact1.setFirstName("John");
        contact1.setLastName("Doe");
        contact1.setPhoneNumber("1234567890");
        emergencyContacts.add(contact1);

        originalUser.setUserEmergencyContacts(emergencyContacts);

        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
        given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token")))).willReturn(authenticatedUser);
        given(userService.getUserById(1L)).willReturn(originalUser);
        given(userService.checkForRoadtripMembership(originalUser, authenticatedUser)).willReturn(true);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1/emergency-contacts")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "some_token");

        // then
        mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is(contact1.getFirstName())))
            .andExpect(jsonPath("$[0].lastName", is(contact1.getLastName())))
            .andExpect(jsonPath("$[0].phoneNumber", is(contact1.getPhoneNumber())));
    }

    @Test
    public void getUserEmergencyContacts_unauthorized_throwsForbidden() throws Exception {
        // given
        User authenticatedUser = new User();
        authenticatedUser.setUserId(2L);
        authenticatedUser.setUsername("otherUser");

        User originalUser = new User();
        originalUser.setUserId(1L);
        originalUser.setUsername("testUser");

        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
        given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token")))).willReturn(authenticatedUser);
        given(userService.getUserById(1L)).willReturn(originalUser);
        given(userService.checkForRoadtripMembership(originalUser, authenticatedUser)).willReturn(false);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1/emergency-contacts")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "some_token");

        // then
        mockMvc.perform(getRequest)
            .andExpect(status().isForbidden());
    }

    @Test
    public void createEmergencyContact_validInput_success() throws Exception {
        // given
        User authenticatedUser = new User();
        authenticatedUser.setUserId(1L);
        authenticatedUser.setUsername("testUser");

        EmergencyContactPostDTO emergencyContactPostDTO = new EmergencyContactPostDTO();
        emergencyContactPostDTO.setFirstName("John");
        emergencyContactPostDTO.setLastName("Doe");
        emergencyContactPostDTO.setPhoneNumber("1234567890");

        UserEmergencyContact savedContact = DTOMapper.INSTANCE.convertEmergencyContactPostDTOToEntity(emergencyContactPostDTO);
        savedContact.setContactId(1L);
        savedContact.setUser(authenticatedUser);

        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
        given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token")))).willReturn(authenticatedUser);
        given(userService.createEmergencyContact(anyLong(), any(UserEmergencyContact.class))).willReturn(savedContact);

        // when
        MockHttpServletRequestBuilder postRequest = post("/users/1/emergency-contacts")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "some_token")
            .content(asJsonString(emergencyContactPostDTO));

        // then
        mockMvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName", is(emergencyContactPostDTO.getFirstName())))
            .andExpect(jsonPath("$.lastName", is(emergencyContactPostDTO.getLastName())))
            .andExpect(jsonPath("$.phoneNumber", is(emergencyContactPostDTO.getPhoneNumber())));
    }

    @Test
    public void createEmergencyContact_unauthorized_throwsForbidden() throws Exception {
        // given
        User authenticatedUser = new User();
        authenticatedUser.setUserId(2L);
        authenticatedUser.setUsername("otherUser");

        EmergencyContactPostDTO emergencyContactPostDTO = new EmergencyContactPostDTO();
        emergencyContactPostDTO.setFirstName("John");
        emergencyContactPostDTO.setLastName("Doe");
        emergencyContactPostDTO.setPhoneNumber("1234567890");

        given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
        given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token")))).willReturn(authenticatedUser);

        // when
        MockHttpServletRequestBuilder postRequest = post("/users/1/emergency-contacts")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "some_token")
            .content(asJsonString(emergencyContactPostDTO));

        // then
        mockMvc.perform(postRequest)
            .andExpect(status().isForbidden());
    }

    /**
     * Helper Method to convert object into a JSON string
     * 
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
