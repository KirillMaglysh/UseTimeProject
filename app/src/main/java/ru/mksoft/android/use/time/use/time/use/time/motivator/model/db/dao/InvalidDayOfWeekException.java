package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao;

/**
 * Day of the week cannot be empty error.
 *
 * @author Kirill
 * @since 25.02.2022
 */
public class InvalidDayOfWeekException extends NullPointerException {
    private static final String ERROR_MESSAGE = "Day of the week cannot be null";

    /**
     * Constructor.
     */
    public InvalidDayOfWeekException() {
        super(ERROR_MESSAGE);
    }
}
