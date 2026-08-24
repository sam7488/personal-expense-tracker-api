package np.sumit.PersonalExpenseTrackerAPI.exception;

public class InvalidIdentifierException extends RuntimeException {
    public InvalidIdentifierException(String message) {
        super(message);
    }
}
