package com.trm.myexercise;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trm.myexercise.view.FlowLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String[] PROGRAME_LANGUAGE =
            {"Java", "Python", "PHP", "Android", "ios",
                    "Android and ios", "Java and PHP", "PHP and Python", "ios and Java"};

    private Random random;

    private FlowLayout mShowFlowLayout;
    private FlowLayout mSelectFlowLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flow_layout);

        init();
        fillFlowLayout();

    }

    private void init() {
        random = new Random();
        mShowFlowLayout = findViewById(R.id.flow_layout_show);
        mSelectFlowLayout = findViewById(R.id.flow_layout_select);
    }

    private void fillFlowLayout() {
        int count = 0;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                getResources().getDimensionPixelSize(R.dimen.flow_item_height));
        layoutParams.gravity = Gravity.CENTER;
        while (count++ < 20) {
            mSelectFlowLayout.addView(createView(), layoutParams);
        }
    }

    private View createView() {
        TextView textView = new TextView(this);
        textView.setBackgroundResource(R.drawable.flow_item_background);
        textView.setText(PROGRAME_LANGUAGE[random.nextInt(PROGRAME_LANGUAGE.length)]);
        textView.setPadding(getResources().getDimensionPixelSize(R.dimen.flow_item_corner), 0, getResources().getDimensionPixelSize(R.dimen.flow_item_corner), 0);
        textView.setGravity(Gravity.CENTER);

        textView.setOnClickListener(this);

        return textView;
    }

    @Override
    public void onClick(View v) {
        if (!(v instanceof TextView)) return;

        if (v.getParent() == mShowFlowLayout) {
            mShowFlowLayout.removeView(v);
            mSelectFlowLayout.addView(v);
        } else if (v.getParent() == mSelectFlowLayout) {
            mSelectFlowLayout.removeView(v);
            mShowFlowLayout.addView(v);
        }

    }
}
