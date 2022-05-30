package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Rule;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.utils.DateTimeUtils.getFormattedLimitTime;

/**
 * Place purpose here.
 *
 * @author Kirill
 * @since 24.02.2022
 */
public class RuleViewHolder extends RecyclerView.ViewHolder {
    private final CheckBox ruleInListCheckbox;
    private final TextView ruleLabel;
    private final TextView mondayTimeLimit;
    private final TextView tuesdayTimeLimit;
    private final TextView wednesdayTimeLimit;
    private final TextView thursdayTimeLimit;
    private final TextView fridayTimeLimit;
    private final TextView saturdayTimeLimit;
    private final TextView sundayTimeLimit;

    /**
     * Конструктор.
     *
     * @param itemView вьюха элемента
     */
    public RuleViewHolder(View itemView) {
        super(itemView);
        ruleLabel = itemView.findViewById(R.id.rule_card_title);
        mondayTimeLimit = itemView.findViewById(R.id.monday_time_limit);
        tuesdayTimeLimit = itemView.findViewById(R.id.tuesday_time_limit);
        wednesdayTimeLimit = itemView.findViewById(R.id.wednesday_time_limit);
        thursdayTimeLimit = itemView.findViewById(R.id.thursday_time_limit);
        fridayTimeLimit = itemView.findViewById(R.id.friday_time_limit);
        saturdayTimeLimit = itemView.findViewById(R.id.saturday_time_limit);
        sundayTimeLimit = itemView.findViewById(R.id.sunday_time_limit);
        ruleInListCheckbox = itemView.findViewById(R.id.is_rule_chosen_for_category_checkbox);
    }

    /**
     * Заполняет данные правила лимитов времени.
     *
     * @param rule правило
     */
    public void fillRuleData(Rule rule) {
        ruleLabel.setText(rule.getName());
        mondayTimeLimit.setText(getFormattedLimitTime(rule, Rule.DayOfWeek.MONDAY));
        tuesdayTimeLimit.setText(getFormattedLimitTime(rule, Rule.DayOfWeek.TUESDAY));
        wednesdayTimeLimit.setText(getFormattedLimitTime(rule, Rule.DayOfWeek.WEDNESDAY));
        thursdayTimeLimit.setText(getFormattedLimitTime(rule, Rule.DayOfWeek.THURSDAY));
        fridayTimeLimit.setText(getFormattedLimitTime(rule, Rule.DayOfWeek.FRIDAY));
        saturdayTimeLimit.setText(getFormattedLimitTime(rule, Rule.DayOfWeek.SATURDAY));
        sundayTimeLimit.setText(getFormattedLimitTime(rule, Rule.DayOfWeek.SUNDAY));
    }
}

