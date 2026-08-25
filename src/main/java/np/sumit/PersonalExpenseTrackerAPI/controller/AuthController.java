package np.sumit.PersonalExpenseTrackerAPI.controller;

import jakarta.validation.Valid;
import np.sumit.PersonalExpenseTrackerAPI.dto.request.LoginRequestDto;
import np.sumit.PersonalExpenseTrackerAPI.dto.response.LoginResponseDto;
import np.sumit.PersonalExpenseTrackerAPI.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto respDto =
                authService.authenticate(requestDto.getUsername(), requestDto.getPassword());
        return ResponseEntity.ok(respDto);
    }
}
