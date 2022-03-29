package com.example.mydatabinding.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.mydatabinding.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载启动页面
        setContentView(R.layout.activity_splash);
        //设置等待时间，单位ms
        Integer time = 2000;
        //设置状态栏
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window,false); //沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色为透明
        window.setNavigationBarColor(Color.parseColor("#FB6556"));//设置状态栏颜色为透明
//        try {
//            //设置状态栏文字为黑色
//            ViewCompat.getWindowInsetsController(window.getDecorView()).setAppearanceLightStatusBars(true);
//        }catch (Exception e){e.printStackTrace();}

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivity = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        },time);
    }
}