package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.stats.short_stats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.AppUseStats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 23.02.2022
 */
public class ShortStatsListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TIME_PART_FORMAT = "%02d";

    private List<ShortStatsListFragment.CategoryInShortSummary> categoriesInShortSummary;
    private Context context;

    @Setter
    private int currentPeriodID = ShortStatsListFragment.WEEK_PERIOD_ID;
    @Setter
    private Date date;

    public ShortStatsListRecyclerAdapter(Context context, List<ShortStatsListFragment.CategoryInShortSummary> categoriesInShortSummary) {
        this.categoriesInShortSummary = categoriesInShortSummary;
        this.context = context;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CategoryInShortStatsCardHolder(LayoutInflater.from(context).inflate(R.layout.category_short_stats_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        CategoryInShortStatsCardHolder cardHolder = (CategoryInShortStatsCardHolder) holder;
        ShortStatsListFragment.CategoryInShortSummary categoryStats = categoriesInShortSummary.get(position);
        cardHolder.hourValInShortCategoryStats.setText(String.format(Locale.US, TIME_PART_FORMAT, categoryStats.getHours()));
        cardHolder.minuteValInShortCategoryStats.setText(String.format(Locale.US, TIME_PART_FORMAT, categoryStats.getMinutes()));
        cardHolder.categoryInShortStatsLabel.setText(categoryStats.getCategory().getName());

        if (currentPeriodID == ShortStatsListFragment.DAY_PERIOD_ID) {
            ((CategoryInShortStatsCardHolder) holder).moreButton.setOnClickListener(view -> Navigation.findNavController(holder.itemView)
                    .navigate(ShortStatsListFragmentDirections.actionNavShortStatsListToNavDayFullStats(
                            categoryStats.getCategory().getId().toString(),
                            new SimpleDateFormat(AppUseStats.DATE_FORMAT, Locale.getDefault()).format(date)
                    ))
            );
        } else {
            ((CategoryInShortStatsCardHolder) holder).moreButton.setOnClickListener(view -> Navigation.findNavController(holder.itemView)
                    .navigate(ShortStatsListFragmentDirections.actionNavShortStatsListToNavWeekFullStats(
                            categoryStats.getCategory().getId().toString()
                    ))
            );
        }
    }

    @Override
    public int getItemCount() {
        return categoriesInShortSummary.size();
    }

    public void reloadStats(List<ShortStatsListFragment.CategoryInShortSummary> categoriesInShortSummary) {
        this.categoriesInShortSummary = categoriesInShortSummary;
        notifyDataSetChanged();
    }

    class CategoryInShortStatsCardHolder extends RecyclerView.ViewHolder {
        private final TextView hourValInShortCategoryStats;
        private final TextView minuteValInShortCategoryStats;
        private final TextView categoryInShortStatsLabel;
        private final Button moreButton;

        public CategoryInShortStatsCardHolder(View itemView) {
            super(itemView);
            hourValInShortCategoryStats = itemView.findViewById(R.id.hour_val_in_results);
            minuteValInShortCategoryStats = itemView.findViewById(R.id.minute_val_in_results);
            categoryInShortStatsLabel = itemView.findViewById(R.id.category_in_short_stats_label);
            moreButton = itemView.findViewById(R.id.more_button_in_short_category_stats);
        }
    }
}
