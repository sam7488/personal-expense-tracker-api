package np.sumit.PersonalExpenseTrackerAPI.controller;

import np.sumit.PersonalExpenseTrackerAPI.dto.request.LoginRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.LoginResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.security.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldThrowExceptionIfUsernameIsInvalid() throws Exception {
        String password = "test";

        LoginRequestDto loginRequestDto = new LoginRequestDto(null, password);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankUsername() throws Exception {
        String password = "test@password";

        LoginRequestDto loginRequestDto = new LoginRequestDto(null, password);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username is required"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRejectInvalidUsername() throws Exception {
        String username = "test&8";
        String password = "test@password";

        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Username cannot contain spaces or special characters. " +
                                "Only letters, numbers, and underscores are allowed."
                ))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(authService);
    }

    @ParameterizedTest
    @MethodSource("invalidUsernames")
    void shouldRejectInvalidUsernameLength(String username) throws Exception {
        String password = "test@password";

        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Username must be between 3 and 50 characters"))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(authService);
    }

    static Stream<String> invalidUsernames() {
        return Stream.of(
                "aa",
                "a".repeat(51)
        );
    }

    @ParameterizedTest
    @MethodSource("usernameWithMinAndMaxAllowedLen")
    void shouldAcceptUsernameWithMinimumAndMaximumAllowedLength(String username) throws Exception {
        String password = "username@password";

        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        LoginResponseDto respDto = new LoginResponseDto("expected-token");
        when(authService.authenticate(username, password))
                .thenReturn(respDto);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("expected-token"));

        verify(authService).authenticate(username, password);
    }

    static Stream<String> usernameWithMinAndMaxAllowedLen() {
        return Stream.of(
                "aaa",
                "a".repeat(50)
        );
    }

    @Test
    void shouldRejectBlankPassword() throws Exception {
        String username = "username";

        LoginRequestDto loginRequestDto = new LoginRequestDto(username, null);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Password is required"))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(authService);
    }


    @ParameterizedTest
    @MethodSource("invalidPasswordLength")
    void shouldRejectInvalidPasswordLength(String password) throws Exception {
        String username = "username";

        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Password must be between 8 and 50 characters"))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(authService);
    }

    static Stream<String> invalidPasswordLength() {
        return Stream.of(
                "p".repeat(7),
                "p".repeat(51)
        );
    }

    @ParameterizedTest
    @MethodSource("validPasswordWithMinAndMaxLength")
    void shouldAcceptValidPasswordWithMinimumAndMaximumLength(String password) throws Exception {
        String username = "username";

        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        LoginResponseDto respDto = new LoginResponseDto("expected-token");

        when(authService.authenticate(username, password))
                .thenReturn(respDto);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("expected-token"));

        verify(authService).authenticate(username, password);
    }

    static Stream<String> validPasswordWithMinAndMaxLength() {
        return Stream.of(
                "p".repeat(8),
                "p".repeat(50)
        );
    }


    @Test
    void shouldRejectInvalidIdentifier() throws Exception {
        String username = "test";
        String password = "test@password";
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        when(authService.authenticate(username, password))
                .thenThrow(new BadCredentialsException("Invalid Credentials"));

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid Credentials"))
                .andExpect(jsonPath("$.code").value("INVALID_USERNAME_OR_PASSWORD"));

        verify(authService).authenticate(username, password);
    }

    @Test
    void shouldAcceptValidIdentifier() throws Exception {
        String username = "username";
        String password = "test@password";

        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

        when(authService.authenticate(username, password))
                .thenReturn(new LoginResponseDto("expected-token"));

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("expected-token"));

        verify(authService).authenticate(username, password);
    }
}
