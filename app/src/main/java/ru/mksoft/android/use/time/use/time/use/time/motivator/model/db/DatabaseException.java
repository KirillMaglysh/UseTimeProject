package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db;

/**
 * Database error.
 *
 * @author Kirill
 * @since 26.02.2022
 */
public class DatabaseException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Database error";

    /**
     * Constructor.
     *
     * @param cause error cause
     */
    public DatabaseException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }
}
