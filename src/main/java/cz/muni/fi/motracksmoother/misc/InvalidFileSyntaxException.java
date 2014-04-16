package cz.muni.fi.motracksmoother.misc;

/**
 * Custom exception for all types of invalid syntax errors in a processing file.
 * 
 * @author Tomas Smetanka
 * @version 1.0
 * @since 1.0
 */
public class InvalidFileSyntaxException extends Exception {

    private String message = null;

    public InvalidFileSyntaxException() {
        super();
    }

    public InvalidFileSyntaxException(String message) {
        super(message);
        this.message = message;
    }

    public InvalidFileSyntaxException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
