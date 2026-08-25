package np.sumit.PersonalExpenseTrackerAPI.security;

import np.sumit.PersonalExpenseTrackerAPI.dto.response.LoginResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldAuthenticateAndReturnToken() {
        String username = "username";
        String password = "password";
        String token = "password$token&123";

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        )).thenReturn(authentication);

        when(jwtService.generateToken(authentication)).thenReturn(token);

        LoginResponseDto result = authService.authenticate(username, password);

        assertEquals(token, result.getAccessToken());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(jwtService).generateToken(authentication);
    }

    @Test
    void shouldThrowExceptionIfAuthenticationFails() {
        String username = "username";
        String password = "password";

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        )).thenThrow(new BadCredentialsException("Invalid Credentials"));

        BadCredentialsException exception =
                assertThrows(
                        BadCredentialsException.class,
                        () -> authService.authenticate(username, password)
                );

        assertEquals("Invalid Credentials", exception.getMessage());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }
}
