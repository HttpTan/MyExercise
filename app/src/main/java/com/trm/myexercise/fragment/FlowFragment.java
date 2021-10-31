package com.trm.myexercise.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.trm.myexercise.R;
import com.trm.myexercise.databinding.FlowLayoutBinding;

import java.util.Random;

public class FlowFragment extends BaseFragment implements View.OnClickListener {

    private FlowLayoutBinding binding;

    private final String[] PROGRAM_LANGUAGE =
            {"Java", "Python", "PHP", "Android", "ios",
                    "Android and ios", "Java and PHP", "PHP and Python", "ios and Java"};

    private Random random;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FlowLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        random = new Random();
        fillFlowLayout();
    }

    private void fillFlowLayout() {
        int count = 0;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                getResources().getDimensionPixelSize(R.dimen.flow_item_height));
        layoutParams.gravity = Gravity.CENTER;
        while (count++ < 20) {
            binding.flowLayoutSelect.addView(createView(), layoutParams);
        }
    }

    private View createView() {
        TextView textView = new TextView(getContext());
        textView.setBackgroundResource(R.drawable.flow_item_background);
        textView.setText(PROGRAM_LANGUAGE[random.nextInt(PROGRAM_LANGUAGE.length)]);
        textView.setPadding(getResources().getDimensionPixelSize(R.dimen.flow_item_corner), 0, getResources().getDimensionPixelSize(R.dimen.flow_item_corner), 0);
        textView.setGravity(Gravity.CENTER);

        textView.setOnClickListener(this);

        return textView;
    }

    @Override
    public void onClick(View v) {
        if (!(v instanceof TextView)) return;

        if (v.getParent() == binding.flowLayoutShow) {
            binding.flowLayoutShow.removeView(v);
            binding.flowLayoutSelect.addView(v);
        } else if (v.getParent() == binding.flowLayoutSelect) {
            binding.flowLayoutSelect.removeView(v);
            binding.flowLayoutShow.addView(v);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}
