package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao;

import java.util.Locale;

/**
 * Invalid time limit value error.
 *
 * @author Kirill
 * @since 25.02.2022
 */
public class InvalidTimeLimitException extends IllegalArgumentException {
    private static final String ERROR_MESSAGE = "Invalid time limit value: %02d:%02d";
    private static final String ERROR_MESSAGE_MINUTES = "Invalid time limit value: %d minutes";

    /**
     * Constructor.
     *
     * @param hours   number of hours
     * @param minutes number of minutes
     */
    public InvalidTimeLimitException(Integer hours, Integer minutes) {
        super(String.format(Locale.US, ERROR_MESSAGE, hours, minutes));
    }

    /**
     * Constructor.
     *
     * @param minutes time limit in minutes
     */
    public InvalidTimeLimitException(Integer minutes) {
        super(String.format(Locale.US, ERROR_MESSAGE_MINUTES, minutes));
    }
}
