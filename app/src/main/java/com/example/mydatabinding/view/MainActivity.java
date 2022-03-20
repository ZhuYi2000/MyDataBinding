package com.example.mydatabinding.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftPresenter = new LeftPresenter(this);
        loading_view = findViewById(R.id.progress_my_3);

        //进行状态栏的相关设置
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window,false); //沉浸式状态栏
        window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色为透明
        try {
            //设置状态栏文字为黑色
            ViewCompat.getWindowInsetsController(window.getDecorView()).setAppearanceLightStatusBars(true);
        }catch (Exception e){e.printStackTrace();}

        leftPresenter.getPokemonList(offset,limit);
    }

    @Override
    public void hideProcess() {
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
}