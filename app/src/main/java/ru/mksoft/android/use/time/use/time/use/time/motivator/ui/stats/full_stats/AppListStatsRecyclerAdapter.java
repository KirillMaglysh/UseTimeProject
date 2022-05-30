package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.full_stats;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppStatsBin;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.HourMinuteTime;

import java.util.List;
import java.util.Locale;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 14.05.2022
 */
public class AppListStatsRecyclerAdapter extends RecyclerView.Adapter<AppListStatsRecyclerAdapter.AppShortStatsCardHolder> {
    private static final String TIME_PART_FORMAT = "%02d";

    private List<AppStatsBin> appStatsBins;
    private Context context;
    private PackageManager packageManager;

    public AppListStatsRecyclerAdapter(Context context, List<AppStatsBin> appStatsBins) {
        this.appStatsBins = appStatsBins;
        this.context = context;
        packageManager = context.getPackageManager();
    }

    @NotNull
    @Override
    public AppShortStatsCardHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new AppShortStatsCardHolder(LayoutInflater.from(context).inflate(R.layout.app_with_use_stats_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AppShortStatsCardHolder holder, int position) {
        AppStatsBin appStatsBin = appStatsBins.get(position);
        HourMinuteTime hourMinuteTime = new HourMinuteTime(appStatsBin.getUsedTime());
        holder.hourValue.setText(String.format(Locale.US, TIME_PART_FORMAT, hourMinuteTime.getHours()));
        holder.minuteValue.setText(String.format(Locale.US, TIME_PART_FORMAT, hourMinuteTime.getMinutes()));

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(appStatsBin.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        holder.appLabel.setText(packageManager.getApplicationLabel(applicationInfo));
        holder.appIcon.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
    }

    @Override
    public int getItemCount() {
        return appStatsBins.size();
    }

    class AppShortStatsCardHolder extends RecyclerView.ViewHolder {
        private final TextView hourValue;
        private final TextView minuteValue;
        private final TextView appLabel;
        private final ImageView appIcon;

        public AppShortStatsCardHolder(View itemView) {
            super(itemView);
            hourValue = itemView.findViewById(R.id.hour_val_in_app_stats);
            minuteValue = itemView.findViewById(R.id.minute_val_in_app_stats);
            appLabel = itemView.findViewById(R.id.app_label_in_stats);
            appIcon = itemView.findViewById(R.id.app_icon_in_stats);
        }
    }
}
