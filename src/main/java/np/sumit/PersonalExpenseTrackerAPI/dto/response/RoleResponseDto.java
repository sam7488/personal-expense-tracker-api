package np.sumit.PersonalExpenseTrackerAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import np.sumit.PersonalExpenseTrackerAPI.entity.ERole;
import np.sumit.PersonalExpenseTrackerAPI.entity.Role;

@Getter
@Setter
@AllArgsConstructor
public class RoleResponseDto {
    private Long id;
    private ERole role;

    public RoleResponseDto(Role role) {
        this.id = role.getId();
        this.role = role.getRole();
    }
}
