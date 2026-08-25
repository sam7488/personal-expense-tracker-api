package np.sumit.PersonalExpenseTrackerAPI.security;

import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import np.sumit.PersonalExpenseTrackerAPI.exception.UserNotFoundException;
import np.sumit.PersonalExpenseTrackerAPI.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrentUserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowExceptionIfCurrentUserDoesNotExist() {
        String username = "john";

        when(authentication.getName())
                .thenReturn(username);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> currentUserService.getCurrentUser()
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldReturnCurrentUser() {
        User user = new User();
        user.setUsername("john");

        when(authentication.getName())
                .thenReturn(user.getUsername());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        User actual = currentUserService.getCurrentUser();

        assertEquals(user, actual);

        verify(userRepository).findByUsername(user.getUsername());
    }
}
