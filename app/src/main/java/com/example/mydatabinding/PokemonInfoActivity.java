package com.example.mydatabinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.mydatabinding.enity.Pokemon;
import com.example.mydatabinding.remote.PokemonInfoService;
import com.wang.avi.AVLoadingIndicatorView;

public class PokemonInfoActivity extends AppCompatActivity {

    private ImageView bigImageView;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private TextView nameTv;
    private TextView atkTv;
    private TextView defenceTv;
    private TextView speedTv;
    private TextView hpTv;
    private TextView weightTv;
    private TextView heightTv;


    private LinearLayout attrLayout;
    private AVLoadingIndicatorView loadingIndicatorView;
    private RoundCornerProgressBar atk_bar;
    private RoundCornerProgressBar defence_bar;
    private RoundCornerProgressBar speed_bar;
    private RoundCornerProgressBar hp_bar;

    private Pokemon thePokemon;

    private final String TAG = "zhu";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://代表获取到了信息
                    setData();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_info);

        //设置状态栏
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window,false); //沉浸式状态栏
        window.setStatusBarColor(Color.TRANSPARENT);//状态栏颜色透明
        try {
            //状态栏字体颜色黑色
            ViewCompat.getWindowInsetsController(window.getDecorView()).setAppearanceLightStatusBars(true);
        }catch (Exception e){e.printStackTrace();}

        loadingIndicatorView = findViewById(R.id.progress_my_4);
        attrLayout = findViewById(R.id.attr_layout);

        atk_bar = findViewById(R.id.atk_bar);
        defence_bar = findViewById(R.id.defence_bar);
        speed_bar = findViewById(R.id.speed_bar);
        hp_bar = findViewById(R.id.hp_bar);

        atkTv = findViewById(R.id.atk_tv);
        defenceTv = findViewById(R.id.defence_tv);
        speedTv = findViewById(R.id.speed_tv);
        hpTv = findViewById(R.id.hp_tv);
        weightTv = findViewById(R.id.weight_tv);
        heightTv = findViewById(R.id.height_tv);

        Intent intent = getIntent();
        int p_id = intent.getIntExtra("p_id",1);//获取宝可梦ID
        nameTv = findViewById(R.id.pokemon_name);
        bigImageView = findViewById(R.id.pokemon_img);

        imageView1 = findViewById(R.id.pokemon_img_1);
        imageView2 = findViewById(R.id.pokemon_img_2);
        imageView3 = findViewById(R.id.pokemon_img_3);
        imageView4 = findViewById(R.id.pokemon_img_4);

        loadImageView(p_id);
        new Thread(){
            @Override
            public void run() {
                super.run();
                PokemonInfoService api_service = new PokemonInfoService("https://pokeapi.co/api/v2/");
                thePokemon = api_service.getOnePokemonAsync(p_id);
                for(int i=0;i<200;i++){
                    try {
                        Thread.sleep(100);//休眠,等待异步数据加载完毕
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(thePokemon.getName()!=null) break;
                }
                if(thePokemon.getName()!=null){
                    Log.d(TAG,"宝可梦数据获取成功！");
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }else {
                    Looper.prepare();
                    Toast.makeText(PokemonInfoActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }.start();
    }

    public void loadImageView(int id){
        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+id+".png";

        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)  //用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存所有图片(原图,转换图)
                .fitCenter();

        Glide.with(this).load(url).apply(options).
                thumbnail(Glide.with(bigImageView).load(R.drawable.loading)).into(bigImageView);

        String url1 = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+id+".png";
        String url2 = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/"+id+".png";
        String url3 = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/"+id+".png";
        String url4 = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/shiny/"+id+".png";

        Glide.with(this).load(url1).apply(options).
                thumbnail(Glide.with(imageView1).load(R.drawable.loading)).into(imageView1);

        Glide.with(this).load(url2).apply(options).
                thumbnail(Glide.with(imageView2).load(R.drawable.loading)).into(imageView2);

        Glide.with(this).load(url3).apply(options).
                thumbnail(Glide.with(imageView3).load(R.drawable.loading)).into(imageView3);

        Glide.with(this).load(url4).apply(options).
                thumbnail(Glide.with(imageView4).load(R.drawable.loading)).into(imageView4);

    }

    public void setData(){
        loadingIndicatorView.hide();
        attrLayout.setVisibility(View.VISIBLE);

        atk_bar.setProgress(thePokemon.getAttack());
        atkTv.setText("攻击      "+thePokemon.getAttack()+"/150");
        defence_bar.setProgress(thePokemon.getDefense());
        defenceTv.setText("防御      "+thePokemon.getDefense()+"/150");
        speed_bar.setProgress(thePokemon.getSpeed());
        speedTv.setText("速度      "+thePokemon.getSpeed()+"/150");
        hp_bar.setProgress(thePokemon.getHp());
        hpTv.setText(thePokemon.getHp()+"/150 HP");

        nameTv.setText(thePokemon.getName());
        weightTv.setText("重 "+thePokemon.getWeight()+" KG");
        heightTv.setText("高 "+thePokemon.getHeight()+" M");
    }
}