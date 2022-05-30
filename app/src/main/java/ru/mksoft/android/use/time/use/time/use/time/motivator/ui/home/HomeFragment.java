package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import lombok.AllArgsConstructor;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentHomeBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.ShortSummaryCardBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.DayProgress;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessedListener;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Property;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.StatsProcessor;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;

/**
 * Fragment with short information about current stats and strike
 *
 * @author Kirill
 * @since 06.03.2022
 */
public class HomeFragment extends Fragment implements StatsProcessedListener {
    private FragmentHomeBinding binding;
    private static final String QUOTE_RESOURCE_NAME_BEGIN = "quote";
    private static final String SCALE_ITEM_LABEL_NAME_BEGIN = "scale_level";
    private static ScaleDrawThread scaleDrawThread;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.todaySummary.timeStatsDescription.setText(R.string.today_home_time_stats_label);
        binding.yesterdaySummary.timeStatsDescription.setText(getString(R.string.yesterday_home_time_stats_label));

        printQuote();

        StatsProcessor statsProcessor = ((MainActivity) getContext()).getStatsProcessor();
        if (statsProcessor.isProcessed()) {
            processStatsUpdated();
        } else {
            statsProcessor.subscribeUIListener(this);
        }

        return binding.getRoot();
    }

    private void printQuote() {
        int quoteNumber = getResources().getInteger(R.integer.quotesNumber);
        String name = QUOTE_RESOURCE_NAME_BEGIN + new Random().nextInt(quoteNumber);
        binding.motivationQuote.setText(getResources().getIdentifier(name,
                "string", getContext().getPackageName()));
    }

    @Override
    public void onDestroyView() {
        scaleDrawThread.interrupt();

        super.onDestroyView();
        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
        binding = null;
    }

    @Override
    public void processStatsUpdated() {
        ((MainActivity) getContext()).getStatsProcessor().unsubscribeUIListener();
        ((MainActivity) getContext()).runOnUiThread(() -> {
            StatsProcessor statsProcessor = ((MainActivity) getContext()).getStatsProcessor();
            updateDayHomeStats(binding.todaySummary, statsProcessor.getTodayProgress());
            updateDayHomeStats(binding.yesterdaySummary, statsProcessor.getYesterdayProgress());

            long strikeChange = 0;
            try {
                strikeChange = DbHelperFactory.getHelper().getPropertyDAO().queryForId(Property.STRIKE_FIELD_ID).getValue();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            fillScale(strikeChange);
        });
    }

    private void fillScale(long strikeChange) {
        scaleDrawThread = new ScaleDrawThread(strikeChange);
        //TODO() добавить больше картинок для градации
        if (strikeChange < -1) {
            binding.heroStateLayout.catImage.setImageDrawable(getResources().getDrawable(R.drawable.angry_64x64));
        } else if (strikeChange > 5) {
            binding.heroStateLayout.catImage.setImageDrawable(getResources().getDrawable(R.drawable.laughtingoutloud_64x64));
        } else if (strikeChange >= 0) {
            binding.heroStateLayout.catImage.setImageDrawable(getResources().getDrawable(R.drawable.happy_64x64));
        } else {
            binding.heroStateLayout.catImage.setImageDrawable(getResources().getDrawable(R.drawable.angry_64x64));
        }

        scaleDrawThread.start();
    }

    private void updateDayHomeStats(ShortSummaryCardBinding summary, DayProgress progress) {
        summary.timeStatsValue.setText(DateTimeUtils.getFormattedMinutesTime((int) (progress.getTimeUsed() / DateTimeUtils.MILLIS_IN_MINUTE)));
        long categoryNumber = 0;
        try {
            categoryNumber = DbHelperFactory.getHelper().getCategoryDAO().countOfUserCategories();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (progress.getFailedGoalNumber() > 0) {
            summary.goalStatsValue.setTextColor(getResources().getColor(R.color.red));
        } else {
            summary.goalStatsValue.setTextColor(getResources().getColor(R.color.teal_200));
        }

        summary.goalStatsValue.setText(String.format(Locale.getDefault(), "%d / %d",
                categoryNumber - progress.getFailedGoalNumber(), categoryNumber));
    }

    @AllArgsConstructor
    private class ScaleDrawThread extends Thread {
        private long strikeChange;

        @Override
        public void run() {
            long needToPaint = Math.abs(strikeChange);
            for (int i = 0; i < needToPaint; i++) {
                String name = SCALE_ITEM_LABEL_NAME_BEGIN + i;
                int scaleItemID = getResources().getIdentifier(name,
                        "id", getContext().getPackageName());

                ((MainActivity) getContext()).runOnUiThread(() -> {
                    ImageView scaleItem = ((Activity) getContext()).findViewById(scaleItemID);
                    if (strikeChange < 0) {
                        scaleItem.setBackground(getResources().getDrawable(R.color.red));
                    } else {
                        scaleItem.setBackground(getResources().getDrawable(R.color.teal_200));
                    }
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
