package np.sumit.PersonalExpenseTrackerAPI.mapper;

import np.sumit.PersonalExpenseTrackerAPI.dto.request.RoleRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.RoleResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public Role toEntity(RoleRequestDto req) {
        Role role = new Role();
        role.setRole(req.getRole());
        return role;
    }

    public RoleResponseDto toResponseDto(Role role) {
        return new RoleResponseDto(role);
    }
}
