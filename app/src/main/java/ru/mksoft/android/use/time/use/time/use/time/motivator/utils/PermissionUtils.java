package ru.mksoft.android.use.time.use.time.use.time.motivator.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Process;
import android.util.Log;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 06.05.2022
 */
public class PermissionUtils {
    private static final String LOG_TAG = PermissionUtils.class.getSimpleName();

    public static int checkUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int permissionMode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        Log.d(LOG_TAG, String.format("Permission %d", permissionMode));

        return permissionMode;
    }

    public static boolean isUsageStatsPermissionAllowed(Context context) {
        return checkUsageStatsPermission(context) == AppOpsManager.MODE_ALLOWED;
    }
}
