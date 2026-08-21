package np.sumit.PersonalExpenseTrackerAPI.service;

import np.sumit.PersonalExpenseTrackerAPI.dto.UserRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.UserResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import np.sumit.PersonalExpenseTrackerAPI.exception.UserNotFoundException;
import np.sumit.PersonalExpenseTrackerAPI.mapper.UserMapper;
import np.sumit.PersonalExpenseTrackerAPI.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = userMapper.toEntity(userRequestDto);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User with id: " + id + " not found")
                );
        return userMapper.toDto(user);
    }

    public List<UserResponseDto> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toDto).toList();
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User with id: " + id + " not found")
                );

        return userMapper.updateAndToResponseDto(userRequestDto, existingUser);
    }

    public void deleteById(Long id) {
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}
