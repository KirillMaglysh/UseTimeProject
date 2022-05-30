package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.full_stats;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentWeekFullCategoryStatsBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppStatsBin;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.UserApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.RuleViewHolder;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 08.05.2022
 */
public class WeekFullCategoryStatsFragment extends FullCategoryStatsFragment {
    private static final int LABEL_NUM = Calendar.DAY_OF_WEEK;
    private BarChart barChart;
    private final String[] xAxisLabels = new String[LABEL_NUM];
    private FragmentWeekFullCategoryStatsBinding binding;

    @Override
    protected void drawOwnPart() {
        visualizeCategoryAndRule();
        editChart();
        prepareChartDataSet();
        drawChart();
    }

    @Override
    protected void initBinding(@NotNull LayoutInflater inflater, ViewGroup container) {
        binding = FragmentWeekFullCategoryStatsBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initAppPieChart() {
        setAppPieChart(binding.categoryStatsDayAppPieChart);
    }

    @Override
    protected View getRootView() {
        return binding.getRoot();
    }

    protected void drawChart() {
        barChart.animateY(1000);
    }

    @Nullable
    protected Category queryCategory() {
        WeekFullCategoryStatsFragmentArgs fragmentArgs = WeekFullCategoryStatsFragmentArgs.fromBundle(getArguments());
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
            return DbHelperFactory.getHelper().getAppUseStatsDao().getStatsForAllCategoryAppsByLastWeek(getCategory());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected FrameLayout getFrameRecyclerLayout() {
        return binding.appListInFullStatsLayout;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return binding.weekAppRecyclerLayoutWithUseStats.appListWithUsedStatsRecyclerView;
    }

    protected void editChart() {
        barChart = binding.categoryStatsWeekBarChart;
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        setXAxis();
        setYAxis();
    }

    private void visualizeCategoryAndRule() {
        new RuleViewHolder(binding.ruleBodyInFullStats.getRoot()).fillRuleData(getRule());
        binding.categoryInFullStatsLabel.setText(getCategory().getName());
    }

    private void setXAxis() {
        Calendar calendar = Calendar.getInstance();
        for (int i = LABEL_NUM - 1; i >= 0; i--) {
            xAxisLabels[i] = DateTimeUtils.getFormattedDateWithDayOfWeek(calendar);
            calendar.add(Calendar.DATE, -1);
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(8);
        xAxis.setLabelCount(LABEL_NUM);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new XAxisValueFormatter());
    }

    private void setYAxis() {
        barChart.getAxisRight().setEnabled(false);
        YAxis axisLeft = barChart.getAxisLeft();
        axisLeft.setValueFormatter(new LeftAxisValueFormatter());
    }

    protected void prepareChartDataSet() {
        List<Integer> stats = getStats(getCategory());

        BarDataSet dataset = new BarDataSet(createDataEntries(stats), "");
        dataset.setValueFormatter(new BarItemFormatter());
        dataset.setColors(createColorSet(stats, getRule()));

        barChart.setData(new BarData(dataset, dataset));
    }

    private static List<BarEntry> createDataEntries(List<Integer> stats) {
        List<BarEntry> entries = new ArrayList<>();
        float xPos = 1f;
        for (Integer stat : stats) {
            entries.add(new BarEntry(xPos++, stat));
        }

        return entries;
    }

    private static int[] createColorSet(List<Integer> stats, Rule rule) {
        int[] colorSet = new int[LABEL_NUM];
        Calendar calendar = Calendar.getInstance();
        for (int i = LABEL_NUM - 1; i >= 0; i--) {
            Integer timeLimit = rule.getTime(Rule.DayOfWeek.values()[DateTimeUtils.getDayOfWeek(calendar)]);

            // TODO попробовать упростить логику определения цвета
            // TODO подумать над количеством градаций уровня
            int factor = Math.min((int) (stats.get(i) / (timeLimit / 100f)), 255);
            if (timeLimit >= stats.get(i)) {
                colorSet[i] = Color.rgb(factor, 255, 0);
            } else {
                colorSet[i] = Color.rgb(255, 255 - factor, 0);
            }

            calendar.add(Calendar.DATE, -1);
        }

        return colorSet;
    }

    protected static List<Integer> getStats(Category category) {
        List<Integer> statsInMinutes = new ArrayList<>();

        try {
            List<Long> longStats = DbHelperFactory.getHelper().getAppUseStatsDao().getCategorySumSuffixTimeStats(category, LABEL_NUM);
            for (Long longStat : longStats) {
                statsInMinutes.add((int) (longStat / DateTimeUtils.MILLIS_IN_MINUTE));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statsInMinutes;
    }

    private static class LeftAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return DateTimeUtils.getFormattedMinutesTime((int) value);
        }
    }

    private class XAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return xAxisLabels[(int) value - 1];
        }
    }

    private static class BarItemFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return DateTimeUtils.getFormattedMinutesTime((int) value);
        }
    }
}
