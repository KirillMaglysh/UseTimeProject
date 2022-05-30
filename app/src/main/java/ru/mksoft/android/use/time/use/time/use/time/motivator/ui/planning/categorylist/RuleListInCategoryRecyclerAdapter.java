package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.RuleViewHolder;

import java.util.List;

/**
 * Place here class purpose.
 *
 * @author Kirill
 * @since 21.02.2022
 */
public class RuleListInCategoryRecyclerAdapter extends RecyclerView.Adapter<RuleListInCategoryRecyclerAdapter.EditCategoryRuleViewHolder> {
    private final List<Rule> rules;
    private final Long selectedRuleId;
    private CheckBox chosenRule = null;
    private int chosenRulePosition = -1;

    public RuleListInCategoryRecyclerAdapter(List<Rule> rules, Long selectedRuleId) {
        this.rules = rules;
        this.selectedRuleId = selectedRuleId;
    }

    @NonNull
    @NotNull
    @Override
    public EditCategoryRuleViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new EditCategoryRuleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_in_category_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EditCategoryRuleViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        Rule rule = rules.get(adapterPosition);
        holder.fillRuleData(rule);

        if (selectedRuleId != null && selectedRuleId.compareTo(rule.getId()) == 0) {
            holder.ruleInListCheckbox.setChecked(true);
            chosenRule = holder.ruleInListCheckbox;
            chosenRulePosition = adapterPosition;
        }

        holder.ruleInListCheckbox.setOnClickListener(view -> onRuleCheckboxClicked((CheckBox) view, holder, holder.getLayoutPosition()));
    }

    private void onRuleCheckboxClicked(CheckBox checkBox, EditCategoryRuleViewHolder holder, int position) {
        if (chosenRule != null) {
            chosenRule.setChecked(false);
        }

        if (!checkBox.isChecked()) {
            chosenRule = null;
            chosenRulePosition = -1;
        } else {
            chosenRule = holder.ruleInListCheckbox;
            chosenRulePosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public Rule getChosenRule() {
        if (chosenRulePosition < 0) {
            return null;
        } else {
            return rules.get(chosenRulePosition);
        }
    }

    static final class EditCategoryRuleViewHolder extends RuleViewHolder {
        private final CheckBox ruleInListCheckbox;

        public EditCategoryRuleViewHolder(View itemView) {
            super(itemView);
            ruleInListCheckbox = itemView.findViewById(R.id.is_rule_chosen_for_category_checkbox);
        }
    }
}
