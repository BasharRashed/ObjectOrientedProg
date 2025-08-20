package exceptions;

/**
 * Exception thrown when there are errors during image processing operations.
 */
public class ImageProcessingException extends AsciiArtException {

    /**
     * Constructs a new ImageProcessingException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ImageProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs a new ImageProcessingException with the specified detail message
     * and cause.
     * 
     * @param message the detail message
     * @param cause   the cause
     */
    public ImageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}