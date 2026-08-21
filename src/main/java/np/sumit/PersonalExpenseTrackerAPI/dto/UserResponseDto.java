package np.sumit.PersonalExpenseTrackerAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import np.sumit.PersonalExpenseTrackerAPI.entity.User;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String userName;
    private String email;
    private String password;
    private String message;

    public UserResponseDto(User user, String message) {
        this.id = user.getId();
        this.userName = user.getUsername();
        this.email = user.getEmail();
        this.message = message;
    }
}
