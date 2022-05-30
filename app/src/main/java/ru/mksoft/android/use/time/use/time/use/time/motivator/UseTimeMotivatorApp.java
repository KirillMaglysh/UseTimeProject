package ru.mksoft.android.use.time.use.time.use.time.motivator;

import android.app.Application;
import android.util.Log;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.alarms.AlarmProcessor;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Property;

import java.sql.SQLException;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 18.11.2021
 */
public class UseTimeMotivatorApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DbHelperFactory.setHelper(getApplicationContext());
        new AlarmProcessor(getApplicationContext()).updateAlarms();
    }

    @Override
    public void onTerminate() {
        DbHelperFactory.releaseHelper();
        super.onTerminate();
    }
}
