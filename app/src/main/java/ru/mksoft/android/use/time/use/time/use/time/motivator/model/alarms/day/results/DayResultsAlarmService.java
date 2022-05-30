package ru.mksoft.android.use.time.use.time.use.time.motivator.model.alarms.day.results;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.alarms.AlarmService;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.util.Calendar;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 05.05.2022
 */
public class DayResultsAlarmService implements AlarmService {
    private final Context context;
    private final AlarmManager alarmManager;
    // TODO: перенести в настройки
    private static final long DAY_ALARM_TIME = (21 * 3600 + 00 * 60) * 1000;
    private final long absoluteAlarmTime;
    private static final int DAY_RESULTS_ALARM_REQUEST_CODE = 798;

    public DayResultsAlarmService(Context context) {
        long curDayTime = DateTimeUtils.getCurDayTimeInMillis();
        if (curDayTime > DAY_ALARM_TIME) {
            absoluteAlarmTime = DateTimeUtils.getDateOtherDayBegin(1).getTime() + DAY_ALARM_TIME;
        } else {
            absoluteAlarmTime = DateTimeUtils.getDateOfCurrentDayBegin().getTime() + DAY_ALARM_TIME;
        }
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void updateAlarm() {
        setRepetitiveAlarm(absoluteAlarmTime);
    }

    private void setRepetitiveAlarm(long timeInMillis) {
        setAlarm(getPendingIntent(getIntent()), timeInMillis);
    }

    private void setAlarm(PendingIntent pendingIntent, long timeInMillis) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }

    public void cancelAlarm() {

    }

    private void cancelPrevRepetitiveAlarm(long timeInMillis) {

    }

    private void cancelAlarm(PendingIntent pendingIntent) {
        alarmManager.cancel(pendingIntent);
    }

    private PendingIntent getPendingIntent(Intent intent) {
        return PendingIntent.getBroadcast(context, DAY_RESULTS_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getIntent() {
        return new Intent(context, DayResultsAlarmReceiver.class);
    }
}
