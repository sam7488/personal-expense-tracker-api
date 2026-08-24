package np.sumit.PersonalExpenseTrackerAPI.service;

import np.sumit.PersonalExpenseTrackerAPI.dto.request.SignUpRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.request.UserRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.UserResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.ERole;
import np.sumit.PersonalExpenseTrackerAPI.entity.Role;
import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import np.sumit.PersonalExpenseTrackerAPI.exception.*;
import np.sumit.PersonalExpenseTrackerAPI.mapper.UserMapper;
import np.sumit.PersonalExpenseTrackerAPI.repository.RoleRepository;
import np.sumit.PersonalExpenseTrackerAPI.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldThrowExceptionIfUsernameContainsSpecialCharactersOrWhitespace() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("john doe");

        InvalidUsernameException ex =
                assertThrows(
                        InvalidUsernameException.class,
                        () -> userService.createUser(signUpRequestDto)
                );

        assertEquals(
                "Username cannot contain spaces or special characters. " +
                        "Only letters, numbers, and underscores are allowed.",
                ex.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionIfUsernameAlreadyExists() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("random");
        when(userRepository.existsByUsername(signUpRequestDto.getUsername()))
                .thenReturn(true);

        UsernameAlreadyExistsException ex =
                assertThrows(
                        UsernameAlreadyExistsException.class,
                        () -> userService.createUser(signUpRequestDto)
                );

        assertEquals(
                "username " + signUpRequestDto.getUsername() + " is not available",
                ex.getMessage()
        );

        verify(userRepository).existsByUsername(signUpRequestDto.getUsername());
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyExists() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("random");
        signUpRequestDto.setEmail("random@gmail.com");

        when(userRepository.existsByUsername(signUpRequestDto.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(signUpRequestDto.getEmail()))
                .thenReturn(true);

        EmailAlreadyExistsException ex =
                assertThrows(
                        EmailAlreadyExistsException.class,
                        () -> userService.createUser(signUpRequestDto)
                );

        assertEquals(
                "email " + signUpRequestDto.getEmail() + " is already in use",
                ex.getMessage()
        );

        verify(userRepository).existsByUsername(signUpRequestDto.getUsername());
        verify(userRepository).existsByEmail(signUpRequestDto.getEmail());
    }

    @Test
    void shouldCreateUserIfUsernameAndEmailAreUniqueAndUsernameIsValid() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("username");
        signUpRequestDto.setPassword("password");

        User user = new User();
        user.setUsername("username");

        when(userMapper.toEntity(signUpRequestDto))
                .thenReturn(user);

        String encodedPassword = "encoded123password";

        when(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .thenReturn(encodedPassword);

        Role role = new Role(ERole.ROLE_USER);
        user.getRoles().add(role);

        when(roleRepository.findByRole(ERole.ROLE_USER)).thenReturn(Optional.of(role));

        when(userRepository.save(user)).thenReturn(user);

        String message = "User Created Successfully";

        UserResponseDto expected = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                message
        );

        when(userMapper.toResponseDto(user, message))
                .thenReturn(expected);

        UserResponseDto actual = userService.createUser(signUpRequestDto);

        assertEquals(expected, actual);

        verify(userRepository).existsByUsername(signUpRequestDto.getUsername());
        verify(userRepository).existsByEmail(signUpRequestDto.getEmail());
        verify(userMapper).toEntity(signUpRequestDto);
        verify(passwordEncoder).encode(signUpRequestDto.getPassword());
        verify(roleRepository).findByRole(ERole.ROLE_USER);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDto(user, message);
    }

    @Test
    void shouldVerifyPasswordIfPasswordIsCorrect() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUsername("username");
        signUpRequestDto.setPassword("password");

        User user = new User();
        user.setUsername(signUpRequestDto.getUsername());
        user.setPassword(signUpRequestDto.getPassword());

        when(userMapper.toEntity(signUpRequestDto))
                .thenReturn(user);

        String encodedPassword = "encoded123password";

        when(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .thenReturn(encodedPassword);

        Role role = new Role(ERole.ROLE_USER);
        user.getRoles().add(role);

        when(roleRepository.findByRole(ERole.ROLE_USER))
                .thenReturn(Optional.of(role));

        when(userRepository.save(user)).thenReturn(user);

        String message = "User Created Successfully";

        UserResponseDto expected = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                message
        );

        when(userMapper.toResponseDto(user, message))
                .thenReturn(expected);

        UserResponseDto actual = userService.createUser(signUpRequestDto);

        assertEquals(expected, actual);
        assertEquals(encodedPassword, user.getPassword());

        verify(userRepository).existsByUsername(signUpRequestDto.getUsername());
        verify(userRepository).existsByEmail(signUpRequestDto.getEmail());
        verify(userMapper).toEntity(signUpRequestDto);
        verify(passwordEncoder).encode(signUpRequestDto.getPassword());
        verify(roleRepository).findByRole(ERole.ROLE_USER);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDto(user, message);
    }

    @Test
    void shouldThrowExceptionIfNeitherUsernameNorEmailIsProvided() {
        UserIdentifierRequiredException ex =
                assertThrows(
                        UserIdentifierRequiredException.class,
                        () -> userService.getUserByUsernameOrEmail(null, null)
                );

        assertEquals(
                "Username or email is required",
                ex.getMessage()
        );
    }

    @Test
    void shouldReturnUserIfUsernameAndEmailMatch() {
        User user = new  User();
        user.setUsername("username");
        user.setEmail("username@gmail.com");

        when(userRepository.findByUsername(user.getUsername())).
                thenReturn(Optional.of(user));

        String message =  "User Found Successfully";

        UserResponseDto expectedResponse = new UserResponseDto();

        when(userMapper.toResponseDto(user, message))
                .thenReturn(expectedResponse);

        UserResponseDto actual =
                userService.getUserByUsernameOrEmail(user.getUsername(), user.getEmail());

        assertEquals(expectedResponse, actual);

        verify(userRepository).findByUsername(user.getUsername());
        verify(userMapper).toResponseDto(user, message);
    }

    @Test
    void shouldThrowExceptionIfBothUsernameAndEmailProvidedButDoesNotMatch() {
        User user = new  User();
        user.setUsername("username");
        user.setEmail("username@gmail.com");

        when(userRepository.findByUsername(user.getUsername())).
                thenReturn(Optional.of(user));

        InvalidIdentifierException ex =
                assertThrows(
                        InvalidIdentifierException.class,
                        () -> userService.getUserByUsernameOrEmail(
                                user.getUsername(),
                                "wrongemail@gmail.com")
                );

        assertEquals("Username and email don't match", ex.getMessage());
        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void shouldReturnUserIfOnlyUsernameProvidedAndExists() {
        User user = new  User();
        user.setUsername("username");

        when(userRepository.findByUsername(user.getUsername())).
                thenReturn(Optional.of(user));

        String message = "User Found Successfully";

        UserResponseDto expectedResponse = new UserResponseDto();

        when(userMapper.toResponseDto(user, message))
                .thenReturn(expectedResponse);

        UserResponseDto actual =
                userService.getUserByUsernameOrEmail(user.getUsername(), user.getEmail());

        assertEquals(expectedResponse, actual);

        verify(userRepository).findByUsername(user.getUsername());
        verify(userMapper).toResponseDto(user, message);
    }

    @Test
    void ShouldThrowExceptionIfUsernameDoesNotExist() {
        User user = new  User();
        user.setUsername("username");

        when(userRepository.findByUsername(user.getUsername())).
                thenReturn(Optional.empty());

        UsernameNotFoundException ex =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> userService.getUserByUsernameOrEmail(
                                user.getUsername(),
                                user.getEmail()
                        )
                );

        assertEquals("Username doesn't exist", ex.getMessage());

        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void shouldReturnUserIfOnlyEmailProvidedAndExists() {
        User user = new  User();
        user.setEmail("username@gmail.com");

        when(userRepository.findByEmail(user.getEmail())).
                thenReturn(Optional.of(user));

        String message = "User Found Successfully";

        UserResponseDto expectedResponse = new UserResponseDto();

        when(userMapper.toResponseDto(user, message))
                .thenReturn(expectedResponse);

        UserResponseDto actual =
                userService.getUserByUsernameOrEmail(user.getUsername(), user.getEmail());

        assertEquals(expectedResponse, actual);

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper).toResponseDto(user, message);
    }

    @Test
    void ShouldThrowExceptionIfEmailDoesNotExist() {
        User user = new  User();
        user.setEmail("username@gmail.com");

        when(userRepository.findByEmail(user.getEmail())).
                thenReturn(Optional.empty());

        EmailNotFoundException ex =
                assertThrows(
                        EmailNotFoundException.class,
                        () -> userService.getUserByUsernameOrEmail(
                                user.getUsername(),
                                user.getEmail()
                        )
                );

        assertEquals("Email doesn't exist", ex.getMessage());

        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void shouldUpdateUserById() {
        Long userId = 5L;
        UserRequestDto userRequestDto = new UserRequestDto();
        User user = new  User();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        String message = "User Updated Successfully";

        UserResponseDto expectedResponse = new UserResponseDto();

        when(userMapper.toResponseDto(user, message))
                .thenReturn(expectedResponse);

        UserResponseDto actual =
                userService.updateUser(userId, userRequestDto);

        assertEquals(expectedResponse, actual);
        verify(userRepository).findById(userId);
        verify(userRepository).existsByUsername(user.getUsername());
        verify(userRepository).existsByEmail(user.getEmail());
        verify(userMapper).toResponseDto(user, message);
    }

    @Test
    void shouldThrowExceptionIfUserIdDoesNotExist() {
        Long userId = 5L;
        UserRequestDto userRequestDto = new UserRequestDto();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException ex =
                assertThrows(
                        UserNotFoundException.class,
                        () -> userService.updateUser(userId, userRequestDto)
                );

        assertEquals("User with id: 5 not found", ex.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionIfUsernameAlreadyExistsWhenUpdating() {
        Long userId = 5L;
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");

        User user = new  User();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByUsername(userRequestDto.getUsername()))
                .thenReturn(true);

        UsernameAlreadyExistsException ex =
                assertThrows(
                        UsernameAlreadyExistsException.class,
                        () -> userService.updateUser(userId, userRequestDto)
                );
        assertEquals("username " + userRequestDto.getUsername() + " is not available", ex.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository).existsByUsername(userRequestDto.getUsername());
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyExistsWhenUpdating() {
        Long userId = 5L;
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");

        User user = new  User();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByUsername(userRequestDto.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(userRequestDto.getEmail()))
                .thenReturn(true);

        EmailAlreadyExistsException ex =
                assertThrows(
                        EmailAlreadyExistsException.class,
                        () -> userService.updateUser(userId, userRequestDto)
                );
        assertEquals("email " + userRequestDto.getEmail() + " is already in use", ex.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository).existsByUsername(userRequestDto.getUsername());
        verify(userRepository).existsByEmail(userRequestDto.getEmail());
    }

    @Test
    void shouldDeleteUserIfExistsById() {
        Long userId = 1L;
        User user = new  User();
        user.setUsername("username");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void ShouldThrowExceptionIfUserDoesNotExistWhenDeleting() {
        Long userId = 5L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException ex =
                assertThrows(
                        UserNotFoundException.class,
                        () -> userService.deleteById(userId)
                );

        assertEquals("User with id: " + userId + " not found", ex.getMessage());
    }
}
