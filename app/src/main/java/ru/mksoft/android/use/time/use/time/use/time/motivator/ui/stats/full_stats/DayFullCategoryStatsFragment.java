package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.full_stats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentDayFullCategoryStatsBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppStatsBin;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.AppUseStats;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.HourMinuteTime;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.05.2022
 */
public class DayFullCategoryStatsFragment extends FullCategoryStatsFragment {
    private FragmentDayFullCategoryStatsBinding binding;
    private static final String TIME_PART_FORMAT = "%02d";

    private Date date;

    @Override
    protected void initAppPieChart() {
        setAppPieChart(binding.categoryStatsDayAppPieChart);
    }

    @Override
    protected void drawOwnPart() {
        drawCategory();
        drawDate();
        drawResults();
    }

    private void drawCategory() {
        binding.categoryInFullStatsLabel.setText(getCategory().getName());
    }

    private void drawDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        binding.date.setText(DateTimeUtils.getFormattedDateWithDayOfWeek(calendar));
    }

    private void drawResults() {
        HourMinuteTime resultHourMinuteTime;
        try {
            resultHourMinuteTime = new HourMinuteTime(DbHelperFactory.getHelper().getAppUseStatsDao()
                    .getCategorySumStatsByDate(getCategory(), date));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        binding.hourValInResults.setText(String.format(Locale.US, TIME_PART_FORMAT, resultHourMinuteTime.getHours()));
        binding.minuteValInResults.setText(String.format(Locale.US, TIME_PART_FORMAT, resultHourMinuteTime.getMinutes()));
        binding.hourValInGoal.setText(String.format(
                Locale.US,
                TIME_PART_FORMAT,
                getCategory().getRule().getHoursLimitTime(Rule.DayOfWeek.values()[(DateTimeUtils.getDayOfWeek(date))])));

        binding.minuteValInGoal.setText(String.format(
                Locale.US,
                TIME_PART_FORMAT,
                getCategory().getRule().getMinutesLimitTime(Rule.DayOfWeek.values()[(DateTimeUtils.getDayOfWeek(date))])));
    }

    @Override
    protected void initBinding(@NotNull LayoutInflater inflater, ViewGroup container) {
        binding = FragmentDayFullCategoryStatsBinding.inflate(inflater, container, false);
    }

    @Override
    protected View getRootView() {
        return binding.getRoot();
    }

    @Nullable
    @Override
    protected Category queryCategory() {
        DayFullCategoryStatsFragmentArgs fragmentArgs = DayFullCategoryStatsFragmentArgs.fromBundle(getArguments());
        Category category = null;
        try {
            category = DbHelperFactory.getHelper().getCategoryDAO().queryForId(Long.valueOf(fragmentArgs.getCategoryId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;
    }

    @Override
    protected List<AppStatsBin> queryAppStats() {
        try {
            return DbHelperFactory.getHelper().getAppUseStatsDao().getStatsForAllCategoryAppsByDate(getCategory(), queryDate());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected FrameLayout getFrameRecyclerLayout() {
        return binding.appListInFullStatsLayout;
    }

    private Date queryDate() {
        DayFullCategoryStatsFragmentArgs fragmentArgs = DayFullCategoryStatsFragmentArgs.fromBundle(getArguments());
        Date date = null;
        try {
            date = new SimpleDateFormat(AppUseStats.DATE_FORMAT, Locale.getDefault()).parse(fragmentArgs.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.date = date;
        return date;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return binding.weekAppRecyclerLayoutWithUseStats.appListWithUsedStatsRecyclerView;
    }
}
