package ru.mksoft.android.use.time.use.time.use.time.motivator.model.alarms.day.results;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessedListener;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessor;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.HourMinuteTime;

import java.sql.SQLException;
import java.util.Locale;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.utils.PermissionUtils.checkUsageStatsPermission;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 05.05.2022
 */
public class DayResultsAlarmReceiver extends BroadcastReceiver implements StatsProcessedListener {
    private static final String LOG_TAG = DayResultsAlarmReceiver.class.getSimpleName();
    private StatsProcessor statsProcessor;
    private Context context;
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        int mode = checkUsageStatsPermission(context);
        Log.d(LOG_TAG, "In onReceive()");

        if (mode == AppOpsManager.MODE_ALLOWED) {
            this.context = context;
            createNextDayAlarm();
            updateData();
        }
    }

    private void updateData() {
        Log.d(LOG_TAG, "In upData()");

        statsProcessor = new StatsProcessor(context);
        statsProcessor.subscribeSystemListener(this);
        new AppListBuilder(context.getPackageManager(), statsProcessor)
                .buildAppList();
    }

    private void makeNotification() {
        Log.d(LOG_TAG, "In makeNotification()");
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String motivatorChannelID = "motivator_channel";
        NotificationChannel myChannel = new NotificationChannel(motivatorChannelID, "MyChannel", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(myChannel);

        NotificationParams params = createNotificationParams();
        //TODO разобраться с notification channel
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, motivatorChannelID)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("Итоги дня")
                        .setContentText(
                                String.format(Locale.getDefault(),
                                        "Время в отслеживаемых приложениях: %s " +
                                                "Целей выполнено: %d из %d",
                                        new HourMinuteTime(params.timeUsed),
                                        params.completedCategoryCount,
                                        params.categoryCount
                                )
                        ).setStyle(new NotificationCompat.InboxStyle()
                                .addLine(String.format(Locale.getDefault(),
                                        "Время в отслеживаемых приложениях: %s ",
                                        new HourMinuteTime(params.timeUsed)
                                ))
                                .addLine(String.format(Locale.getDefault(),
                                        "Целей выполнено: %d из %d",
                                        params.completedCategoryCount,
                                        params.categoryCount
                                )));

        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private NotificationParams createNotificationParams() {
        long timeUsed = statsProcessor.getTodayProgress().getTimeUsed();
        long categoryCount = 0;
        try {
            categoryCount = DbHelperFactory.getHelper().getCategoryDAO().countOfUserCategories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        long completedCategoriesCount = categoryCount - statsProcessor.getTodayProgress().getFailedGoalNumber();

        return new NotificationParams(timeUsed, categoryCount, completedCategoriesCount);
    }

    private void createNextDayAlarm() {
        new DayResultsAlarmService(context).updateAlarm();
    }

    @Override
    public void processStatsUpdated() {
        makeNotification();
    }

    @AllArgsConstructor
    @Getter
    private static class NotificationParams {
        long timeUsed;
        long categoryCount;
        long completedCategoryCount;
    }
}
