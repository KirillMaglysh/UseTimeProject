package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentCategoryListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;

import java.sql.SQLException;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist.CategoryListRecyclerAdapter.CREATED_CATEGORY_DIALOG_RESULT_KEY;
import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.planning.categorylist.CategoryListRecyclerAdapter.EDIT_CATEGORY_DIALOG_RESULT_KEY;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 * @author Kirill
 * @since 13.02.2022
 */
public class CategoryListFragment extends Fragment {
    private FragmentCategoryListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.categoryListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoryListRecyclerAdapter adapter = null;
        try {
            adapter = new CategoryListRecyclerAdapter(this, DbHelperFactory.getHelper().getCategoryDAO().getAllCategoriesWoDefault());
            recyclerView.setAdapter(adapter);
        } catch (SQLException e) {
            //TODO Обработать ошибки корректно
            e.printStackTrace();
        }

        CategoryListRecyclerAdapter finalAdapter = adapter;
        binding.newCategoryButton.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(CategoryListFragmentDirections.actionNavCategoryListToNavEditCategory(finalAdapter.getItemCount(), "-1", CREATED_CATEGORY_DIALOG_RESULT_KEY)));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.clearFragmentResultListener(EDIT_CATEGORY_DIALOG_RESULT_KEY);
        fragmentManager.clearFragmentResultListener(CREATED_CATEGORY_DIALOG_RESULT_KEY);
        binding = null;
    }
}
