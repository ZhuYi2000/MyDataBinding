package com.example.mydatabinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.mydatabinding.databinding.ActivityMainBinding;
import com.example.mydatabinding.remote.PokemonInfoService;
import com.wang.avi.AVLoadingIndicatorView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "zhu";
    private PokemonInfoService infoService;
    private AVLoadingIndicatorView loading_view;
    private int offset = 0;
    private int limit = 200;

    //用于更新UI
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://代表初始数据加载完毕
                    loading_view.hide();
                    break;
            }
        }
    };

    //观察者
    private Observer<List<String>> observer = new Observer<List<String>>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG, "开始采用subscribe连接");
        }

        @Override
        public void onNext(@NonNull List<String> strings) {
            Log.d(TAG, "对Next事件作出响应" + strings.get(0));
            //引入LeftFragment
            LeftFragment leftFragment = new LeftFragment(strings);
            replaceFragment(leftFragment);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
            Log.d(TAG, "对Error事件作出响应");
        }

        @Override
        public void onComplete() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
            Log.d(TAG, "对Complete事件作出响应");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        User user = new User("zhu","yi");
        binding.setUser(user);
        loading_view = findViewById(R.id.progress_my_3);

        Window window = getWindow();

        WindowCompat.setDecorFitsSystemWindows(window,false); //沉浸式状态栏
        window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色为透明
        try {
            //设置状态栏文字为黑色
            ViewCompat.getWindowInsetsController(window.getDecorView()).setAppearanceLightStatusBars(true);
        }catch (Exception e){e.printStackTrace();}

        //被观察者
        Observable<List<String>> listObs = Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<String>> emitter) throws Throwable {
                new Thread(){
                    @Override
                    public void run(){
                        super.run();
                        PokemonInfoService api_service = new PokemonInfoService("https://pokeapi.co/api/v2/");
                        List<String> p_name_list = new ArrayList<>();
                        p_name_list = api_service.getPokemonListAsync(offset,limit);
                        for(int i=0;i<200;i++){
                            try {
                                Thread.sleep(100);//休眠,等待异步数据加载完毕
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(p_name_list.size()==limit){
                                break;
                            }
                        }

                        if(p_name_list.size()==limit) {
                            emitter.onNext(p_name_list);
                            emitter.onComplete();
                        }else {Log.e(TAG,"网络异常，加载失败！");}
                    }
                }.start();
            }
        });

        listObs.subscribe(observer);

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();//通过manager获取触发器
        transaction.replace(R.id.my_frameLayout,fragment);
        //transaction.addToBackStack(null);  //将fragment入栈，在用户点击返回键时，逐个出栈
        transaction.commit();
    }
}