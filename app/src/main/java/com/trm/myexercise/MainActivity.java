package com.trm.myexercise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.trm.myexercise.view.RoundProgressBar;

public class MainActivity extends AppCompatActivity {

    private RoundProgressBar mProgressbar;

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
    }
}
