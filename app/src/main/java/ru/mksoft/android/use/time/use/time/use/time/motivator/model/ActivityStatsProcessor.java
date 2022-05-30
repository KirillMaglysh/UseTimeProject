package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import android.util.Log;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.RequestPackageUsageStatsPermissionListener;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.UserApp;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 24.02.2022
 */
public class ActivityStatsProcessor extends StatsProcessor {
    private static final String LOG_TAG = ActivityStatsProcessor.class.getSimpleName();

    private final RequestPackageUsageStatsPermissionListener updateUseStatsRequestPackageUsageStatsPermissionListener;

    /**
     * Constructor
     *
     * @param activity context of Activity
     */
    public ActivityStatsProcessor(MainActivity activity) {
        super(activity);

        // Using a single listener to prevent duplicating permission requests at application startup
        updateUseStatsRequestPackageUsageStatsPermissionListener = isGranted -> {
            if (isGranted) {
                updateUseStatsGrantedPermission();
            } else {
                Log.w(LOG_TAG, "Package usage stats permission denied");
            }
        };
    }

    /**
     * Updates stats of all app begin with its last update
     */
    public void updateUseStats() {
        ((MainActivity) getContext()).requestPackageUsageStatsPermission(updateUseStatsRequestPackageUsageStatsPermissionListener);
    }

    /**
     * Add application statistic.
     *
     * @param userApp application
     */
    public void addAppStats(UserApp userApp) {
        ((MainActivity) getContext()).requestPackageUsageStatsPermission(isGranted -> {
            if (isGranted) {
                addAppStatsGrantedPermission(userApp);
            } else {
                Log.w(LOG_TAG, "Package usage stats permission denied");
            }
        });
    }
}
