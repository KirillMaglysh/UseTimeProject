package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist;

import android.os.Bundle;
import android.text.TextUtils;
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
import ru.mksoft.android.use.time.use.time.use.time.motivator.R;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentEditCategoryBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Category;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.Rule;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;
import ru.mksoft.android.use.time.use.time.use.time.motivator.ui.messaging.MessageDialogType;

import java.sql.SQLException;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist.CategoryListRecyclerAdapter.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditCategoryFragment#} factory method to
 * create an instance of this fragment.
 *
 * @author Kirill
 * @since 16.02.2022
 */
public class EditCategoryFragment extends BottomSheetDialogFragment {
    private FragmentEditCategoryBinding binding;

    /**
     * Constructor
     */
    public EditCategoryFragment() {
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditCategoryFragmentArgs fragmentArgs = EditCategoryFragmentArgs.fromBundle(getArguments());
        Category category = null;
        try {
            category = EDIT_CATEGORY_DIALOG_RESULT_KEY.equals(fragmentArgs.getCreateOrAddCategory()) ?
                    DbHelperFactory.getHelper().getCategoryDAO().queryForId(Long.valueOf(fragmentArgs.getCategoryId())) :
                    new Category();
        } catch (SQLException e) {
            //TODO Обработать ошибки корректно
            e.printStackTrace();
        }

        Long selectedRuleId = null;
        if (category != null && category.getId() != null) {
            binding.dialogCategoryLabel.setText(category.getName());
            selectedRuleId = category.getRule().getId();
        }

        RecyclerView recyclerView = binding.ruleListInCategoryDialog;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RuleListInCategoryRecyclerAdapter ruleListAdapter = null;
        try {
            ruleListAdapter = new RuleListInCategoryRecyclerAdapter(DbHelperFactory.getHelper().getRuleDAO().getAllRules(), selectedRuleId);
            recyclerView.setAdapter(ruleListAdapter);
        } catch (SQLException e) {
            //TODO Обработать ошибки корректно
            e.printStackTrace();
        }

        final Category finalCategory = category;
        final RuleListInCategoryRecyclerAdapter adapter = ruleListAdapter;
        binding.confirmCategoryDialogButton.setOnClickListener(v -> {
            if (adapter != null) {
                add(finalCategory, adapter.getChosenRule(), fragmentArgs.getCreateOrAddCategory(), fragmentArgs.getCategoryHolderPosition());
            }
        });


        binding.cancelCategoryDialogButton.setOnClickListener(this::cancel);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void add(Category category, Rule rule, String resultType, Integer positionInAdapter) {
        if (rule == null) {
            warning(R.string.edit_category_choose_rule_warning);
            return;
        }

        String name = binding.dialogCategoryLabel.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            warning(R.string.edit_category_name_empty_warning);
            return;
        }

        category.setName(name);
        category.setRule(rule);
        try {
            DbHelperFactory.getHelper().getCategoryDAO().createOrUpdate(category);
        } catch (SQLException e) {
            warning(R.string.edit_category_name_exists_warning);
            return;
        }

        Bundle result = new Bundle();
        result.putLong(CATEGORY_ID_RESULT_KEY, category.getId());
        result.putInt(CATEGORY_HOLDER_POSITION_IN_ADAPTER_RESULT_KEY, positionInAdapter);

        requireActivity().getSupportFragmentManager().setFragmentResult(resultType, result);
        dismiss();
    }

    private void cancel(View view) {
        dismiss();
    }

    private void warning(int messageId) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
        navHostFragment.getNavController().navigate(EditCategoryFragmentDirections.actionNavEditCategoryToNavMessageDialog(
                MessageDialogType.WARNING,
                null,
                requireContext().getString(messageId),
                null,
                null
        ));
    }
}
