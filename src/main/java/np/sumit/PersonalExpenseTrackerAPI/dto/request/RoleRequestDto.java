package np.sumit.PersonalExpenseTrackerAPI.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import np.sumit.PersonalExpenseTrackerAPI.entity.ERole;

@Getter
@AllArgsConstructor
public class RoleRequestDto {
    @NotNull
    private ERole role;
}
