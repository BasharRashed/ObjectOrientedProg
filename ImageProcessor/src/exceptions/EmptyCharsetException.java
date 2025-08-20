package exceptions;

/**
 * Exception thrown when attempting to use an empty charset in ASCII art
 * generation.
 */
public class EmptyCharsetException extends AsciiArtException {

    /**
     * Constructs a new EmptyCharsetException with a default message.
     */
    public EmptyCharsetException() {
        super("No characters available in the charset.");
    }

    /**
     * Constructs a new EmptyCharsetException with the specified detail message.
     * 
     * @param message the detail message
     */
    public EmptyCharsetException(String message) {
        super(message);
    }
}