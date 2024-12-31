package cz.stanislavcapek.evidencepd.service.template.exception;

/**
 * An instance of class {@code InvalidFormatXslxExeption}
 *
 * @author Stanislav Čapek
 * @version 1.0
 */
public class InvalidFormatXslxExeption extends RuntimeException {

    public InvalidFormatXslxExeption(String message) {
        super(message);
    }

    public InvalidFormatXslxExeption(String message, Throwable cause) {
        super(message, cause);
    }
}
