package ru.mksoft.android.use.time.use.time.use.time.motivator.utils;

import lombok.Getter;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 07.05.2022
 */
public class HourMinuteTime {
    @Getter
    int hours;
    @Getter
    int minutes;

    public HourMinuteTime(long timeInMillis) {
        minutes = (int) (timeInMillis / DateTimeUtils.MILLIS_IN_MINUTE);
        hours = minutes / 60;
        minutes %= 60;
    }

    @Override
    public String toString() {
        return hours + " ч " + minutes + " м";
    }
}
