package np.sumit.PersonalExpenseTrackerAPI.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username cannot contain spaces or special characters. " +
                    "Only letters, numbers, and underscores are allowed."
    )
    private String username;

    @NotBlank
    @Email
    @Size(min = 8, max = 50)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String email;
}
