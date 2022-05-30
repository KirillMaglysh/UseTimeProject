package ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Database Helper Factory.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class DbHelperFactory {
    private static DbHelper dbHelper;

    /**
     * Returns dbHelper
     *
     * @return dbHelper
     */
    public static DbHelper getHelper() {
        return dbHelper;
    }

    /**
     * Set dbHelper
     *
     * @param context application context
     */
    public static void setHelper(Context context) {
        dbHelper = OpenHelperManager.getHelper(context, DbHelper.class);
    }

    /**
     * Release the helper that was previously returned by a call getHelper(Context)
     */
    public static void releaseHelper() {
        OpenHelperManager.releaseHelper();
        dbHelper = null;
    }
}
