package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.03.2022
 */
@AllArgsConstructor
@Getter
@Setter
public class DayProgress {
    private int failedGoalNumber;
    private long timeUsed;

    /**
     * Adds how much you want to timeUsed
     *
     * @param add how much you want to add to timeUsed
     */
    public void increaseTimeUsed(long add) {
        timeUsed += add;
    }

    /**
     * Adds how much you want to failedGoalNumber
     *
     * @param add how much you want to add to timeUsed
     */
    public void increaseFailedGoalNumber(long add) {
        failedGoalNumber += add;
    }
}
