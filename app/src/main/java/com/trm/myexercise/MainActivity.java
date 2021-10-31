package com.trm.myexercise;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.trm.myexercise.fragment.MainFragment;
import com.trm.myexercise.utils.FragmentNavigationRequest;
import com.trm.myexercise.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, MainFragment.newInstance()).commit();

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);

        viewModel.getNavigateToFragment().observe(this, it -> {
            FragmentNavigationRequest fragmentRequest = it.getContentIfNeed();
            if (fragmentRequest == null) {
                return;
            }
            FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragmentRequest.getFragment(), fragmentRequest.getTag());
            if (fragmentRequest.getBackStack()) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commitAllowingStateLoss();
        });
    }

    public MainViewModel getViewModel() {
        return viewModel;
    }
}