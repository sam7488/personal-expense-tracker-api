package np.sumit.PersonalExpenseTrackerAPI.mapper;

import np.sumit.PersonalExpenseTrackerAPI.dto.UserRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.UserResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequestDto req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setEmail(req.getEmail());
        return user;
    }

    public UserResponseDto toDto(User user) {
        return new UserResponseDto(user, "User Added Successfully");
    }

    public UserResponseDto updateAndToResponseDto(UserRequestDto req, User user) {
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setEmail(req.getEmail());
        UserResponseDto responseDto = new UserResponseDto(user, "User Updated Successfully");

        responseDto.setMessage("User Updated successfully");

        return responseDto;
    }
}
