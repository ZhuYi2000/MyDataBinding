package com.example.mydatabinding.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydatabinding.R;
import com.example.mydatabinding.enity.Pokemon;
import com.example.mydatabinding.presenter.IPresenter;
import com.example.mydatabinding.presenter.LeftPresenter;

import com.wang.avi.AVLoadingIndicatorView;


public class MainActivity extends AppCompatActivity implements IView {

    private final String TAG = "zhu";
    private AVLoadingIndicatorView loading_view;
    private int offset = 0;
    private int limit = 400;
    private IPresenter leftPresenter;

    private LinearLayout left,middle,right;
    private ImageView left_iv,middle_iv,right_iv;
    private TextView left_tv,middle_tv,right_tv;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftPresenter = new LeftPresenter(this);
        loading_view = findViewById(R.id.progress_my_3);

        //底部导航栏设置
        left = findViewById(R.id.tab_left);
        left_iv = findViewById(R.id.tab_left_iv);
        left_tv = findViewById(R.id.tab_left_tv);

        middle = findViewById(R.id.tab_middle);
        middle_iv = findViewById(R.id.tab_middle_iv);
        middle_tv = findViewById(R.id.tab_middle_tv);

        right = findViewById(R.id.tab_right);
        right_iv = findViewById(R.id.tab_right_iv);
        right_tv = findViewById(R.id.tab_right_tv);

        context = this;

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!left_iv.isSelected()){
                    leftPresenter.getPokemonList(offset,limit);
                }

                left_iv.setSelected(true);
                left_tv.setTextColor(Color.WHITE);

                middle_iv.setSelected(false);
                middle_tv.setTextColor(Color.BLACK);

                right_iv.setSelected(false);
                right_tv.setTextColor(Color.BLACK);
            }
        });

        middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!middle_iv.isSelected()){
                    loadFragment(new MiddleFragment(context));
                }
                middle_iv.setSelected(true);
                middle_tv.setTextColor(Color.WHITE);

                left_iv.setSelected(false);
                left_tv.setTextColor(Color.BLACK);

                right_iv.setSelected(false);
                right_tv.setTextColor(Color.BLACK);


            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!right_iv.isSelected()){
                    loadFragment(new RightFragment(context));
                }
                right_iv.setSelected(true);
                right_tv.setTextColor(Color.WHITE);

                left_iv.setSelected(false);
                left_tv.setTextColor(Color.BLACK);

                middle_iv.setSelected(false);
                middle_tv.setTextColor(Color.BLACK);
            }
        });


        //进行状态栏的相关设置
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window,false); //沉浸式状态栏
        window.setStatusBarColor(Color.parseColor("#FB6556"));//设置状态栏颜色为透明
        window.setNavigationBarColor(Color.parseColor("#FB6556"));
        try {
            //设置状态栏文字为黑色
            ViewCompat.getWindowInsetsController(window.getDecorView()).setAppearanceLightStatusBars(true);
        }catch (Exception e){e.printStackTrace();}

        //请求并加载数据
        leftPresenter.getPokemonList(offset,limit);
    }

    @Override
    public void hideProcess() {
        //默认界面是左侧
        left_iv.setSelected(true);
        left_tv.setTextColor(Color.WHITE);
        loading_view.hide();
    }

    @Override
    public void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();//通过manager获取触发器
        transaction.replace(R.id.my_frameLayout,fragment);
        //transaction.addToBackStack(null);  //将fragment入栈，在用户点击返回键时，逐个出栈
        transaction.commit();
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(this, "网络异常，数据加载失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(Pokemon pokemon) {

    }

    public void clickLeftBtn(View view) {

    }
}