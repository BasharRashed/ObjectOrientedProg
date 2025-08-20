package exceptions;

/**
 * Custom exception class for ASCII art related errors.
 */
public class AsciiArtException extends RuntimeException {

    /**
     * Constructs a new AsciiArtException with the specified detail message.
     * 
     * @param message the detail message
     */
    public AsciiArtException(String message) {
        super(message);
    }

    /**
     * Constructs a new AsciiArtException with the specified detail message and
     * cause.
     * 
     * @param message the detail message
     * @param cause   the cause
     */
    public AsciiArtException(String message, Throwable cause) {
        super(message, cause);
    }
}