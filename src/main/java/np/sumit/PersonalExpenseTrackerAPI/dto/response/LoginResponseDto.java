package np.sumit.PersonalExpenseTrackerAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class LoginResponseDto {
    private String accessToken;
}
