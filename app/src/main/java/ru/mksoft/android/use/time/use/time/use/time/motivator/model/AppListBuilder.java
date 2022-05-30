package ru.mksoft.android.use.time.use.time.use.time.motivator.model;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.DatabaseException;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.UserApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 07.01.2022
 */

//TODO() переписать длинные лямбда на нормальные классы
public class AppListBuilder {
    private static final String LOG_TAG = AppListBuilder.class.getSimpleName();

    private final PackageManager packageManager;
    private final StatsProcessor statsProcessor;
    // TODO: Проверить необходимость хранения переменной
    private boolean isBuilt;
    private AppListBuiltListener uiListener;
    private Category defaultCategory;

    /**
     * Constructor
     *
     * @param packageManager packageManager from activity
     * @param statsProcessor statsProcessor, which must be notified
     */
    public AppListBuilder(PackageManager packageManager, StatsProcessor statsProcessor) {
        Log.d(LOG_TAG, "Create AppListBuilder... packageManager " + packageManager);
        this.packageManager = packageManager;
        this.statsProcessor = statsProcessor;
    }

    /**
     * Updates information about application on the device in other thread
     */
    public synchronized void buildAppList() {
        new Thread(() -> {
            isBuilt = false;
            statsProcessor.processAppListBuildingBegan();
            process();
        }).start();
    }

    private synchronized void process() {
        Log.d(LOG_TAG, "Process built app list...");
        List<UserApp> untrackedApps;
        List<UserApp> trackedApps;
        try {
            defaultCategory = DbHelperFactory.getHelper().getCategoryDAO().getDefaultCategory();
            trackedApps = DbHelperFactory.getHelper().getUserAppDAO().getAllTrackedApps();
            untrackedApps = DbHelperFactory.getHelper().getUserAppDAO().getAllUntrackedApps();
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error getting default category");
            throw new DatabaseException(e);
        }

        TreeMap<Integer, AppParams> sortedTracked = createAppsMap(trackedApps);
        TreeMap<Integer, AppParams> sortedUntracked = createAppsMap(untrackedApps);
        readCurrentAppList(sortedTracked, sortedUntracked);

        deleteNotFoundApps(trackedApps, sortedTracked);
        deleteNotFoundApps(untrackedApps, sortedUntracked);

        // TODO: обработать возможную гонку состояний
        notifyAppListBuilt();
    }

    private void readCurrentAppList(TreeMap<Integer, AppParams> sortedTracked, TreeMap<Integer, AppParams> sortedUntracked) {
        List<ApplicationInfo> notSortedAppList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : notSortedAppList) {
            try {
                if (sortedTracked.containsKey(info.uid)) {
                    sortedTracked.get(info.uid).wasFound = true;
                } else if (sortedUntracked.containsKey(info.uid)) {
                    sortedUntracked.get(info.uid).wasFound = true;
                } else if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    addNewUntrackedAppIntoDB(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addNewUntrackedAppIntoDB(ApplicationInfo info) throws SQLException {
        UserApp newApp = new UserApp();
        newApp.setSystemId(info.uid);
        newApp.setPackageName(String.valueOf(info.packageName));
        newApp.setCategory(defaultCategory);
        newApp.setLastUpdateDate(DateTimeUtils.getDateOfCurrentDayBegin());

        DbHelperFactory.getHelper().getUserAppDAO().create(newApp);
    }

    private static TreeMap<Integer, AppParams> createAppsMap(List<UserApp> userApps) {
        TreeMap<Integer, AppParams> sortedTracked = new TreeMap<>();
        for (int i = 0; i < userApps.size(); i++) {
            sortedTracked.put(userApps.get(i).getSystemId(), new AppParams(i));
        }

        return sortedTracked;
    }

    private static void deleteNotFoundApps(List<UserApp> userApps, TreeMap<Integer, AppParams> sortedApps) {
        sortedApps.forEach((num, appParams) -> {
            if (!appParams.wasFound) {
                try {
                    DbHelperFactory.getHelper().getUserAppDAO().delete(userApps.get(appParams.originalPos));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void notifyAppListBuilt() {
        Log.d(LOG_TAG, "Notify app list built");

        isBuilt = true;
        if (uiListener != null) {
            uiListener.processAppListBuilt();
        } else {
            Log.d(LOG_TAG, "uiListener undefined");
        }

        statsProcessor.updateUseStats();
    }

    /**
     * Returns true if app list built and false otherwise
     *
     * @return is app list building finish
     */
    public boolean isBuilt() {
        return isBuilt;
    }

    /**
     * Subscribe single UI listener, who needs to react on stats processing finish.
     *
     * @param listener single UI listener, who needs to react on stats processing finish
     */
    public void subscribe(AppListBuiltListener listener) {
        uiListener = listener;
    }

    /**
     * Unsubscribe single UI listener, who needs to react on stats processing finish.
     */
    public void unsubscribeUIListener() {
        uiListener = null;
    }

    /**
     * UI element, which need to react on app list built.
     */
    public interface AppListBuiltListener {
        /**
         * Calls when statistics is processed.
         */
        void processAppListBuilt();
    }

    static class AppParams {
        int originalPos;

        boolean wasFound;

        public AppParams(int originalPos) {
            this.originalPos = originalPos;
            wasFound = false;
        }

    }
}
