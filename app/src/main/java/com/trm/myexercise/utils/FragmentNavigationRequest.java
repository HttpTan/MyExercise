package com.trm.myexercise.utils;

import androidx.fragment.app.Fragment;

/**
 * Helper class used to pass fragment navigation requests between MainActivity
 * and its corresponding ViewModel.
 */
public class FragmentNavigationRequest {
    private Fragment fragment;

    private Boolean backStack = false;

    private String tag;

    public FragmentNavigationRequest(Fragment fragment, Boolean backStack, String tag) {
        this.fragment = fragment;
        this.backStack = backStack;
        this.tag = tag;
    }


    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Boolean getBackStack() {
        return backStack;
    }

    public void setBackStack(Boolean backStack) {
        this.backStack = backStack;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
