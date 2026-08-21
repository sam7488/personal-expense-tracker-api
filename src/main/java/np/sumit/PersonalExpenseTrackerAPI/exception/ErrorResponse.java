package np.sumit.PersonalExpenseTrackerAPI.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
}
