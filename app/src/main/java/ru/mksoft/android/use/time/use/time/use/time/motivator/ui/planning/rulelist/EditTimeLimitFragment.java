package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentEditTimeLimitBinding;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.rulelist.EditRuleFragment.EDIT_TIME_LIMIT_DIALOG_RESULT_KEY;

/**
 * Fragment which is showing when user need to edit time limit
 *
 * @author Kirill
 * @since 24.02.2022
 */
public class EditTimeLimitFragment extends BottomSheetDialogFragment {
    /**
     * Key to get name of the day from result bundle
     */
    public static final String DAY_NAME_KEY = "day_name";

    /**
     * Key to get hours from result bundle
     */
    public static final String RULE_HOURS_KEY = "rule_hours";

    /**
     * Key to get minutes from result bundle
     */
    public static final String RULE_MINUTES_KEY = "rule_minutes";

    private FragmentEditTimeLimitBinding binding;
    private String dayName;

    /**
     * Constructor
     */
    public EditTimeLimitFragment() {
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditTimeLimitFragmentArgs args = EditTimeLimitFragmentArgs.fromBundle(getArguments());
        dayName = args.getDayName();
        binding.editTimeLimitLabel.setText(args.getRuleName());
        binding.editTimeLimitTitle.setText(args.getRuleDay());

        binding.timeLimitEditor.setIs24HourView(true);
        binding.timeLimitEditor.setHour(args.getRuleHours());
        binding.timeLimitEditor.setMinute(args.getRuleMinutes());

        binding.cancelRuleEditionButton.setOnClickListener(this::cancel);
        binding.confirmRuleEditionButton.setOnClickListener(this::saveTimeLimit);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditTimeLimitBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void cancel(View view) {
        dismiss();
    }

    private void saveTimeLimit(View view) {
        Bundle result = new Bundle();
        result.putString(DAY_NAME_KEY, dayName);
        result.putInt(RULE_HOURS_KEY, binding.timeLimitEditor.getHour());
        result.putInt(RULE_MINUTES_KEY, binding.timeLimitEditor.getMinute());

        requireActivity().getSupportFragmentManager().setFragmentResult(EDIT_TIME_LIMIT_DIALOG_RESULT_KEY, result);
        dismiss();
    }
}
