package com.example.mydatabinding.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydatabinding.R;
import com.example.mydatabinding.enity.Trainer;
import com.example.mydatabinding.presenter.IDBPresenter;
import com.example.mydatabinding.presenter.RightPresenter;
import com.example.mydatabinding.sensor.MiStepSensor;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * 使用本地的SQLite数据库进行是否已经登录的状态保存
 * 初始化逻辑：
 * boolean isLogin()查询SQLite数据库，判断是否已经登录或者登录是否过期
 * 如果未登录，显示注册/登录按钮，并提示用户注册
 * 如果已登录，显示用户详细信息(layout的属性由hide变为show)
 *
 * 点击注册按钮后：
 * 跳转到新的activity_register中，用户输入注册信息，进行合法性检查，送入云数据库中存储，查询云数据库，提示注册成功
 * 然后回退到本页面
 *
 * 点击登录按钮后：
 * 弹窗输入用户名称，密码，与云数据库进行比对，如果正确，显示用户详细信息，并将时间戳和登录用户ID称送入SQLite数据库中
 */
public class RightFragment extends Fragment implements IDBView,RightAdapter.OnItemClickListener{
    public IDBPresenter rightPresenter;
    public Context parent_context;
    public LinearLayout login_register,trainer_info;

    private final String TAG = "zhu";

    public Button login_btn,register_btn;
    public TextView trainer_name,trainer_id,logout_tv,none_pokemon_tv,todaySteps_tv;
    public AVLoadingIndicatorView progress;
    public RecyclerView recyclerView;

    public Trainer the_trainer = new Trainer();
    public List<Integer> pid_list = new ArrayList<>();

    private SensorManager stepManager;
    private Sensor stepSensor,stepDetector;
    private int step_count = 0;

    //监听步数传感器的数据，废弃，MIUI中不好用
    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.d(TAG,"回调事件");
            if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                Log.d(TAG,"DETECTOR");
                Toast.makeText(parent_context, "DETECTOR", Toast.LENGTH_SHORT).show();
            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                Toast.makeText(parent_context, "COUNTER", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"COUNTER");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://登录成功或者已经登录会调用这个方法
                    login_register.setVisibility(View.INVISIBLE);
                    trainer_name.setText("训练师："+the_trainer.getT_name());
                    trainer_id.setText("ID："+the_trainer.getT_id());
                    trainer_info.setVisibility(View.VISIBLE);
                    logout_tv.setVisibility(View.VISIBLE);

                    //获取已经捕捉到的精灵列表
                    rightPresenter.getPokemonByTrainer(the_trainer.getT_id());
                    break;
                case 2://背包中没有精灵
                    none_pokemon_tv.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                    break;
                case 3://背包中有精灵，设置rv
                    RightAdapter adapter = new RightAdapter(parent_context,recyclerView,pid_list);
                    adapter.setOnItemClickListener(RightFragment.this::onItemClick);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
            }
        }
    };


    public RightFragment(Context context) {
        parent_context = context;
    }

    public static RightFragment newInstance(Context context) {
        RightFragment fragment = new RightFragment(context);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_right, container, false);
        rightPresenter = new RightPresenter(parent_context,this);

        //绑定布局以及进度条
        //login_register是用户未登录时显示的布局
        //trainer_info是用户登录完毕后显示的布局
        login_register = view.findViewById(R.id.login_register);
        trainer_info = view.findViewById(R.id.trainer_info);
        progress = view.findViewById(R.id.progress_my_8);

        //设置rv布局及其adapter
        recyclerView = view.findViewById(R.id.right_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(parent_context,4));

        //绑定控件
        trainer_name = view.findViewById(R.id.trainer_name);
        trainer_id = view.findViewById(R.id.trainer_id);

        //用户没有宝可梦时，显示这一句
        none_pokemon_tv = view.findViewById(R.id.none_pokemon_tv);

        //每日步数，利用小米提供的接口实现
        todaySteps_tv = view.findViewById(R.id.today_steps);

        //登录以后显示在右上角的“退出登录”按钮
        logout_tv = view.findViewById(R.id.logout_tv);
        logout_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightPresenter.trainerLogout(the_trainer);
            }
        });

        //登录按钮
        login_btn = view.findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        //注册按钮
        register_btn = view.findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        //调用presenter.isLogin()方法，判断是否已经登录，如果已经登录，修改UI显示，隐藏login_register,显示trainer_info
        //如果未登录或者登录已经过期，则不用改变UI视图
        rightPresenter.isLogin();

        //初始化步数传感器，获取系统目前步数
        //stepManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        String brand = android.os.Build.BRAND;
        Log.d(TAG,brand);

        //只有在小米手机上，才能使用计步器接口
        if(brand.equals("Xiaomi")){
            MiStepSensor miStepSensor = new MiStepSensor(parent_context);
            int todaySteps = miStepSensor.getTodaySteps(todaySteps_tv);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //initStepSensor();
    }

    @Override
    public void onPause() {
        super.onPause();
        //stepManager.unregisterListener(mListener);
    }

    private void initStepSensor() {
        stepSensor = stepManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetector = stepManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepSensor!=null){
            Log.d(TAG,"stepCounter 获取成功");
            Log.d(TAG,"stepCounter:"+stepSensor.getResolution());
            Log.d(TAG,"stepCounter:"+stepSensor.getMaximumRange());
        }
        if(stepDetector!=null){
            Log.d(TAG,"stepDetector 获取成功");
            Log.d(TAG,"stepDetector:"+stepDetector.getResolution());
            Log.d(TAG,"stepDetector:"+stepDetector.getMaximumRange());
        }

//        Sensor testSensor = stepManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        stepManager.registerListener(this,testSensor,SensorManager.SENSOR_DELAY_NORMAL);
        stepManager.registerListener(mListener,stepSensor,SensorManager.SENSOR_DELAY_NORMAL,0);
        stepManager.registerListener(mListener,stepDetector,SensorManager.SENSOR_DELAY_NORMAL,0);

        Log.d(TAG,"步数初始化...");
//        List<Sensor> list=stepManager.getSensorList(Sensor.TYPE_ALL);
//        for(Sensor sensor:list){
//            Log.e(TAG, "initData: "+sensor.getName() );
//        }
    }

    /*
    ** 继承自接口的方法
     */

    @Override
    public void showErrorMessage() {
        Looper.prepare();
        Toast.makeText(parent_context, "操作失败！", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void showSuccessRegister() {
        Looper.prepare();
        Toast.makeText(parent_context, "注册成功，请登录！", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void showExistMessage(String t_name) {
        Looper.prepare();
        Toast.makeText(parent_context, "该用户名已存在："+t_name, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void showNotExitMessage() {
        Looper.prepare();
        Toast.makeText(parent_context, "登录失败，该用户不存在！", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void showPassError() {
        Looper.prepare();
        Toast.makeText(parent_context, "登录失败，密码错误！", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void showAfterLogin(Trainer trainer) {
        Looper.prepare();
        Toast.makeText(parent_context, "登录成功！", Toast.LENGTH_SHORT).show();
        the_trainer = trainer;
        Message msg = new Message();
        msg.what = 1;
        handler.sendMessage(msg);
        //应该调用presenter中的saveLogin(Trainer trainer)方法保存登录状态
        rightPresenter.saveLogin(trainer);
        Looper.loop();
    }

    @Override
    public void showAlreadyLogin(Trainer trainer) {
        the_trainer = trainer;
        Message msg = new Message();
        msg.what = 1;
        handler.sendMessage(msg);
    }

    @Override
    public void showNotLogin() {
        login_register.setVisibility(View.VISIBLE);
        trainer_info.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showAlreadyLogout(Trainer trainer) {
        the_trainer = new Trainer();
        Toast.makeText(parent_context, "训练师 "+trainer.getT_name()+" 已退出！", Toast.LENGTH_SHORT).show();
        //将所有UI的可视与否全都重置回初始状态
        login_register.setVisibility(View.VISIBLE);
        trainer_info.setVisibility(View.INVISIBLE);
        logout_tv.setVisibility(View.INVISIBLE);

        none_pokemon_tv.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void showNonePokemon() {
        Message msg = new Message();
        msg.what = 2;
        handler.sendMessage(msg);
    }

    @Override
    public void showAllPokemon(List<Integer> pid_list) {
        this.pid_list = pid_list;
        Message msg = new Message();
        msg.what = 3;
        handler.sendMessage(msg);
    }

    public void showRegisterDialog(){
        View view = LayoutInflater.from(parent_context).inflate(R.layout.dialog_register,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(parent_context).setView(view).create();
        EditText input_name = view.findViewById(R.id.register_name);
        EditText input_passwd_0 = view.findViewById(R.id.register_password);
        EditText input_passwd_1 = view.findViewById(R.id.register_password_1);
        Button dialog_register_btn = view.findViewById(R.id.dialog_register_btn);
        dialog_register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trainer_name = input_name.getText().toString().trim();
                String passwd = input_passwd_0.getText().toString().trim();
                String confirm_passwd = input_passwd_1.getText().toString().trim();

                if(passwd.equals(confirm_passwd) && !passwd.isEmpty()){
                    if(!trainer_name.isEmpty()){
                        rightPresenter.addTrainer(trainer_name,passwd);
                    }else {
                        Toast.makeText(parent_context, "用户名不能为空！", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(parent_context, "两次密码不一致！", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showLoginDialog(){
        View view = LayoutInflater.from(parent_context).inflate(R.layout.dialog_login,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(parent_context).setView(view).create();
        EditText input_name = view.findViewById(R.id.login_name);
        EditText input_password = view.findViewById(R.id.login_password);
        Button dialog_login_btn = view.findViewById(R.id.dialog_login_btn);

        dialog_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trainer_name = input_name.getText().toString().trim();
                String passwd = input_password.getText().toString().trim();
                if(trainer_name.isEmpty()||passwd.isEmpty()){
                    Toast.makeText(parent_context, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    rightPresenter.loginTrainer(trainer_name,passwd);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //监听rv的点击事件
    //打开新的宝可梦详情页面
    @Override
    public void onItemClick(RecyclerView parent, View view, int p_id) {
        Intent info_intent = new Intent(parent_context, PokemonInfoActivity.class);
        info_intent.putExtra("p_id",p_id);
        startActivity(info_intent);
    }


}