package com.trm.myexercise.viewmodels;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.DiffUtil;

import com.trm.myexercise.fragment.BaseFragment;
import com.trm.myexercise.fragment.ChromeFragment;
import com.trm.myexercise.fragment.EraserFragment;
import com.trm.myexercise.fragment.FlowFragment;
import com.trm.myexercise.fragment.ScaleViewFragment;
import com.trm.myexercise.utils.Event;
import com.trm.myexercise.utils.FragmentNavigationRequest;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel_TAG";

    /**
     * 主页展示list列表的数据
     */
    private List<FragmentNavigationRequest> fragmentList;
    public List<FragmentNavigationRequest> getFragmentList() {
        if (fragmentList != null) {
            return fragmentList;
        }
        fragmentList = new ArrayList<>();
        FragmentNavigationRequest fragmentRequest = new FragmentNavigationRequest(
                BaseFragment.newInstance(FlowFragment.class), true, "FlowFragment");
        fragmentList.add(fragmentRequest);

        fragmentRequest = new FragmentNavigationRequest(
                BaseFragment.newInstance(ChromeFragment.class), true, "ChromeFragment");
        fragmentList.add(fragmentRequest);

        fragmentRequest = new FragmentNavigationRequest(
                BaseFragment.newInstance(EraserFragment.class), true, "EraserFragment");
        fragmentList.add(fragmentRequest);

        fragmentRequest = new FragmentNavigationRequest(
                BaseFragment.newInstance(ScaleViewFragment.class), true, "ScaleViewFragment");
        fragmentList.add(fragmentRequest);

        return fragmentList;
    }

    /**
     * This [LiveData] object is used to notify the MainActivity that the main
     * content fragment needs to be swapped. Information about the new fragment
     * is conveniently wrapped by the [Event] class.
     */

    private MutableLiveData<Event<FragmentNavigationRequest>> navigateToFragment = new MutableLiveData<>();
    public LiveData<Event<FragmentNavigationRequest>> getNavigateToFragment() {
        return navigateToFragment;
    }

    /**
     * Convenience method used to swap the fragment shown in the main activity
     *
     * @param fragmentRequest the fragment to show
     */
    public void showFragment(FragmentNavigationRequest fragmentRequest) {
        Log.d(TAG, "showFragment showFragment");
        navigateToFragment.postValue(new Event(fragmentRequest));
    }

    public static DiffUtil.ItemCallback<FragmentNavigationRequest> activityDiffItem = new DiffUtil.ItemCallback<FragmentNavigationRequest>() {
        @Override
        public boolean areItemsTheSame(@NonNull FragmentNavigationRequest oldItem, @NonNull FragmentNavigationRequest newItem) {
            if (oldItem.getFragment().getClass().getName().equals(newItem.getFragment().getClass().getName())) {
                return true;
            }
            return false;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull FragmentNavigationRequest oldItem, @NonNull FragmentNavigationRequest newItem) {
            if (oldItem.getFragment().getClass().getName().equals(newItem.getFragment().getClass().getName())) {
                return true;
            }
            return false;
        }
    };


}
