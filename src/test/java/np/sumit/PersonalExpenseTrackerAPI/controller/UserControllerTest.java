package np.sumit.PersonalExpenseTrackerAPI.controller;

import np.sumit.PersonalExpenseTrackerAPI.config.SecurityConfig;
import np.sumit.PersonalExpenseTrackerAPI.dto.request.SignUpRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.request.UserRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.UserResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.exception.UserIdentifierRequiredException;
import np.sumit.PersonalExpenseTrackerAPI.service.CustomUserDetailsService;
import np.sumit.PersonalExpenseTrackerAPI.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;


import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldThrowExceptionIfUsernameIsInvalid() throws Exception {
        String password = "password";
        String email = "username@gmail.com";

        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername(null);
        signUpRequestDto.setPassword(password);
        signUpRequestDto.setEmail(email);

        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequestDto))
        )
                .andExpect(status()
                        .isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Username is required"))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(userService);
    }


    @Test

    void shouldRejectInvalidUsername() throws Exception {
        String username = "test&8";
        String password = "test@password";

        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername(username);
        signUpRequestDto.setPassword(password);
        signUpRequestDto.setEmail("test@gmail.com");

        mockMvc.perform(
                        post("/api/users/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(
                                "Username cannot contain spaces or special characters. " +
                                "Only letters, numbers, and underscores are allowed."
                        )
                )
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(userService);
    }

    @ParameterizedTest
    @MethodSource("invalidUsernames")
    void shouldRejectInvalidUsernameLength(String username) throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername(username);
        signUpRequestDto.setPassword("test@password");
        signUpRequestDto.setEmail("username@gmail.com");


        mockMvc.perform(
                        post("/api/users/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Username must be between 3 and 50 characters"))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(userService);
    }

    static Stream<String> invalidUsernames() {
        return Stream.of(
                "aa",
                "a".repeat(51)
        );
    }

    @Test
    void shouldRejectBlankPassword() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("username");
        signUpRequestDto.setEmail("username@gmail.com");

        mockMvc.perform(
                        post("/api/users/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Password is required"))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(userService);
    }


    @ParameterizedTest
    @MethodSource("invalidPasswordLength")
    void shouldRejectInvalidPasswordLength(String password) throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("username");
        signUpRequestDto.setPassword(password);
        signUpRequestDto.setEmail("username@gmail.com");

        mockMvc.perform(
                        post("/api/users/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Password must be between 8 and 50 characters"))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(userService);
    }

    static Stream<String> invalidPasswordLength() {
        return Stream.of(
                "p".repeat(7),
                "p".repeat(51)
        );
    }

    @Test
    void shouldSignUp() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("username");
        signUpRequestDto.setPassword("password");
        signUpRequestDto.setEmail("username@gmail.com");

        UserResponseDto expectedResponse = new  UserResponseDto(
                1L,
                signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                "message"
        );

        when(userService.createUser(any(SignUpRequestDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(
                post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequestDto))
        )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(userService).createUser(any(SignUpRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserById() throws Exception {
        Long userId = 1L;

        UserResponseDto expectedResponse = new  UserResponseDto(
                userId,
                "username",
                "email@gmail.com",
                "message"
        );

        when(userService.getUserById(userId))
                .thenReturn(expectedResponse);

        mockMvc.perform(
                        get("/api/users/{id}", userId)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(userService).getUserById(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsers() throws Exception {
        UserResponseDto userResponseDto1 = new  UserResponseDto();
        UserResponseDto userResponseDto2 = new  UserResponseDto();

        List<UserResponseDto> expectedResponse = List.of(
                userResponseDto1,
                userResponseDto2
        );

        when(userService.getAllUser())
                .thenReturn(expectedResponse);

        mockMvc.perform(
                        get("/api/users")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(userService).getAllUser();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUser() throws Exception {
        Long userId = 1L;
        UserRequestDto userRequestDto = new UserRequestDto();
        UserResponseDto expectedResponse = new  UserResponseDto();
        expectedResponse.setUsername("username");
        expectedResponse.setId(userId);


        when(userService.updateUser(eq(userId), any(UserRequestDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(
                        put("/api/users")
                                .param("id", userId.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.username").value(expectedResponse.getUsername()));

        verify(userService).updateUser(eq(userId), any(UserRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserById() throws Exception {
        Long userId = 1L;
        mockMvc.perform(
                delete("/api/users")
                        .param("id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userId))
        )
                .andExpect(status().isNoContent());

        verify(userService).deleteById(userId);
    }
}
