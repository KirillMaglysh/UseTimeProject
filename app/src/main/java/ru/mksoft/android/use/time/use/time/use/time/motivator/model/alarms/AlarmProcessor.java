package ru.mksoft.android.use.time.use.time.use.time.motivator.model.alarms;

import android.content.Context;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.alarms.day.results.DayResultsAlarmService;

import java.util.ArrayList;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 05.05.2022
 */
public class AlarmProcessor {
    private List<AlarmService> alarmServices;

    public AlarmProcessor(Context context) {
        alarmServices = new ArrayList<>();
        alarmServices.add(new DayResultsAlarmService(context));
    }

    public void updateAlarms() {
        new Thread(
                () -> {
                    for (AlarmService alarmService : alarmServices) {
                        alarmService.updateAlarm();
                    }
                }
        ).start();
    }
}
