package np.sumit.PersonalExpenseTrackerAPI.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private int status;
    private Instant timestamp;
}
