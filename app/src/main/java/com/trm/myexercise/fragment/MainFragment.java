package com.trm.myexercise.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trm.myexercise.MainActivity;
import com.trm.myexercise.R;
import com.trm.myexercise.adapter.ActivityItemAdapter;
import com.trm.myexercise.utils.FragmentNavigationRequest;
import com.trm.myexercise.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements ActivityItemAdapter.OnItemClick {

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private RecyclerView recyclerView;
    private ActivityItemAdapter adapter;

    private MainViewModel mainViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!(getActivity() instanceof MainActivity)) {
            try {
                throw new Exception("not com.trm.myexercise.MainActivity");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        mainViewModel = ((MainActivity) getActivity()).getViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = mainView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActivityItemAdapter(MainViewModel.activityDiffItem);
        recyclerView.setAdapter(adapter);

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.submitList(mainViewModel.getFragmentList());
        adapter.setOnItemClick(this::onClick);
    }

    @Override
    public void onClick(FragmentNavigationRequest request) {
        mainViewModel.showFragment(request);
    }
}
