package com.example.mydatabinding.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydatabinding.R;
import com.example.mydatabinding.sensor.CameraPreview;

public class CameraActivity extends AppCompatActivity {

    private Context context;
    private final String TAG = "zhu";

    private FrameLayout mFrameLayout;
    private TextView pid_tv,hint_tv;

    int p_id;
    long t_id;
    String t_name;


    //动态请求权限
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
                if(isGranted){
                    //权限被允许
                    CameraPreview preview = new CameraPreview(context);
                    mFrameLayout.addView(preview);//相机在最底层

                    ImageView imageView = new ImageView(context);
                    FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(800,800, Gravity.CENTER);
                    fl.setMargins(0,0,0,400);
                    imageView.setLayoutParams(fl);
                    if(p_id == 1)imageView.setImageResource(R.drawable.go001);
                    if(p_id == 4)imageView.setImageResource(R.drawable.go004);
                    if(p_id == 7)imageView.setImageResource(R.drawable.go007);
                    if(p_id == 25)imageView.setImageResource(R.drawable.go025);

                    mFrameLayout.addView(imageView);//然后是3D精灵层

                    ImageView imageView_2 = new ImageView(context);
                    FrameLayout.LayoutParams fl_2 = new FrameLayout.LayoutParams(300,300, Gravity.CENTER);
                    fl_2.setMargins(0,650,0,0);
                    imageView_2.setLayoutParams(fl_2);
                    imageView_2.setImageResource(R.drawable.pokeball3d);
                    mFrameLayout.addView(imageView_2);

                }else {
                    //权限不被允许
                    Toast.makeText(context, "未开启权限，请到设置中手动打开", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context = this;
        Intent intent = getIntent();
        p_id = intent.getIntExtra("p_id",25);//获取宝可梦ID
        t_id = intent.getLongExtra("t_id",2022002);//获取用户ID
        t_name= intent.getStringExtra("t_name");//获取用户名
//        Log.d(TAG,p_id+"宝可梦ID");
//        Log.d(TAG,t_id+"训练师ID");
//        Log.d(TAG,t_name+":训练师名称");
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);//获取相机权限
        mFrameLayout = findViewById(R.id.preview_f);
        pid_tv = findViewById(R.id.camera_pid);

        if(p_id == 1)pid_tv.setText("野生精灵：妙蛙种子");
        if(p_id == 4)pid_tv.setText("野生精灵：小火龙");
        if(p_id == 7)pid_tv.setText("野生精灵：杰尼龟");
        if(p_id == 25)pid_tv.setText("野生精灵：皮卡丘");

        //进行状态栏的相关设置
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window,false); //沉浸式状态栏
        window.setStatusBarColor(Color.parseColor("#FB6556"));//设置状态栏颜色为透明
        window.setNavigationBarColor(Color.TRANSPARENT);
        try {
            //设置状态栏文字为黑色
            ViewCompat.getWindowInsetsController(window.getDecorView()).setAppearanceLightStatusBars(true);
        }catch (Exception e){e.printStackTrace();}
    }
}