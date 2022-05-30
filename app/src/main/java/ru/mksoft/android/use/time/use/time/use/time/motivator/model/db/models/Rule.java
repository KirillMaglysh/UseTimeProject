package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models;

import androidx.annotation.NonNull;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.InvalidDayOfWeekException;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.InvalidTimeLimitException;

import java.util.Map;

/**
 * Class for database table "RULES" representation.
 *
 * @author Kirill
 * @since 18.11.2021
 */
@DatabaseTable(tableName = "RULES")
public class Rule {
    /**
     * RULE_NAME field name
     */
    public static final String FIELD_RULE_NAME = "RULE_NAME";

    /**
     * Неограниченное время (максимальное количество минут в сутках).
     */
    public static final int NO_LIMIT_TIME = 23 * 60 + 59;

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(generatedId = true)
    @Getter
    @Setter
    private Long id;

    @SuppressWarnings("FieldNamingConvention")
    @DatabaseField(columnName = FIELD_RULE_NAME,
            unique = true,
            width = 32,
            index = true,
            canBeNull = false)
    @Getter
    @Setter
    private String name;

    @DatabaseField(columnName = "MONDAY_MINUTES", canBeNull = false)
    private Integer mondayMinutes;

    @DatabaseField(columnName = "TUESDAY_MINUTES", canBeNull = false)
    private Integer tuesdayMinutes;

    @DatabaseField(columnName = "WEDNESDAY_MINUTES", canBeNull = false)
    private Integer wednesdayMinutes;

    @DatabaseField(columnName = "THURSDAY_MINUTES", canBeNull = false)
    private Integer thursdayMinutes;

    @DatabaseField(columnName = "FRIDAY_MINUTES", canBeNull = false)
    private Integer fridayMinutes;

    @DatabaseField(columnName = "SATURDAY_MINUTES", canBeNull = false)
    private Integer saturdayMinutes;

    @DatabaseField(columnName = "SUNDAY_MINUTES", canBeNull = false)
    private Integer sundayMinutes;

    /**
     * Constructor.
     */
    public Rule() {
        setDailySameLimits(NO_LIMIT_TIME);
    }

    /**
     * Sets time limits by day of the week.
     *
     * @param timeLimits time limits by day of the week
     */
    public void setDayLimits(Map<DayOfWeek, Integer> timeLimits) {
        mondayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.MONDAY);
        tuesdayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.TUESDAY);
        wednesdayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.WEDNESDAY);
        thursdayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.THURSDAY);
        fridayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.FRIDAY);
        saturdayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.SATURDAY);
        sundayMinutes = validateTimeLimitInMinutes(timeLimits, DayOfWeek.SUNDAY);
    }

    /**
     * Sets the same time limits for each day.
     *
     * @param timeLimit time limits for each day
     */
    public void setDailySameLimits(@NonNull Integer timeLimit) {
        mondayMinutes = validateTimeLimitInMinutes(timeLimit);
        tuesdayMinutes = validateTimeLimitInMinutes(timeLimit);
        wednesdayMinutes = validateTimeLimitInMinutes(timeLimit);
        thursdayMinutes = validateTimeLimitInMinutes(timeLimit);
        fridayMinutes = validateTimeLimitInMinutes(timeLimit);
        saturdayMinutes = validateTimeLimitInMinutes(timeLimit);
        sundayMinutes = validateTimeLimitInMinutes(timeLimit);
    }

    /**
     * Returns the number of hours in the time limit for a given day of the week.
     *
     * @param dayOfWeek day of the week
     * @return number of hours in the time limit
     */
    public int getHoursLimitTime(@NonNull DayOfWeek dayOfWeek) {
        return getTime(dayOfWeek) / 60;
    }

    /**
     * Returns the number of minutes of the time limit for a given day of the week.
     *
     * @param dayOfWeek day of the week
     * @return number of minutes of the time limit
     */
    public int getMinutesLimitTime(@NonNull DayOfWeek dayOfWeek) {
        return getTime(dayOfWeek) % 60;
    }

    /**
     * Returns the time limit for a specific day of the week in minutes.
     *
     * @param dayOfWeek day of the week
     * @return time limit in minutes
     */
    @NonNull
    public Integer getTime(@NonNull DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return mondayMinutes;
            case TUESDAY:
                return tuesdayMinutes;
            case WEDNESDAY:
                return wednesdayMinutes;
            case THURSDAY:
                return thursdayMinutes;
            case FRIDAY:
                return fridayMinutes;
            case SATURDAY:
                return saturdayMinutes;
            case SUNDAY:
                return sundayMinutes;
            default:
                throw new InvalidDayOfWeekException();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mondayMinutes=" + mondayMinutes +
                ", tuesdayMinutes=" + tuesdayMinutes +
                ", wednesdayMinutes=" + wednesdayMinutes +
                ", thursdayMinutes=" + thursdayMinutes +
                ", fridayMinutes=" + fridayMinutes +
                ", saturdayMinutes=" + saturdayMinutes +
                ", sundayMinutes=" + sundayMinutes +
                '}';
    }

    private static Integer validateTimeLimitInMinutes(Map<DayOfWeek, Integer> timeLimits, DayOfWeek dayOfWeek) {
        return validateTimeLimitInMinutes(timeLimits.get(dayOfWeek));
    }

    private static Integer validateTimeLimitInMinutes(Integer minutes) {
        if (minutes == null || minutes.compareTo(0) < 0 || minutes.compareTo(NO_LIMIT_TIME) > 0) {
            throw new InvalidTimeLimitException(minutes);
        }

        return minutes;
    }

    /**
     * Day of the week.
     */
    public enum DayOfWeek {
        /**
         * Monday. Day number - 0.
         */
        MONDAY(0),
        /**
         * Tuesday. Day number - 1.
         */
        TUESDAY(1),
        /**
         * Wednesday. Day number - 2.
         */
        WEDNESDAY(2),
        /**
         * Thursday. Day number - 3.
         */
        THURSDAY(3),
        /**
         * Пятница. Day number - 4.
         */
        FRIDAY(4),
        /**
         * Saturday. Day number - 5.
         */
        SATURDAY(5),
        /**
         * Sunday. Day number - 6.
         */
        SUNDAY(6);

        private final int dayNumber;

        DayOfWeek(int dayNumber) {
            this.dayNumber = dayNumber;
        }

        /**
         * Returns the number of the day of the week.
         * The week starts on Monday. Number 0!!!!!
         *
         * @return number of the day of the week
         */
        public int dayNumber() {
            return dayNumber;
        }
    }
}
