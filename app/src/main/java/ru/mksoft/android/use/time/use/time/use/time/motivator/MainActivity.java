package ru.mksoft.android.use.time.use.time.use.time.motivator;

import android.app.AppOpsManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import lombok.Getter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.ActivityMainBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.ActivityStatsProcessor;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessedListener;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessor;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Property;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.utils.PermissionUtils.checkUsageStatsPermission;

/**
 * Main and single activity of the app
 *
 * @author Kirill
 * @since 18.11.21
 */
public class MainActivity extends AppCompatActivity implements StatsProcessedListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String USER_LEVEL_LABEL_NAME_BEGIN = "user_level";

    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    @Getter
    private AppListBuilder appListBuilder;
    @Getter
    private StatsProcessor statsProcessor;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private final Set<RequestPackageUsageStatsPermissionListener> requestPackageUsageStatsPermissionListeners = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::processRequestUsageStatsPermissionResult
        );

        statsProcessor = new ActivityStatsProcessor(this);
        appListBuilder = new AppListBuilder(getPackageManager(), statsProcessor);
        appListBuilder.buildAppList();
        statsProcessor.subscribeSystemListener(this);

        super.onCreate(savedInstanceState);
        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        buildTopLevelMenu(drawer);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void processRequestUsageStatsPermissionResult(ActivityResult result) {
        int mode = checkUsageStatsPermission(this);
        Log.i(LOG_TAG, "PACKAGE_USAGE_STATS permission changed to " + mode);

        for (RequestPackageUsageStatsPermissionListener requestPackageUsageStatsPermissionListener : requestPackageUsageStatsPermissionListeners) {
            requestPackageUsageStatsPermissionListener.onPermissionGranted(mode == AppOpsManager.MODE_ALLOWED);
        }
        requestPackageUsageStatsPermissionListeners.clear();
    }

    private void buildTopLevelMenu(DrawerLayout drawer) {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_applist,
                R.id.nav_category_list,
                R.id.nav_rule_list,
                R.id.nav_short_stats_list
        ).setOpenableLayout(drawer).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        appListBuilder.buildAppList();
        super.onResume();
    }

    /**
     * Request PACKAGE_USAGE_STATS permission.
     *
     * @param listener request listener
     */
    public void requestPackageUsageStatsPermission(RequestPackageUsageStatsPermissionListener listener) {
        int mode = checkUsageStatsPermission(this);

        if (mode == AppOpsManager.MODE_ALLOWED) {
            listener.onPermissionGranted(true);
            return;
        }

        Log.e(LOG_TAG, "Permission requested: " + requestPackageUsageStatsPermissionListeners.size());
        if (requestPackageUsageStatsPermissionListeners.isEmpty()) {
            // TODO ограничить количество запросов и решить, что делать, если пользователь не даёт нужное разрешение
            someActivityResultLauncher.launch(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        requestPackageUsageStatsPermissionListeners.add(listener);
    }

    @Override
    public void processStatsUpdated() {
        runOnUiThread(() -> {
            long level = 0;
            try {
                level = DbHelperFactory.getHelper().getPropertyDAO().queryForId(Property.USER_LEVEL_FIELD_ID).getValue();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            bindUserLevelTitle(level);
            bindUserLevelImage(level);
        });
    }

    private void bindUserLevelImage(long level) {
        ImageView imageUserLevel = binding.navView.getHeaderView(0).findViewById(R.id.nav_header_level_image);
        level = Math.min(level, Property.MAX_SPECIFIC_LEVEL);

        int levelImageResId = getResources().getIdentifier("user_level_image_" + (level + 1), "drawable", getPackageName());
        imageUserLevel.setImageDrawable(
                getDrawable(levelImageResId)
        );
    }

    private void bindUserLevelTitle(long level) {
        TextView headerUserLevel = binding.navView.getHeaderView(0).findViewById(R.id.nav_header_main_title_tv);
        if (level > Property.MAX_SPECIFIC_LEVEL) {
            headerUserLevel.setText(String.format(Locale.getDefault(), "Легенда %d", (level - Property.MAX_SPECIFIC_LEVEL)));
        } else {
            int levelLabelResId = getResources().getIdentifier(USER_LEVEL_LABEL_NAME_BEGIN + level, "string", getPackageName());
            headerUserLevel.setText(levelLabelResId);
        }
    }
}