package np.sumit.PersonalExpenseTrackerAPI.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import np.sumit.PersonalExpenseTrackerAPI.entity.ERole;

@Getter
public class RoleRequestDto {
    @NotNull
    private ERole role;
}
