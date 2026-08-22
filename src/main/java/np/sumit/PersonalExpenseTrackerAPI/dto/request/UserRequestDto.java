package np.sumit.PersonalExpenseTrackerAPI.dto.request;

import lombok.Getter;

@Getter
public class UserRequestDto {
    private String username;
    private String password;
    private String email;
}
