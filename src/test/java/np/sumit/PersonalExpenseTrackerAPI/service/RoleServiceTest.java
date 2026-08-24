package np.sumit.PersonalExpenseTrackerAPI.service;

import np.sumit.PersonalExpenseTrackerAPI.dto.request.RoleRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.RoleResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.entity.ERole;
import np.sumit.PersonalExpenseTrackerAPI.entity.Role;
import np.sumit.PersonalExpenseTrackerAPI.exception.RoleAlreadyExistsException;
import np.sumit.PersonalExpenseTrackerAPI.exception.RoleNotFoundException;
import np.sumit.PersonalExpenseTrackerAPI.mapper.RoleMapper;
import np.sumit.PersonalExpenseTrackerAPI.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    RoleRepository roleRepository;

    @Mock
    RoleMapper roleMapper;

    @InjectMocks
    RoleService roleService;

    @Test
    void shouldThrowExceptionIfRoleAlreadyExists(){
        //arrange
        RoleRequestDto roleRequestDto = new RoleRequestDto(ERole.ROLE_USER);
        when(roleRepository.existsByRole(roleRequestDto.getRole())).thenReturn(true);

        //act
        RoleAlreadyExistsException ex = assertThrows(RoleAlreadyExistsException.class,
                () -> roleService.createRole(roleRequestDto));

        //assert
        assertEquals("Role : " + roleRequestDto.getRole() + " already exists", ex.getMessage());
        verify(roleRepository).existsByRole(roleRequestDto.getRole());
    }

    @Test
    void shouldCreateRoleIfRoleIsUnique() {
        //arrange
        RoleRequestDto roleRequestDto = new RoleRequestDto(ERole.ROLE_USER);
        when(roleRepository.existsByRole(roleRequestDto.getRole()))
                .thenReturn(false);

        Role role = new Role(roleRequestDto.getRole());
        role.setId(1L);

        when(roleMapper.toEntity(roleRequestDto)).thenReturn(role);

        when(roleRepository.save(role))
                .thenReturn(role);

        RoleResponseDto roleResponseDto = new RoleResponseDto(role);

        when(roleMapper.toResponseDto(role))
                .thenReturn(roleResponseDto);

        //act
        RoleResponseDto result = roleService.createRole(roleRequestDto);

        //assert
        verify(roleRepository).existsByRole(roleRequestDto.getRole());
        verify(roleMapper).toEntity(roleRequestDto);
        verify(roleRepository).save(role);
        verify(roleMapper).toResponseDto(role);

        assertEquals(roleResponseDto, result);
    }

    @Test
    void shouldThrowExceptionIfRoleIdDoesNotExist() {
        //arrange
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        //act
        RoleNotFoundException ex = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.getRoleById(roleId)
        );

        //assert
        assertEquals("Role for id " + roleId + " not found", ex.getMessage());

        verify(roleRepository).findById(roleId);
    }

    @Test
    void shouldReturnRoleIfRoleIdExists() {
        Long roleId = 5L;
        Role role = new Role(ERole.ROLE_USER);
        role.setId(roleId);

        when(roleRepository.findById(roleId))
                .thenReturn(Optional.of(role));

        RoleResponseDto roleResponseDto = new RoleResponseDto(role);

        when(roleMapper.toResponseDto(role)).thenReturn(roleResponseDto);

        RoleResponseDto result = roleService.getRoleById(roleId);

        assertEquals(roleResponseDto, result);

        verify(roleRepository).findById(roleId);
        verify(roleMapper).toResponseDto(role);
    }

    @Test
    void shouldThrowExceptionIfRoleAlreadyExistsWhileUpdating() {
        Long roleId = 5L;

        RoleRequestDto roleRequestDto = new RoleRequestDto(ERole.ROLE_USER);
        when(roleRepository.existsByRole(roleRequestDto.getRole())).thenReturn(true);

        RoleAlreadyExistsException ex = assertThrows(RoleAlreadyExistsException.class,
                () -> roleService.updateRole(roleId, roleRequestDto));

        assertEquals("Role : " + roleRequestDto.getRole() + " already exists", ex.getMessage());
        verify(roleRepository).existsByRole(roleRequestDto.getRole());
    }

    @Test
    void shouldThrowExceptionIfRoleIdNotFoundWhileUpdating() {
        Long roleId = 5L;

        RoleRequestDto roleRequestDto = new RoleRequestDto(ERole.ROLE_USER);
        when(roleRepository.existsByRole(roleRequestDto.getRole())).thenReturn(false);

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        RoleNotFoundException ex = assertThrows(RoleNotFoundException.class,
                () -> roleService.updateRole(roleId, roleRequestDto));

        assertEquals("Role for id " + roleId + " not found", ex.getMessage());
        verify(roleRepository).existsByRole(roleRequestDto.getRole());
        verify(roleRepository).findById(roleId);
    }

    @Test
    void shouldUpdateRoleIfNewRoleIsUnique() {
        Long roleId = 2L;

        RoleRequestDto roleRequestDto = new RoleRequestDto(ERole.ROLE_USER);
        when(roleRepository.existsByRole(roleRequestDto.getRole())).thenReturn(false);

        Role role = new Role(roleRequestDto.getRole());
        role.setId(roleId);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        RoleResponseDto roleResponseDto = new RoleResponseDto(role);
        when(roleMapper.toResponseDto(role)).thenReturn(roleResponseDto);

        RoleResponseDto result = roleService.updateRole(roleId, roleRequestDto);

        verify(roleRepository).existsByRole(roleRequestDto.getRole());
        verify(roleRepository).findById(roleId);
        verify(roleMapper).toResponseDto(role);

        assertEquals(roleResponseDto, result);
    }

    @Test
    void shouldThrowExceptionIfRoleIdDoesNotExistsWhenDeleting() {
        Long roleId = 10L;

        when(roleRepository.findById(roleId))
                .thenReturn(Optional.empty());

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        RoleNotFoundException ex = assertThrows(
                RoleNotFoundException.class,
                () -> roleService.deleteRoleById(roleId)
        );

        assertEquals("Role for id " + roleId + " not found", ex.getMessage());

        verify(roleRepository).findById(roleId);
    }

    @Test
    void shouldDeleteIfRoleIdExists() {
        Long roleId = 10L;
        Role role = new Role(ERole.ROLE_USER);
        role.setId(roleId);

        when(roleRepository.findById(roleId))
                .thenReturn(Optional.of(role));

        roleService.deleteRoleById(roleId);

        verify(roleRepository).findById(roleId);
        verify(roleRepository).delete(role);
    }

    @Test
    void shouldReturnListOfRoles() {
        Role role1 = new Role(ERole.ROLE_USER);
        role1.setId(1L);

        Role role2 = new Role(ERole.ROLE_ADMIN);
        role2.setId(2L);

        when(roleRepository.findAll())
                .thenReturn(Arrays.asList(role1, role2));

        RoleResponseDto roleResponseDto1 = new RoleResponseDto(role1);
        RoleResponseDto roleResponseDto2 = new RoleResponseDto(role2);

        when(roleMapper.toResponseDto(role1)).thenReturn(roleResponseDto1);
        when(roleMapper.toResponseDto(role2)).thenReturn(roleResponseDto2);

        List<RoleResponseDto> expected = Arrays.asList(
                roleResponseDto1,
                roleResponseDto2
        );

        List<RoleResponseDto> result = roleService.getAllRole();

        assertEquals(expected, result);

        verify(roleRepository).findAll();
        verify(roleMapper).toResponseDto(role1);
        verify(roleMapper).toResponseDto(role2);
    }
}
