package np.sumit.PersonalExpenseTrackerAPI.controller;

import np.sumit.PersonalExpenseTrackerAPI.dto.UserRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.UserResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> addUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto responseDto = userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> addUser() {
        List<UserResponseDto> responseDto = userService.getAllUser();
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        UserResponseDto responseDto = userService.getUserById(id);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> updateUser(
            @RequestParam Long id, @RequestBody UserRequestDto req) {
        UserResponseDto responseDto = userService.updateUser(id, req);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteUserById(@RequestParam Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
