package np.sumit.PersonalExpenseTrackerAPI.controller;

import jakarta.validation.Valid;
import np.sumit.PersonalExpenseTrackerAPI.dto.request.SignUpRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.request.UserRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.UserResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> addUser(@Valid @RequestBody SignUpRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        UserResponseDto responseDto = userService.getUserById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponseDto> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ) {
        UserResponseDto responseDto = userService.getUserByUsernameOrEmail(
                username,
                email
        );
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        List<UserResponseDto> responseDto = userService.getAllUser();
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
