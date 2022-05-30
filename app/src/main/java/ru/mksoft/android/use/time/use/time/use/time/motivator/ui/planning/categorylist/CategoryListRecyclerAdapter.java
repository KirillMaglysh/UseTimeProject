package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.DatabaseException;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.messaging.MessageDialogType;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist.RuleListRecyclerAdapter;

import java.sql.SQLException;
import java.util.List;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelper.PREDEFINED_ID;

/**
 * Адаптер списка категорий.
 *
 * @author Kirill
 * @since 20.02.2022
 */
public class CategoryListRecyclerAdapter extends RecyclerView.Adapter<CategoryListRecyclerAdapter.CategoryCardViewHolder> {
    private static final String LOG_TAG = RuleListRecyclerAdapter.class.getSimpleName();

    /**
     * Key of fragment result listener if old category was edited
     */
    public static final String EDIT_CATEGORY_DIALOG_RESULT_KEY = "edit_category_dialog_result";

    /**
     * Key of fragment result listener if new category was created
     */
    public static final String CREATED_CATEGORY_DIALOG_RESULT_KEY = "created_category_dialog_result";

    /**
     * Key to get category position in adapter from result bundle
     */
    public static final String CATEGORY_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY = "category_holder_position_in_adapter_result";

    /**
     * Key to get id of of the category from result bundle
     */
    public static final String CATEGORY_ID_RESULT_KEY = "category_id_result";

    private final Context context;
    private final List<Category> categories;

    /**
     * Конструктор.
     *
     * @param fragment   фрагмент
     * @param categories список категорий для отображения
     */
    public CategoryListRecyclerAdapter(Fragment fragment, List<Category> categories) {
        this.categories = categories;
        this.context = fragment.getContext();
        FragmentManager fragmentManager = fragment.requireActivity().getSupportFragmentManager();
        LifecycleOwner lifecycleOwner = fragment.getViewLifecycleOwner();

        fragmentManager.setFragmentResultListener(EDIT_CATEGORY_DIALOG_RESULT_KEY, lifecycleOwner, (requestKey, result) -> {
            if (!EDIT_CATEGORY_DIALOG_RESULT_KEY.equals(requestKey)) {
                return;
            }

            int position = result.getInt(CATEGORY_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY);
            try {
                categories.set(position, DbHelperFactory.getHelper().getCategoryDAO().queryForId(result.getLong(CATEGORY_ID_RESULT_KEY)));
                notifyItemChanged(position);
            } catch (SQLException e) {
                //TODO Обработать ошибки корректно
                e.printStackTrace();
            }
        });

        fragmentManager.setFragmentResultListener(CREATED_CATEGORY_DIALOG_RESULT_KEY, lifecycleOwner, (requestKey, result) -> {
            if (!CREATED_CATEGORY_DIALOG_RESULT_KEY.equals(requestKey)) {
                return;
            }

            try {
                categories.add(DbHelperFactory.getHelper().getCategoryDAO().queryForId(result.getLong(CATEGORY_ID_RESULT_KEY)));
                notifyItemInserted(categories.size());
            } catch (SQLException e) {
                //TODO Обработать ошибки корректно
                e.printStackTrace();
            }
        });
    }

    @NonNull
    @Override
    public CategoryCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_card, parent, false);
        return new CategoryCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryCardViewHolder holder, int position) {
        Category category = categories.get(holder.getAdapterPosition());
        holder.categoryTitle.setText(category.getName());
        holder.ruleLabel.setText(category.getRule().getName());

        if (PREDEFINED_ID == category.getId()) {
            holder.editButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        } else {
            holder.editButton.setOnClickListener(view -> editCategory(holder));
            holder.deleteButton.setOnClickListener(view -> deleteCategory(view, holder.getAdapterPosition()));
        }
    }

    private void editCategory(CategoryCardViewHolder holder) {
        int position = holder.getAdapterPosition();
        Navigation.findNavController(holder.itemView)
                .navigate(CategoryListFragmentDirections.actionNavCategoryListToNavEditCategory(position,
                        categories.get(position).getId().toString(), EDIT_CATEGORY_DIALOG_RESULT_KEY));
    }

    private void deleteCategory(View view, int position) {
        Category removingCategory = categories.get(position);
        try {
            DbHelperFactory.getHelper().getCategoryDAO().delete(removingCategory);
        } catch (SQLException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof SQLiteConstraintException) {
                    Log.e(LOG_TAG, "Category deletion error", e);
                    Navigation.findNavController(view)
                            .navigate(CategoryListFragmentDirections.actionNavCategoryListToNavMessageDialog(
                                    MessageDialogType.ERROR,
                                    null,
                                    context.getString(R.string.edit_category_unable_delete_used),
                                    null,
                                    null
                            ));
                    return;
                }
                cause = cause.getCause();
            }

            throw new DatabaseException(e);
        }

        categories.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryCardViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryTitle;
        private final TextView ruleLabel;
        private final Button editButton;
        private final Button deleteButton;

        public CategoryCardViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_card_title);
            ruleLabel = itemView.findViewById(R.id.category_card_rule_name);
            editButton = itemView.findViewById(R.id.category_card_edit_button);
            deleteButton = itemView.findViewById(R.id.category_card_delete_button);
        }
    }
}
