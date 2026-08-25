package np.sumit.PersonalExpenseTrackerAPI.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username cannot contain spaces or special characters. " +
                    "Only letters, numbers, and underscores are allowed."
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Email
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String password;
}
