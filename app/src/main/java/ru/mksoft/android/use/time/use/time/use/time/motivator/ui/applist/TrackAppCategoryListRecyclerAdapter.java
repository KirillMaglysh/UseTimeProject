package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;

import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 06.02.2022
 */
public class TrackAppCategoryListRecyclerAdapter extends RecyclerView.Adapter<TrackAppCategoryListRecyclerAdapter.CategoryViewHolder> {
    private final List<Category> categories;
    private CheckBox chosenCategory = null;
    private int chosenCategoryPosition = -1;

    /**
     * Constructor
     *
     * @param categories list of categories you want to show user
     */
    public TrackAppCategoryListRecyclerAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_in_dialog_label, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryViewHolder holder, int position) {
        holder.categoryInListCheckbox.setText(categories.get(position).getName());

        int pos = position;
        holder.categoryInListCheckbox.setOnClickListener(view -> {
            if (chosenCategory != null) {
                chosenCategory.setChecked(false);
            }

            chosenCategory = holder.categoryInListCheckbox;
            chosenCategoryPosition = pos;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * Returns category which user chosen or null if he has chosen nothing
     *
     * @return category which user chosen or null if he has chosen nothing
     */
    public Category getChosenCategory() {
        if (chosenCategoryPosition != -1) {
            return categories.get(chosenCategoryPosition);
        } else {
            return null;
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox categoryInListCheckbox;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryInListCheckbox = itemView.findViewById(R.id.category_in_dialog_label);
        }
    }
}
