package com.example.mydatabinding.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydatabinding.R;
import com.example.mydatabinding.presenter.CameraPresenter;
import com.example.mydatabinding.presenter.IDBPresenter;
import com.example.mydatabinding.sensor.CameraPreview;
import com.example.mydatabinding.sensor.VibrateHelp;
import com.wang.avi.AVLoadingIndicatorView;

public class CameraActivity extends AppCompatActivity implements SensorEventListener {

    private Context context;
    private final String TAG = "zhu";

    private FrameLayout mFrameLayout;
    private TextView pid_tv,hint_tv;
    private IDBPresenter cameraPresenter;
    private ProgressBar horizontalPB;

    int p_id;
    long t_id;
    String t_name;
    boolean click_ball = false;

    int shake_num = 0;

    //传感器管理类用于管理线性加速度计
    private SensorManager sensorManager;
    private Sensor sensor;

    //处理子线程的UI更新
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //插入成功，显示语句
                    Toast.makeText(context, pid_tv.getText()+"捕捉成功！请去背包中查看！", Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    };


    //精灵球的点击事件，获取传感器陀螺仪、为其注册监听器
    private final View.OnClickListener ballClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            Toast.makeText(context, "点击精灵球", Toast.LENGTH_SHORT).show();
//            cameraPresenter.addPokemonByTrainer(t_id,p_id);//测试插入功能是否正常
            click_ball = true;
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            //获取线性加速度计
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            //注册监听器
            sensorManager.registerListener(CameraActivity.this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(context, "左右摇动手机以捕捉精灵！", Toast.LENGTH_LONG).show();
        }
    };


    //动态请求权限
    //以及对界面进行布局
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
                if(isGranted){
                    //权限被允许
                    CameraPreview preview = new CameraPreview(context);
                    mFrameLayout.addView(preview);//相机在最底层


                    horizontalPB.bringToFront();//将进度条层数提到相机前面


                    ImageView imageView_0 = new ImageView(context);
                    FrameLayout.LayoutParams fl_0 = new FrameLayout.LayoutParams(1600,1600, Gravity.CENTER);
                    fl_0.setMargins(0,0,10,20);
                    imageView_0.setLayoutParams(fl_0);
                    imageView_0.setImageResource(R.drawable.circle);

                    mFrameLayout.addView(imageView_0);//底部的圆形


                    ImageView imageView = new ImageView(context);
                    FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(800,800, Gravity.CENTER);
                    fl.setMargins(0,0,0,400);
                    imageView.setLayoutParams(fl);
                    if(p_id == 1)imageView.setImageResource(R.drawable.go001);
                    if(p_id == 4)imageView.setImageResource(R.drawable.go004);
                    if(p_id == 7)imageView.setImageResource(R.drawable.go007);
                    if(p_id == 25)imageView.setImageResource(R.drawable.go025);

                    mFrameLayout.addView(imageView);//然后是3D宝可梦层


                    ImageView imageView_2 = new ImageView(context);
                    FrameLayout.LayoutParams fl_2 = new FrameLayout.LayoutParams(500,500, Gravity.CENTER);
                    fl_2.setMargins(0,650,0,0);
                    imageView_2.setLayoutParams(fl_2);
                    imageView_2.setImageResource(R.drawable.pokeball3d);
                    imageView_2.setOnClickListener(ballClick);
                    mFrameLayout.addView(imageView_2);//然后是3D精灵球层

                    Toast.makeText(context, "点击精灵球开始捕捉！", Toast.LENGTH_SHORT).show();

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

        cameraPresenter = new CameraPresenter(this);

        Intent intent = getIntent();
        p_id = intent.getIntExtra("p_id",25);//获取宝可梦ID
        t_id = intent.getLongExtra("t_id",2022002);//获取用户ID
        t_name= intent.getStringExtra("t_name");//获取用户名
//        Log.d(TAG,p_id+"宝可梦ID");
//        Log.d(TAG,t_id+"训练师ID");
//        Log.d(TAG,t_name+":训练师名称");
        //申请获取相机权限
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);

        mFrameLayout = findViewById(R.id.preview_f);

        //根据不同的ID设置标题的不同
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

        //获取进度条
        horizontalPB = findViewById(R.id.horizontalPB);
    }

    //不用以后，取消对传感器的监听，节省电量
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果不加判断，会导致空指针错误
        if(click_ball) {
            sensorManager.unregisterListener(this);
        }
    }

    //cameraPresenter.addPokemonByTrainer的回调方法，捕捉成功后执行
    public void successCatch(long trainer_id,int pokemon_id){
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }


    //监听传感器，012分布是xyz轴的加速度（去除了重力的影响）
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION &&sensorEvent.values[0]>3){
//            Log.d(TAG,"x:"+sensorEvent.values[0]);
//            Log.d(TAG,"y:"+sensorEvent.values[1]);
//            Log.d(TAG,"z:"+sensorEvent.values[2]);
            shake_num+=1;
            if(shake_num<10) {
                horizontalPB.setProgress(shake_num * 10);
                //震动0.5s
                VibrateHelp.vSimple(context,500);
            }
            if(shake_num==10){
                cameraPresenter.addPokemonByTrainer(t_id,p_id);//捕捉精灵
                //震动2s
                VibrateHelp.vSimple(context,2000);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}