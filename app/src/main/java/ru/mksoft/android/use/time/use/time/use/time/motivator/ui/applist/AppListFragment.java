package ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mksoft.android.use.time.use.time.use.time.motivator.MainActivity;
import ru.mksoft.android.use.time.use.time.use.time.motivator.databinding.FragmentAppListBinding;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.AppListBuilder;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.models.UserApp;
import ru.mksoft.android.use.time.use.time.use.time.motivator.model.db.dao.DbHelperFactory;

import java.sql.SQLException;
import java.util.List;

import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist.AppListRecyclerAdapter.TRACK_NEW_APP_DIALOG_RESULT_KEY;
import static ru.mksoft.android.use.time.use.time.use.time.motivator.ui.applist.AppListRecyclerAdapter.UNTRACK_APP_DIALOG_RESULT_KEY;

/**
 * Fragment which shows all installed user applications. Here user can track or untrack applications
 *
 * @author Kirill
 * @since 15.01.2022
 */
public class AppListFragment extends Fragment implements AppListBuilder.AppListBuiltListener {
    private FragmentAppListBinding binding;
    private ConstraintLayout progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAppListBinding.inflate(inflater, container, false);
        AppListBuilder appListBuilder = ((MainActivity) getContext()).getAppListBuilder();
        progressBar = binding.appListProgressWindow.progressWindow;
        if (appListBuilder.isBuilt()) {
            processAppListBuilt();
        } else {
            appListBuilder.subscribe(this);
            progressBar.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getContext()).getAppListBuilder().unsubscribeUIListener();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.clearFragmentResultListener(TRACK_NEW_APP_DIALOG_RESULT_KEY);
        fragmentManager.clearFragmentResultListener(UNTRACK_APP_DIALOG_RESULT_KEY);
        binding = null;
    }

    @Override
    public void processAppListBuilt() {
        ((MainActivity) getContext()).getAppListBuilder().unsubscribeUIListener();
        ((MainActivity) getContext()).runOnUiThread(() -> {
            RecyclerView recyclerView = binding.appListRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            List<UserApp> allTrackedApps = null;
            List<UserApp> allUntrackedApps = null;
            try {
                //TODO Подумать на постраничными запросами к БД.
                //TODO Запрашивать только те приложения, которые надо отобразить.
                allTrackedApps = DbHelperFactory.getHelper().getUserAppDAO().getAllTrackedApps();
                allUntrackedApps = DbHelperFactory.getHelper().getUserAppDAO().getAllUntrackedApps();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            recyclerView.setAdapter(new AppListRecyclerAdapter(this, allTrackedApps, allUntrackedApps));
            progressBar.setVisibility(View.INVISIBLE);
        });
    }
}
