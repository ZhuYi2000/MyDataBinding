package com.example.mydatabinding.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
public class RightFragment extends Fragment implements IDBView{
    public IDBPresenter rightPresenter;
    public Context parent_context;
    public LinearLayout login_register,trainer_info;

    public Button login_btn,register_btn;
    public TextView trainer_name,trainer_id,logout_tv;

    public Trainer the_trainer = new Trainer();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    login_register.setVisibility(View.INVISIBLE);
                    trainer_name.setText("训练师："+the_trainer.getT_name());
                    trainer_id.setText("ID："+the_trainer.getT_id());
                    trainer_info.setVisibility(View.VISIBLE);
                    logout_tv.setVisibility(View.VISIBLE);
                    break;
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

        login_register = view.findViewById(R.id.login_register);
        trainer_info = view.findViewById(R.id.trainer_info);

        //调用presenter.isLogin()方法，判断是否已经登录，如果已经登录，修改UI显示，隐藏login_register,显示trainer_info
        //如果未登录或者登录已经过期，则不用改变UI视图
        rightPresenter.isLogin();

        trainer_name = view.findViewById(R.id.trainer_name);
        trainer_id = view.findViewById(R.id.trainer_id);

        logout_tv = view.findViewById(R.id.logout_tv);
        logout_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightPresenter.trainerLogout(the_trainer);
            }
        });

        login_btn = view.findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        register_btn = view.findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        return view;
    }

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
        login_register.setVisibility(View.VISIBLE);
        trainer_info.setVisibility(View.INVISIBLE);
        logout_tv.setVisibility(View.INVISIBLE);
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
}