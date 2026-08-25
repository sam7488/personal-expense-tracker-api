package np.sumit.PersonalExpenseTrackerAPI.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "id",
        "username",
        "email",
        "message"
})
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String message;
}
