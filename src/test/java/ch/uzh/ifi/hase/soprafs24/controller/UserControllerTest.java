package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private AuthenticationInterceptor authenticationInterceptor;

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setUserId(1L);
    user.setFirstName("firstname");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setFirstName("firstname");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);
    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId", is(user.getUserId().intValue())))
        .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_Username_taken() throws Exception {
    // // create first user
    // User user = new User();
    // user.setUsername("testUsername");

    // User user_2 = new User();
    // user_2.setUsername("testUsername");

    // UserPostDTO userPostDTO = new UserPostDTO();
    // userPostDTO.setUsername("testUsername");

    // UserPostDTO userPostDTO_2 = new UserPostDTO();
    // userPostDTO.setUsername("testUsername");

    // given(userService.createUser(Mockito.any())).willReturn(user);
    // given(userService.createUser(Mockito.any())).willReturn(user_2);

    // // when/then -> do the request + validate the result
    // MockHttpServletRequestBuilder postRequest_1 = post("/users")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(asJsonString(userPostDTO));

    // MockHttpServletRequestBuilder postRequest_2 = post("/users")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(asJsonString(userPostDTO_2));

    // // then
    // mockMvc.perform(postRequest_2)
    // .andExpect(status().isConflict());

    UserPostDTO userPostDTO_conflict = new UserPostDTO();
    userPostDTO_conflict.setUsername("username");
    userPostDTO_conflict.setPassword("password");

    given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));
    // mock auth token
    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

    MockHttpServletRequestBuilder postRequest_conflict = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO_conflict));

    mockMvc.perform(postRequest_conflict).andExpect(status().isConflict());
  }
//Testing POST /users with empty username
@Test
public void createUser_emptyUsername() throws Exception {
  UserPostDTO userPostDTO_conflict = new UserPostDTO();
  userPostDTO_conflict.setUsername("");
  userPostDTO_conflict.setPassword("password");

  given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
  // mock auth token
  given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

  MockHttpServletRequestBuilder postRequest_conflict = post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(asJsonString(userPostDTO_conflict));

  mockMvc.perform(postRequest_conflict).andExpect(status().isBadRequest());
}

//Testing GET /users/{id} successful
  @Test
  public void getUserById_UserExists() throws Exception {
    // given
    User user = new User();
    user.setFirstName("Firstname");
    user.setLastName("Lastname");
    user.setUsername("firstname Lastname");
    user.setPhoneNumber("079");
    user.setMail("firstname@lastname");
    user.setReceiveNotifications(true);
    user.setStatus(UserStatus.ONLINE);
    user.setUserId(999L);

    given(userService.getUserById(999L)).willReturn(user);
    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
        .willReturn(user); // Simuliert authentifizierten User

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/999")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "some_token"); // Simulierte Authentifizierung

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(user.getUserId().intValue()))) // id als einzelnes Feld überprüfen
        .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(user.getLastName())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())))
        .andExpect(jsonPath("$.mail", is(user.getMail())))
        .andExpect(jsonPath("$.receiveNotifications", is(user.getReceiveNotifications())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  //testing GET users/id with invalid id will throw "Forbidden"
  @Test
  public void givenUnauthorizedAccess_whenGetUserById_thenReturnForbidden() throws Exception {
      // given
      Long userId = 999L;
      //String token = "mock-token";

      // Mock the userService.getUserByToken method to return a mock User with a different ID
      User mockAuthenticatedUser = new User();
      mockAuthenticatedUser.setUserId(123L); // Different from the requested userId
      //given(userService.getUserByToken(token)).willReturn(mockAuthenticatedUser);
      given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
      given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token")))).willReturn(mockAuthenticatedUser);

      // Mock the userService.getUserById method to throw a ResponseStatusException with 404 Not Found
      given(userService.getUserById(userId)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

      // Debugging: Print the mock user ID
      System.out.println("Mock Authenticated User ID: " + mockAuthenticatedUser.getUserId());

      // when
      MockHttpServletRequestBuilder getRequest = get("/users/" + userId)
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "some_token");

      // then
      mockMvc.perform(getRequest)
          .andExpect(status().isForbidden())
          .andExpect(result -> {
              // Debugging: Print the response status
              System.out.println("Response Status: " + result.getResponse().getStatus());
          });
  }
  //PUT/users/id with valid input
  @Test
  public void updateUser_validInput_success() throws Exception {
    // given
    UserPostDTO userPostDTO = new UserPostDTO();

    // and
    User user = new User();
    user.setFirstName("Firstname Lastname");
    user.setUserId(999L); // getUserbyToken gibt User 1 zurück, PUT Request geht aber auf ID 999

    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
        .willReturn(user); // Simuliert authentifizierten User
    doNothing().when(userService).updateUser(Mockito.anyLong(), Mockito.any(User.class));

    // when
    MockHttpServletRequestBuilder putRequest = put("/users/999")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "some_token") // Simulierte Authentifizierung
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isNoContent()); // Erwartet 204 NO CONTENT
  }

  //PUT/users/id with wrong userId throws forbidden
  @Test
  public void givenUnauthorizedAccess_whenPUTUserById_thenReturnForbidden() throws Exception {
    // given
    Long userId = 123L;
    UserPostDTO userPostDTO = new UserPostDTO();

    // and
    User user = new User();
    user.setFirstName("Firstname Lastname");
    user.setUserId(999L); // getUserbyToken gibt User 1 zurück, PUT Request geht aber auf ID 999

    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
        .willReturn(user); // Simuliert authentifizierten User
    doNothing().when(userService).updateUser(Mockito.anyLong(), Mockito.any(User.class));

    // when
    MockHttpServletRequestBuilder putRequest = put("/users/" + userId)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "some_token") // Simulierte Authentifizierung
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isForbidden()); // Erwartet 403 Forbidden
  }

  //PUT with duplicate username
  @Test
  public void upddateUser_Username_taken() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("Username taken");

        // and
        User user = new User();
        user.setUsername("Username taken");
        user.setUserId(999L);

    doThrow(new ResponseStatusException(HttpStatus.CONFLICT))
            .when(userService).updateUser(anyLong(), any(User.class));
    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
        .willReturn(user); // Simuliert authentifizierten User

    // when
    MockHttpServletRequestBuilder putRequest = put("/users/999")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "some_token") // Simulierte Authentifizierung
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isConflict()); // Erwartet 409
  }

  //PUT with empty username will throw 400
  @Test
  public void upddateUser_Username_empty_willThrow400() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("");

        // and
        User user = new User();
        user.setUsername("Username");
        user.setUserId(999L);

    doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
            .when(userService).updateUser(anyLong(), any(User.class));
    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
        .willReturn(user); // Simuliert authentifizierten User

    // when
    MockHttpServletRequestBuilder putRequest = put("/users/999")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "some_token") // Simulierte Authentifizierung
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(putRequest)
        .andExpect(status().isBadRequest()); // Erwartet 400
  }

  //DELETE successfull
  @Test
  public void deleteUser_successfull() throws Exception {
        // given
        User user = new User();
        user.setUsername("Username");
        user.setUserId(999L);

    doNothing().when(userService).deleteUser(anyLong());

    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
        .willReturn(user); // Simuliert authentifizierten User

    // when
    MockHttpServletRequestBuilder deleteRequest = delete("/users/999")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "some_token"); // Simulierte Authentifizierung

    // then
    mockMvc.perform(deleteRequest)
        .andExpect(status().isNoContent()); // Erwartet 204
  }

  //DELETE unsuccessfull
  @Test
  public void deleteUser_unsuccessfull() throws Exception {
        // given
        User user = new User();
        user.setUsername("Username");
        user.setUserId(999L);

    doThrow(new ResponseStatusException(HttpStatus.CONFLICT))
        .when(userService).deleteUser(anyLong());

    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
        .willReturn(user); // Simuliert authentifizierten User

    // when
    MockHttpServletRequestBuilder deleteRequest = delete("/users/999")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "some_token"); // Simulierte Authentifizierung

    // then
    mockMvc.perform(deleteRequest)
        .andExpect(status().isConflict()); // Erwartet 409
  }

  //DELETE unauthorized
  @Test
  public void deleteUser_unauthorized() throws Exception {
        // given
        User user = new User();
        user.setUsername("Username");
        user.setUserId(999L);
        user.setToken("someToken");

        //and
        User otherUser = new User();
        otherUser.setFirstName("otherName");
        otherUser.setUserId(111L);
        otherUser.setToken("wrongToken");

    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.any()))
        .willReturn(otherUser); // Simuliert authentifizierten User

    // when
    MockHttpServletRequestBuilder deleteRequest = delete("/users/999")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "wrongToken"); // Simulierte Authentifizierung

    // then
    mockMvc.perform(deleteRequest)
        .andExpect(status().isForbidden()); // Erwartet 403
  }

  //Login successfull
  @Test
  public void loginUser_successfull() throws Exception {

    User mockUser = new User();
    mockUser.setUserId(1L);
    mockUser.setUsername("testuser");
    mockUser.setPassword("password");
    mockUser.setStatus(UserStatus.ONLINE);

    // given
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("testuser");
    userPostDTO.setPassword("password");

    given(userService.loginUser(Mockito.any()))
        .willReturn(mockUser);
    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

    // when
    MockHttpServletRequestBuilder postRequest = post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username", is(mockUser.getUsername())))
        .andExpect(jsonPath("$.userId", is(mockUser.getUserId().intValue())));
  }

  //logout successful
  @Test
  public void logoutUser_successfull() throws Exception {
    // given
    User user = new User();
    user.setFirstName("Firstname Lastname");
    user.setUserId(999L); // getUserbyToken gibt User 1 zurück, PUT Request geht aber auf ID 999

    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
        .willReturn(user); // Simuliert authentifizierten User
    doNothing().when(userService).logoutUser(Mockito.any());

    // when
    MockHttpServletRequestBuilder postRequest = post("/auth/logout")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "some_token"); // Simulierte Authentifizierung
        //.content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isOk()); // Erwartet 200
  }

  //test logout wrongId
  @Test
  public void logoutUser_Fail() throws Exception {
    // given
    User user = new User();
    user.setFirstName("Firstname Lastname");
    user.setUserId(999L); // getUserbyToken gibt User 1 zurück, PUT Request geht aber auf ID 999
    user.setToken("someToken");
    //and
    User otherUser = new User();
    otherUser.setFirstName("otherName");
    otherUser.setUserId(111L);
    otherUser.setToken("wrongToken");

    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
    given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
        .willReturn(user); // Simuliert authentifizierten User
    doNothing().when(userService).logoutUser(Mockito.any());

    // when
    MockHttpServletRequestBuilder postRequest = post("/auth/logout")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "some_token"); // Simulierte Authentifizierung
        //.content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isOk()); // Erwartet 200
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  public String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}