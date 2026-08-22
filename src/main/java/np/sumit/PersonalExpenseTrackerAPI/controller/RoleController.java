package np.sumit.PersonalExpenseTrackerAPI.controller;

import jakarta.validation.Valid;
import np.sumit.PersonalExpenseTrackerAPI.dto.request.RoleRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.RoleResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleResponseDto> addRole(@Valid @RequestBody RoleRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id){
        RoleResponseDto responseDto = roleService.getRoleById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getRoles() {
        List<RoleResponseDto> responseDto = roleService.getAllRole();
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping
    public ResponseEntity<RoleResponseDto> updateRole(
            @RequestParam Long id, @RequestBody RoleRequestDto req) {
        RoleResponseDto responseDto = roleService.updateRole(id, req);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteRoleById(@RequestParam Long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.noContent().build();
    }
}
