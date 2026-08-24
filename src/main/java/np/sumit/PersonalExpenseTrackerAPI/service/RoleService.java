package np.sumit.PersonalExpenseTrackerAPI.service;

import np.sumit.PersonalExpenseTrackerAPI.dto.request.RoleRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.RoleResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.Role;
import np.sumit.PersonalExpenseTrackerAPI.exception.RoleAlreadyExistsException;
import np.sumit.PersonalExpenseTrackerAPI.exception.RoleNotFoundException;
import np.sumit.PersonalExpenseTrackerAPI.mapper.RoleMapper;
import np.sumit.PersonalExpenseTrackerAPI.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleService(
            RoleRepository roleRepository,
            RoleMapper roleMapper
    ) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public RoleResponseDto createRole(RoleRequestDto requestDto) {
        if(roleRepository.existsByRole(requestDto.getRole())){
            throw new RoleAlreadyExistsException("Role : " + requestDto.getRole() + " already exists");
        }
        Role role  = roleMapper.toEntity(requestDto);
        roleRepository.save(role);
        return roleMapper.toResponseDto(role);
    }

    public RoleResponseDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role for id " + id + " not found"));
        return roleMapper.toResponseDto(role);
    }

    public RoleResponseDto updateRole(Long id, RoleRequestDto requestDto) {
        if(roleRepository.existsByRole(requestDto.getRole())){
            throw new RoleAlreadyExistsException("Role : " + requestDto.getRole() + " already exists");
        }

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role for id " + id + " not found"));

        role.setRole(requestDto.getRole());
        return roleMapper.toResponseDto(role);
    }

    public void deleteRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role for id " + id + " not found"));
        roleRepository.delete(role);
    }

    public List<RoleResponseDto> getAllRole() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toResponseDto).toList();
    }
}
