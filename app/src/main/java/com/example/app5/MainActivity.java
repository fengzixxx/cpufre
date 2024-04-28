package com.example.app5;

import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import android.os.Bundle;
import android.view.View;

import com.gsls.gt.GT;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GT.startFloatingWindow(MainActivity.this, com.example.app5.DemoFloatingWindow.class);//启动悬浮窗
            }
        });

    }

}