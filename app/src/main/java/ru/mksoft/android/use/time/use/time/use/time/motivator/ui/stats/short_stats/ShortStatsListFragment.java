package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.short_stats;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lombok.Getter;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentShortStatsListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessedListener;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessor;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.CategoryStatsBin;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.AppUseStats;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShortStatsListFragment#} factory method to
 * create an instance of this fragment.
 *
 * @author Kirill
 * @since 20.02.2022
 */
public class ShortStatsListFragment extends Fragment implements StatsProcessedListener {
    private FragmentShortStatsListBinding binding;
    private ConstraintLayout progressBar;
    private ShortStatsListRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Date date = DateTimeUtils.getDateOfCurrentDayBegin();
    public static final int WEEK_PERIOD_ID = 0;
    public static final int DAY_PERIOD_ID = 1;
    private int currentPeriodID = WEEK_PERIOD_ID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShortStatsListBinding.inflate(inflater, container, false);
        if (currentPeriodID == DAY_PERIOD_ID) {
            binding.dateText.setEnabled(true);
        }

        setDateText();
        StatsProcessor statsProcessor = ((MainActivity) getContext()).getStatsProcessor();
        progressBar = binding.shortStatsListProgressWindow.progressWindow;
        if (statsProcessor.isProcessed()) {
            processStatsUpdated();
        } else {
            statsProcessor.subscribeUIListener(this);
            progressBar.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
        binding = null;
    }

    private List<CategoryInShortSummary> buildCategoryList() {
        List<CategoryInShortSummary> shortSummaries = null;
        try {
            List<CategoryStatsBin> categoryStatsBins =
                    currentPeriodID == DAY_PERIOD_ID ?
                            DbHelperFactory.getHelper().getAppUseStatsDao().getAllCategoriesSumStatsBinWoDefaultByDate(date) :
                            DbHelperFactory.getHelper().getAppUseStatsDao().getAllCategoriesSumStatsBinWoDefaultByLastWeek();

            shortSummaries = new ArrayList<>(categoryStatsBins.size());
            for (CategoryStatsBin categoryStatsBin : categoryStatsBins) {
                shortSummaries.add(new CategoryInShortSummary(categoryStatsBin.getCategory(), categoryStatsBin.getUsedTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shortSummaries;
    }

    @Override
    public void processStatsUpdated() {
        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
        ((MainActivity) getContext()).runOnUiThread(() -> {
            initDateChoose();
            recyclerView = binding.statsListRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ShortStatsListRecyclerAdapter(this.getContext(), buildCategoryList());
            recyclerView.setAdapter(adapter);
        });

        progressBar.setVisibility(View.INVISIBLE);
    }

    private void initDateChoose() {
        binding.periodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                if (selectedId == currentPeriodID) {
                    return;
                }

                if (selectedId == WEEK_PERIOD_ID) {
                    binding.dateText.setVisibility(View.INVISIBLE);
                } else {
                    binding.dateText.setVisibility(View.VISIBLE);
                }

                currentPeriodID = (int) selectedId;
                reloadStats();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.dateText.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
            datePickerDialog.setOnDateSetListener(new StatsDateStateListener());
            datePickerDialog.show();
        });
    }

    private void reloadStats() {
        adapter.setDate(date);
        adapter.setCurrentPeriodID(currentPeriodID);
        adapter.reloadStats(buildCategoryList());
    }

    private void setDateText() {
        binding.dateText.setText(new SimpleDateFormat(AppUseStats.DATE_FORMAT, Locale.getDefault()).format(date));
    }

    @Getter
    public class CategoryInShortSummary {
        private Category category;
        int minutes;
        int hours;

        public CategoryInShortSummary(Category category, long timeInMilliseconds) {
            this.category = category;
            minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds) % 60;
            hours = (int) TimeUnit.MILLISECONDS.toHours(timeInMilliseconds) % 24;
        }
    }

    private class StatsDateStateListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (date.getTime() != calendar.getTimeInMillis()) {
                date.setTime(calendar.getTimeInMillis());
                setDateText();
                reloadStats();
            }
        }
    }
}
