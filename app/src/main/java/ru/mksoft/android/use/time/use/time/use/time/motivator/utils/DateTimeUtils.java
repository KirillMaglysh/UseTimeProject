package ru.mksoft.android.use.time.use.time.use.time.motivator.utils;

import android.widget.TextView;
import androidx.annotation.NonNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Rule;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utils for work with date and time.
 *
 * @author Kirill
 * @since 26.02.2022
 */
public class DateTimeUtils {

    /**
     * Labels of days of the week in russian
     * TODO change for localized resources
     */
    public static final String[] DAY_LABELS_RU = {"ПН", " ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};

    /**
     * Number of milliseconds in one minute
     */
    public static final int MILLIS_IN_MINUTE = 60000;
    private static final String DAY_TIME_LIMIT_FORMAT = "%02d:%02d";
    private static final String DATE_DAY_MONTH_FORMAT = "%02d.%02d";
    private static final String TIME_PART_FORMAT = "%02d";

    /**
     * Returns formatted time value in minutes
     *
     * @param time time in minutes
     * @return string in format "HH:mm"
     */
    public static String getFormattedMinutesTime(int time) {
        return getFormattedHoursMinutesTime(time / 60, time % 60);
    }

    /**
     * Returns formatted time value.
     *
     * @param hours   time in hours
     * @param minutes time in minutes
     * @return string in format "HH:mm"
     */
    public static String getFormattedHoursMinutesTime(int hours, int minutes) {
        return String.format(Locale.ENGLISH, DAY_TIME_LIMIT_FORMAT, hours, minutes);
    }

    /**
     * Returns formatted rule time limit for specific day of the week.
     *
     * @param rule      rule
     * @param dayOfWeek day of week
     * @return string in format "HH:mm"
     */
    public static String getFormattedLimitTime(@NonNull Rule rule, @NonNull Rule.DayOfWeek dayOfWeek) {
        return getFormattedMinutesTime(rule.getTime(dayOfWeek));
    }

    /**
     * Returns string value of the date with day of week.
     *
     * @param calendar date which formatted string value you need
     * @return string value of the date with day of week
     */
    public static String getFormattedDateWithDayOfWeek(Calendar calendar) {
        // TODO переделать на ресурсы с переводом
        return DAY_LABELS_RU[getDayOfWeek(calendar)] + " " + String.format(Locale.getDefault(), DATE_DAY_MONTH_FORMAT, calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1);
    }

    /**
     * Returns number of day of week of your date
     *
     * @param calendar calendar pointing at date which day of week you want to know
     * @return number of day of week of your date
     */
    public static int getDayOfWeek(Calendar calendar) {
//        return LocalDate.from(Instant.ofEpochMilli(calendar.getTimeInMillis())).get(ChronoField.DAY_OF_WEEK);
        // TODO() переделать на локализацию, попробовать использовать enum из Rule
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 6 : calendar.get(Calendar.DAY_OF_WEEK) - 2;
    }

    /**
     * Returns number of day of week of your date
     *
     * @param date date which day of week you want to know
     * @return number of day of week of your date
     */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getDayOfWeek(calendar);
    }

    public static int getCurrentDayOFWeek() {
        return getDayOfWeek(getDateOfCurrentDayBegin());
    }

    /**
     * Parse string time value (string in format "HH:mm").
     *
     * @param time time value in string format
     * @return minutes number
     */
    public static int parseHoursMinutesTime(@NonNull CharSequence time) {
        int hours = Integer.parseInt(String.valueOf(time.subSequence(0, 2)));
        int minutes = Integer.parseInt(String.valueOf(time.subSequence(3, 5)));
        return hours * 60 + minutes;
    }

    /**
     * Parse value of time input field (string in format "HH:mm").
     *
     * @param timeField time input field
     * @return minutes value
     */
    public static int parseTimeFieldValue(@NonNull TextView timeField) {
        return parseHoursMinutesTime(timeField.getText());
    }

    /**
     * Returns begin of current date
     *
     * @return current date begin
     */
    public static Date getDateOfCurrentDayBegin() {
        Instant instant = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * Returns Date of begin of the chosen day begin
     *
     * @param diff diff of date with today
     * @return Date of begin of the chosen day begin
     */
    public static Date getDateOtherDayBegin(int diff) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDateOfCurrentDayBegin());
        calendar.add(Calendar.DATE, diff);

        return calendar.getTime();
    }

    public static long getCurDayTimeInMillis(){
        return Calendar.getInstance().getTimeInMillis() - DateTimeUtils.getDateOfCurrentDayBegin().getTime();
    }
}
