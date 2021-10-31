package com.trm.myexercise.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.trm.myexercise.databinding.ScaleLayoutBinding;

public class ScaleViewFragment extends BaseFragment {

    private ScaleLayoutBinding binding;

    private MutableLiveData<Float> freLiveData = new MutableLiveData<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ScaleLayoutBinding.inflate(inflater);

        freLiveData.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                binding.content.setText(aFloat.toString());
            }
        });

        binding.scaleView.setListener(value -> freLiveData.postValue(value));
        binding.scaleView.post(() -> freLiveData.postValue(binding.scaleView.getPointerValue()));

        return binding.getRoot();
    }

}