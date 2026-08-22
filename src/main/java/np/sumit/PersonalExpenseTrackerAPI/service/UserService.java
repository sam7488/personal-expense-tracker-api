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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDto createUser(SignUpRequestDto reqDto) {
        if(userRepository.existsByUsername(reqDto.getUsername())) {
            throw new UserNameAlreadyExistsException(
                    "username " + reqDto.getUsername() + " is not available"
            );
        }
        if(userRepository.existsByEmail(reqDto.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "email " + reqDto.getEmail() + " is not available"
            );
        }

        if (reqDto.getUsername().matches(".*\\s+.*")) {
            throw new InvalidUsernameException(
                    "Username cannot contain spaces or special characters. " +
                            "Only letters, numbers, and underscores are allowed."
            );
        }

        User user = userMapper.toEntity(reqDto);
        user.setPassword(passwordEncoder.encode(reqDto.getPassword()));

        Role role = roleRepository.findByRole(ERole.ROLE_USER)
                .orElseThrow(
                        () -> new RoleNotFoundException(
                                "Role : " + ERole.ROLE_USER + " not found"
                        )
                );

        user.getRoles().add(role);

        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    public UserResponseDto getUserByUsernameOrEmail(String username, String email) {
        boolean hasUserName = username != null && !username.isEmpty();
        boolean hasEmail = email != null && !email.isEmpty();
        if(!hasUserName && !hasEmail) {
            throw new UserIdentifierRequiredException(
                    "Username or email is required"
            );
        }

        User user;

        if(hasUserName) {
            user = userRepository.findByUsername(username)
                    .orElseThrow(
                            () -> new UserNameNotFoundException(
                                    "username doesn't exist"
                            )
                    );
            if(hasEmail && !email.equals(user.getEmail())) {
                throw new InvalidIdentifier(
                        "username and email don't match"
                );
            }
        }
        else {
            user = userRepository.findByEmail(email)
                    .orElseThrow(
                            () -> new EmailNotFoundException(
                                    "Email doesn't exist"
                            )
                    );
        }

        return userMapper.toResponseDto(user);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User with id: " + id + " not found")
                );
        return userMapper.toResponseDto(user);
    }

    public List<UserResponseDto> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toResponseDto).toList();
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User with id: " + id + " not found")
                );

        if(userRequestDto.getUsername() != null && !userRepository.existsByUsername(userRequestDto.getUsername())) {
            existingUser.setUsername(userRequestDto.getUsername());
        }
        if(userRequestDto.getEmail() != null && !userRepository.existsByEmail(userRequestDto.getEmail())) {
            existingUser.setEmail(userRequestDto.getEmail());
        }
        if(userRequestDto.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

        return userMapper.toResponseDto(existingUser);
    }

    public void deleteById(Long id) {
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}
