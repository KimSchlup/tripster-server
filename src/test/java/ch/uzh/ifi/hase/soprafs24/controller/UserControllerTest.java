package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
@ActiveProfiles("dev")
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private AuthenticationInterceptor authenticationInterceptor;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setFirstName("Firstname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);
    given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].firstName", is(user.getFirstName())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

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
    //     .contentType(MediaType.APPLICATION_JSON)
    //     .content(asJsonString(userPostDTO));

    // MockHttpServletRequestBuilder postRequest_2 = post("/users")
    //     .contentType(MediaType.APPLICATION_JSON)
    //     .content(asJsonString(userPostDTO_2));

    // // then
    // mockMvc.perform(postRequest_2)
    //     .andExpect(status().isConflict());

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

  @Test
  public void getUserById_UserExists() throws Exception {
      //given
      User user = new User();
      user.setName("Firstname Lastname");
      user.setUsername("firstname@lastname");
      user.setStatus(UserStatus.ONLINE);
      user.setId(999L);


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
              .andExpect(jsonPath("$.id", is(user.getId().intValue()))) // id als einzelnes Feld überprüfen
              .andExpect(jsonPath("$.name", is(user.getName())))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  public void updateUser_validInput_success() throws Exception {
// given
      UserPostDTO userPostDTO = new UserPostDTO();

      // and
      User user = new User();
      user.setName("Firstname Lastname");
      user.setId(999L); // getUserbyToken gibt User 1 zurück, PUT Request geht aber auf ID 999

      given(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);
      given(userService.getUserByToken(Mockito.argThat(token -> token.equals("some_token"))))
              .willReturn(user); // Simuliert authentifizierten User
      given(userService.updateUser(Mockito.anyLong(), Mockito.any(User.class))).willReturn(new User());

      // when
      MockHttpServletRequestBuilder putRequest = put("/users/999")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization", "some_token") // Simulierte Authentifizierung
              .content(asJsonString(userPostDTO));

      // then
      mockMvc.perform(putRequest)
              .andExpect(status().isNoContent()); // Erwartet 204 NO CONTENT
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