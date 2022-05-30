package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.jetbrains.annotations.NotNull;
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentTrackNewAppDialogBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.messaging.MessageDialogType;

import java.sql.SQLException;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist.AppListRecyclerAdapter.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrackNewAppDialog#} factory method to
 * create an instance of this fragment.
 *
 * @author 02.03.2022
 * @since Kirill
 */
public class TrackNewAppDialog extends BottomSheetDialogFragment {
    private FragmentTrackNewAppDialogBinding binding;

    /**
     * Constructor
     */
    public TrackNewAppDialog() {
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TrackNewAppDialogArgs fragmentArgs = TrackNewAppDialogArgs.fromBundle(getArguments());

        binding.dialogAppName.setText(fragmentArgs.getAppLabel());

        RecyclerView recyclerView = binding.categoryListInDialog;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TrackAppCategoryListRecyclerAdapter categoryListAdapter = null;
        try {
            categoryListAdapter = new TrackAppCategoryListRecyclerAdapter(DbHelperFactory.getHelper().getCategoryDAO().getAllCategoriesWoDefault());
            recyclerView.setAdapter(categoryListAdapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final TrackAppCategoryListRecyclerAdapter adapter = categoryListAdapter;
        binding.cancelDialogButton.setOnClickListener(this::cancel);
        binding.addDialogButton.setOnClickListener(v -> {
            if (adapter != null) {
                add(adapter.getChosenCategory(), fragmentArgs.getPositionInAdapter());
            }
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackNewAppDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void add(Category category, Integer positionInAdapter) {
        if (category != null) {
            Bundle result = new Bundle();
            result.putLong(CHOSEN_CATEGORY_ID_RESULT_KEY, category.getId());
            result.putInt(APP_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY, positionInAdapter);
            requireActivity().getSupportFragmentManager().setFragmentResult(TRACK_NEW_APP_DIALOG_RESULT_KEY, result);
            dismiss();
        } else {
            warning(R.string.track_new_app_choose_category_warning);
        }
    }

    private void cancel(View view) {
        dismiss();
    }

    private void warning(int messageId) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
        navHostFragment.getNavController().navigate(TrackNewAppDialogDirections.actionNavTrackNewAppDialogToNavMessageDialog(
                MessageDialogType.WARNING,
                null,
                requireContext().getString(messageId),
                null,
                null
        ));
    }
}
