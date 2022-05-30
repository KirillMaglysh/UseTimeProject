package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.AppUseStats;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Property;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.UserApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.PermissionUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 24.02.2022
 */
public class StatsProcessor {
    private static final String LOG_TAG = StatsProcessor.class.getSimpleName();

    private final Context context;
    private StatsProcessedListener uiListener;
    private final List<StatsProcessedListener> updateStatisticResultsListeners;
    private boolean isNewDate = false;
    private boolean isProcessed;

    @Getter
    private DayProgress todayProgress = new DayProgress(0, 0);
    @Getter
    private final DayProgress yesterdayProgress = new DayProgress(0, 0);

    /**
     * Constructor
     *
     * @param context context of Activity
     */
    public StatsProcessor(Context context) {
        this.context = context;
        updateStatisticResultsListeners = new ArrayList<>();
    }

    protected Context getContext() {
        return context;
    }

    /**
     * Updates stats of all app begin with its last update
     */
    public void updateUseStats() {
        if (PermissionUtils.isUsageStatsPermissionAllowed(context)) {
            updateUseStatsGrantedPermission();
        }
    }

    protected void updateUseStatsGrantedPermission() {
        isProcessed = false;
        todayProgress = new DayProgress(0, 0);
        List<UserApp> trackedUserApps = getTrackedUserApps();
        if (trackedUserApps.isEmpty()) {
            notifyStatsProcessed();
            return;
        }

        AppListParseResults appListParseResults = parseAppList(trackedUserApps);
        TreeMap<String, UserApp> userAppMap = appListParseResults.getUserAppMap();

        int strikeChange = 0;
        Calendar nextDate = Calendar.getInstance();
        Date today = DateTimeUtils.getDateOfCurrentDayBegin();
        if (appListParseResults.minDate.before(today)) {
            isNewDate = true;
        }

        for (Date curDate = appListParseResults.getMinDate(); curDate.before(today) || curDate.equals(today); ) {
            nextDate.setTime(curDate);
            nextDate.add(Calendar.DATE, 1);

            strikeChange += processDateStats(userAppMap, nextDate, curDate);

            curDate.setTime(nextDate.getTimeInMillis());
        }

        setUserAppsLastUpdate(trackedUserApps, today);
        try {
            DbHelperFactory.getHelper().getPropertyDAO().updateStrike(strikeChange);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateYesterdayParams();
        notifyStatsProcessed();
    }

    protected void updateYesterdayParams() {
        Property yesterdayTime = null;
        Property yesterdayGoalsFailed = null;
        try {
            yesterdayTime = DbHelperFactory.getHelper().getPropertyDAO().queryForId(Property.YESTERDAY_USED_TIME_FIELD_ID);
            yesterdayGoalsFailed = DbHelperFactory.getHelper().getPropertyDAO().queryForId(Property.YESTERDAY_FAILED_GOALS_NUMBER_FIELD_ID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (isNewDate) {
            yesterdayTime.setValue(yesterdayProgress.getTimeUsed());
            yesterdayGoalsFailed.setValue((long) yesterdayProgress.getFailedGoalNumber());

            try {
                DbHelperFactory.getHelper().getPropertyDAO().update(yesterdayTime);
                DbHelperFactory.getHelper().getPropertyDAO().update(yesterdayGoalsFailed);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            yesterdayProgress.setFailedGoalNumber(Math.toIntExact(yesterdayGoalsFailed.getValue()));
            yesterdayProgress.setTimeUsed(yesterdayTime.getValue());
        }
    }

    private void notifyStatsProcessed() {
        Log.d(LOG_TAG, "Stats Processed");
        isProcessed = true;
        publishUIListener();
        publishSystemListeners();
    }

    private static void setUserAppsLastUpdate(List<UserApp> trackedUserApps, Date today) {
        for (UserApp trackedUserApp : trackedUserApps) {
            trackedUserApp.setLastUpdateDate(today);
            try {
                DbHelperFactory.getHelper().getUserAppDAO().update(trackedUserApp);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int processDateStats(TreeMap<String, UserApp> userAppMap, Calendar nextDate, Date curDate) {
        HashMap<Category, Long> usedCategoriesStats = new HashMap<>();

        for (UsageStats usageStat : queryUsageStats(curDate, nextDate)) {
            UserApp userApp = userAppMap.get(usageStat.getPackageName());
            if (userApp != null) {
                long usedTime = usageStat.getTotalTimeVisible();
                if (usedCategoriesStats.containsKey(userApp.getCategory())) {
                    usedCategoriesStats.replace(userApp.getCategory(), usedCategoriesStats.get(userApp.getCategory()) + usedTime);
                } else {
                    usedCategoriesStats.put(userApp.getCategory(), usedTime);
                }

                updateAppUseStatsForDate(curDate, usedTime, userApp);
                userAppMap.remove(userApp.getPackageName());
            }
        }

        processUnusedAppsForDate(userAppMap, curDate);

        return processCategoriesDateStats(usedCategoriesStats, curDate);
    }

    private int processCategoriesDateStats(HashMap<Category, Long> usedCategoriesStats, Date date) {
        int strikeChange = 0;

        Set<Map.Entry<Category, Long>> entries = usedCategoriesStats.entrySet();
        for (Map.Entry<Category, Long> entry : entries) {
            strikeChange += processCategoryDateState(entry.getKey(), entry.getValue(), date);
        }

        if (date.equals(DateTimeUtils.getDateOfCurrentDayBegin())) {
            return 0;
        }

        return strikeChange;
    }

    private int processCategoryDateState(Category category, Long usedTime, Date date) {
        boolean dayGoalCompleted = category.isDayGoalCompleted(date, usedTime);

        if (date.after(DateTimeUtils.getDateOtherDayBegin(-2))) {
            if (date.equals(DateTimeUtils.getDateOfCurrentDayBegin())) {
                todayProgress.increaseTimeUsed(usedTime);
                if (!dayGoalCompleted) {
                    todayProgress.increaseFailedGoalNumber(1);
                }
            } else {
                yesterdayProgress.increaseTimeUsed(usedTime);
                if (!dayGoalCompleted) {
                    yesterdayProgress.increaseFailedGoalNumber(1);
                }
            }
        }

        return dayGoalCompleted ? 1 : -1;
    }

    private static void processUnusedAppsForDate(TreeMap<String, UserApp> userAppMap, Date curDate) {
        for (Map.Entry<String, UserApp> entry : userAppMap.entrySet()) {
            updateAppUseStatsForDate(curDate, 0, entry.getValue());
        }
    }

    private static void updateAppUseStatsForDate(Date curDate, long totalTimeVisible, UserApp userApp) {
        if (curDate.equals(userApp.getLastUpdateDate())) {
            try {
                DbHelperFactory.getHelper().getAppUseStatsDao().removeAppStatsPerDay(userApp, curDate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (curDate.after(userApp.getLastUpdateDate()) || curDate.equals(userApp.getLastUpdateDate())) {
            try {
                DbHelperFactory.getHelper().getAppUseStatsDao().create(createAppUseStatsForDate(curDate, totalTimeVisible, userApp));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private List<UsageStats> queryUsageStats(Date curDate, Calendar nextDate) {
        return ((UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE))
//                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis() - 30 * 1000, System.currentTimeMillis());
                .queryUsageStats(UsageStatsManager.INTERVAL_BEST, curDate.getTime(), nextDate.getTimeInMillis());
    }

    @NotNull
    private static AppUseStats createAppUseStatsForDate(Date curDate, long totalTimeVisible, UserApp userApp) {
        AppUseStats dbUseStats = new AppUseStats();
        dbUseStats.setDate(curDate);
        dbUseStats.setUsageTime(totalTimeVisible);
        dbUseStats.setDate(curDate);
        dbUseStats.setUserApp(userApp);

        return dbUseStats;
    }

    @NotNull
    private static List<UserApp> getTrackedUserApps() {
        List<UserApp> userApps = new ArrayList<>();
        try {
            userApps = DbHelperFactory.getHelper().getUserAppDAO().getAllTrackedApps();
        } catch (SQLException e) {
            //TODO: корректно обработать ошибку
            e.printStackTrace();
        }

        return userApps;
    }

    private static AppListParseResults parseAppList(List<UserApp> userApps) {
        TreeMap<String, UserApp> userAppMap = new TreeMap<>();
        Date minDate = DateTimeUtils.getDateOfCurrentDayBegin();
        for (UserApp userApp : userApps) {
            userAppMap.put(userApp.getPackageName(), userApp);
            if (minDate.after(userApp.getLastUpdateDate())) {
                minDate = userApp.getLastUpdateDate();
            }
        }

        return new AppListParseResults(userAppMap, minDate);
    }

    /**
     * Add application statistic.
     *
     * @param userApp application
     */
    public void addAppStats(UserApp userApp) {
        if (PermissionUtils.isUsageStatsPermissionAllowed(context)) {
            addAppStatsGrantedPermission(userApp);
        }
    }

    protected void addAppStatsGrantedPermission(UserApp userApp) {
        isProcessed = false;
        new Thread(() -> runAppStatsSeparateThread(userApp)).start();
    }

    private void runAppStatsSeparateThread(UserApp userApp) {
        Date today = DateTimeUtils.getDateOfCurrentDayBegin();
        Calendar nextDate = Calendar.getInstance();
        nextDate.setTime(today);
        nextDate.add(Calendar.DATE, 1);

        long usedTime = addNewAppUsageStatsForDate(userApp, today, nextDate);
        processCategoryDateState(userApp.getCategory(), usedTime, today);

        userApp.setLastUpdateDate(today);
        notifyStatsProcessed();
    }

    private long addNewAppUsageStatsForDate(UserApp userApp, Date curDate, Calendar nextDate) {
        for (UsageStats usageStat : queryUsageStats(curDate, nextDate)) {
            if (userApp.getPackageName().equals(usageStat.getPackageName())) {
                try {
                    DbHelperFactory.getHelper().getAppUseStatsDao().create(createAppUseStatsForDate(curDate, usageStat.getTotalTimeVisible(), userApp));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return usageStat.getTotalTimeVisible();
            }
        }

        try {
            DbHelperFactory.getHelper().getAppUseStatsDao().create(createAppUseStatsForDate(curDate, 0, userApp));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Removes all usage statistics for the specific application
     *
     * @param userApp application for which you want to delete all usage statistics
     */
    public static void removeAllStatsForApp(UserApp userApp) {
        try {
            DbHelperFactory.getHelper().getAppUseStatsDao().removeAppAllStats(userApp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if all stats is processed and false otherwise
     *
     * @return is stats processing finish
     */
    public boolean isProcessed() {
        return isProcessed;
    }

    /**
     * If app list is building stats is not processed
     */
    public void processAppListBuildingBegan() {
        isProcessed = false;
    }

    /**
     * Subscribe single UI listener, who needs to react on stats processing finish.
     *
     * @param listener single UI listener, who needs to react on stats processing finish
     */
    public void subscribeUIListener(StatsProcessedListener listener) {
        uiListener = listener;
    }

    public void subscribeSystemListener(StatsProcessedListener listener) {
        updateStatisticResultsListeners.add(listener);
    }

    private void publishUIListener() {
        if (uiListener != null) {
            uiListener.processStatsUpdated();
        }
    }

    private void publishSystemListeners() {
        Log.d(LOG_TAG, String.format("Listeners.size = %d", updateStatisticResultsListeners.size()));
        for (StatsProcessedListener listener : updateStatisticResultsListeners) {
            listener.processStatsUpdated();
        }
    }

    /**
     * Unsubscribe single UI listener, who needs to react on stats processing finish.
     */
    public void unsubscribeUIListener() {
        uiListener = null;
    }

    @Getter
    private static class AppListParseResults {
        private final TreeMap<String, UserApp> userAppMap;
        private final Date minDate;

        public AppListParseResults(TreeMap<String, UserApp> userAppMap, Date minDate) {
            this.userAppMap = userAppMap;
            this.minDate = minDate;
        }
    }
}
