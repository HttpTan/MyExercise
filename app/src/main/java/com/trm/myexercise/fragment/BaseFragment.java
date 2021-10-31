package com.trm.myexercise.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    public static <T> T newInstance(Class<? extends BaseFragment> tClass) {
        try {
            return (T) tClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public @LayoutRes int getLayout() {
        return 0;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayout() == 0) {
            return null;
        }

        return inflater.inflate(getLayout(), container, false);
    }
}
