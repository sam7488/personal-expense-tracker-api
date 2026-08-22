package np.sumit.PersonalExpenseTrackerAPI.exception;

public class UserIdentifierRequiredException extends RuntimeException {
    public UserIdentifierRequiredException(String message) {
        super(message);
    }
}
