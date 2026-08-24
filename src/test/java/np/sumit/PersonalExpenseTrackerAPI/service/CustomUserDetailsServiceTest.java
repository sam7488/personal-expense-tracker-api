package np.sumit.PersonalExpenseTrackerAPI.service;

import np.sumit.PersonalExpenseTrackerAPI.entity.CustomUserDetails;
import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import np.sumit.PersonalExpenseTrackerAPI.exception.UsernameNotFoundException;
import np.sumit.PersonalExpenseTrackerAPI.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldThrowExceptionIfUsernameDoesNotExist() {
        String username = "username";
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        UsernameNotFoundException ex =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> customUserDetailsService.loadUserByUsername(username)
                );

        assertEquals("Username " + username + " does not exist", ex.getMessage());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldReturnCustomUserDetailsIfUsernameExists() {
        String username = "username";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        UserDetails actual = customUserDetailsService.loadUserByUsername(username);

        assertInstanceOf(CustomUserDetails.class, actual);
        assertEquals(username, actual.getUsername());

        verify(userRepository).findByUsername(username);
    }

}
