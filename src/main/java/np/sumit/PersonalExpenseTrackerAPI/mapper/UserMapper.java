package np.sumit.PersonalExpenseTrackerAPI.mapper;

import np.sumit.PersonalExpenseTrackerAPI.dto.request.SignUpRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.UserResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(SignUpRequestDto req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        return user;
    }

    public UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(user, "User Added Successfully");
    }
}
