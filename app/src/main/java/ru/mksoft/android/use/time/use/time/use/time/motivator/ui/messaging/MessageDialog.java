package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.messaging;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentMessageDialogBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Message dialog.
 * Uses to show message (error, warning, etc).
 *
 * @author Kirill
 * @since 03.03.2022
 */
public class MessageDialog extends BottomSheetDialogFragment {
    /**
     * Message dialog result code.
     */
    public static final String MESSAGE_DIALOG_RESULT_CODE = "message_dialog_result";

    /**
     * Negative button pressed value.
     */
    public static final int NEGATIVE_BUTTON = 0;

    /**
     * Positive button pressed value.
     */
    public static final int POSITIVE_BUTTON = 1;

    private FragmentMessageDialogBinding binding;

    /**
     * Constructor.
     */
    public MessageDialog() {
        setCancelable(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessageDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MessageDialogArgs fragmentArgs = MessageDialogArgs.fromBundle(getArguments());

        fillBackground(fragmentArgs);
        fillTitle(fragmentArgs);
        fillButtons(fragmentArgs);
        binding.dialogMessage.setText(fragmentArgs.getMessage());
    }

    private void fillTitle(MessageDialogArgs fragmentArgs) {
        String title = fragmentArgs.getTitle();
        if (!TextUtils.isEmpty(title)) {
            binding.dialogTitle.setText(title);
            return;
        }

        switch (fragmentArgs.getDialogType()) {
            case ERROR:
                binding.dialogTitle.setText(R.string.message_dialog_default_error_title);
                return;
            case WARNING:
                binding.dialogTitle.setText(R.string.message_dialog_default_warning_title);
                return;
        }

        binding.dialogTitle.setVisibility(View.GONE);
    }

    private void fillBackground(MessageDialogArgs fragmentArgs) {
        MessageDialogType type = fragmentArgs.getDialogType();
        switch (type) {
            case ERROR:
                binding.getRoot().setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.error_dialog_background));
                return;
            case WARNING:
                binding.getRoot().setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.warning_dialog_background));
        }
    }

    private void fillButtons(MessageDialogArgs fragmentArgs) {
        String positiveButtonTitle = fragmentArgs.getPositiveButtonTitle();
        String negativeButtonTitle = fragmentArgs.getNegativeButtonTitle();
        if (TextUtils.isEmpty(positiveButtonTitle) && TextUtils.isEmpty(negativeButtonTitle)) {
            positiveButtonTitle = getString(R.string.message_dialog_default_positive_button_title);
        }

        fillButton(binding.positiveDialogButton, positiveButtonTitle);
        binding.positiveDialogButton.setOnClickListener(buttonView -> buttonAction(POSITIVE_BUTTON));

        fillButton(binding.negativeDialogButton, negativeButtonTitle);
        binding.negativeDialogButton.setOnClickListener(buttonView -> buttonAction(NEGATIVE_BUTTON));
    }

    private static void fillButton(View button, String buttonTitle) {
        if (TextUtils.isEmpty(buttonTitle)) {
            button.setVisibility(View.GONE);
            return;
        }

        ((Button) button).setText(buttonTitle);
        button.setVisibility(View.VISIBLE);
    }

    private void buttonAction(@DialogResultValue int pressedButton) {
        Bundle result = new Bundle();
        result.putInt(MESSAGE_DIALOG_RESULT_CODE, pressedButton);
        requireActivity().getSupportFragmentManager().setFragmentResult(MESSAGE_DIALOG_RESULT_CODE, result);
        dismiss();
    }

    @IntDef({NEGATIVE_BUTTON, POSITIVE_BUTTON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogResultValue {
    }
}
