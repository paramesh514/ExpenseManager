package ve.com.abicelis.creditcardexpensemanager.exceptions;

/**
 * Created by Alex on 3/9/2016.
 */
public class AccountNotFoundException extends Exception {

    public static final String DEFAULT_MESSAGE = "Account does not exist";

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
