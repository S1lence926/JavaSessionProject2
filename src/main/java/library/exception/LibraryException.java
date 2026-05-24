package library.exception;

/**
 * Доменное исключение библиотечной системы.
 * Используется в сервисном слое для бизнес-ошибок.
 */
public class LibraryException extends RuntimeException {

    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
