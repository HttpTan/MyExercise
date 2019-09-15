package com.trm.myexercise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.trm.myexercise.view.RoundProgressBar;

public class MainActivity extends AppCompatActivity {

    private RoundProgressBar mProgressbar;

    private ProgressBar loading_to_call;
    private ValueAnimator objectAnimator;
    private static final long TIME_OUT_LENGTH = 8000;
    private static final String PROGRESS_PROPERTY = "progress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressbar = findViewById(R.id.round_progressbar);

        mProgressbar.setClickListener(new RoundProgressBar.ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(MainActivity.this, "点击了", Toast.LENGTH_SHORT).show();

            }
        });

        mProgressbar.setProgressChangeListener(new RoundProgressBar.ProgressChangeListener() {
            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "完成了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgressChanged(int progress) {

            }
        });


        loading_to_call = findViewById(R.id.img_loading);
        initCallProgress();

        objectAnimator.start();
    }



    private void initCallProgress() {
        loading_to_call.setProgress(0);

        objectAnimator = ObjectAnimator
                .ofInt(loading_to_call, PROGRESS_PROPERTY, loading_to_call.getMax())
                .setDuration(TIME_OUT_LENGTH);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(MainActivity.this, "完成了", Toast.LENGTH_SHORT).show();
            }
        });
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //tv_action_go.setText(String.format("%s秒", String.valueOf(5 - valueAnimator.getCurrentPlayTime() / 1000)));
            }
        });
        objectAnimator.setInterpolator(new LinearInterpolator());
    }
}
