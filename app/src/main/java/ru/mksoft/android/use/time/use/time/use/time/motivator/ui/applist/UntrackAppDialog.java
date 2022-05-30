package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentUntrackAppDialogBinding;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist.AppListRecyclerAdapter.APP_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY;
import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist.AppListRecyclerAdapter.UNTRACK_APP_DIALOG_RESULT_KEY;

/**
 * Dialog for disabling the application from tracking.
 *
 * @author Kirill
 * @since 27.02.2022
 */
public class UntrackAppDialog extends BottomSheetDialogFragment {
    private FragmentUntrackAppDialogBinding binding;

    /**
     * Constructor.
     */
    public UntrackAppDialog() {
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UntrackAppDialogArgs fragmentArgs = UntrackAppDialogArgs.fromBundle(getArguments());

        binding.dialogAppName.setText(fragmentArgs.getAppLabel());
        binding.cancelDialogButton.setOnClickListener(this::cancel);
        binding.saveDialogButton.setOnClickListener(v -> save(fragmentArgs.getPositionInAdapter()));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUntrackAppDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void save(Integer positionInAdapter) {
        Bundle result = new Bundle();
        result.putInt(APP_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY, positionInAdapter);
        requireActivity().getSupportFragmentManager().setFragmentResult(UNTRACK_APP_DIALOG_RESULT_KEY, result);
        dismiss();
    }

    private void cancel(View view) {
        dismiss();
    }
}
