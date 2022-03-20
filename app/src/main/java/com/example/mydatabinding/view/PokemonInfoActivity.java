package com.example.mydatabinding.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.mydatabinding.R;
import com.example.mydatabinding.enity.Pokemon;
import com.example.mydatabinding.presenter.IPresenter;
import com.example.mydatabinding.presenter.LeftInfoPresenter;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class PokemonInfoActivity extends AppCompatActivity implements IView {

    private IPresenter leftInfoPresenter;

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

    private final String TAG = "zhu";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_info);

        //设置状态栏
        Window window = getWindow();
        //沉浸式状态栏
        WindowCompat.setDecorFitsSystemWindows(window,false);
        //状态栏颜色透明
        window.setStatusBarColor(Color.TRANSPARENT);
        //状态栏字体颜色黑色
        try {
            ViewCompat.getWindowInsetsController(window.getDecorView()).setAppearanceLightStatusBars(true);
        }catch (Exception e){e.printStackTrace();}

        Intent intent = getIntent();
        int p_id = intent.getIntExtra("p_id",1);//获取宝可梦ID

        //绑定UI控件
        fvbID();

        leftInfoPresenter = new LeftInfoPresenter(this);
        //获取属性信息
        leftInfoPresenter.getOnePokemon(p_id);
        //获取图片资源
        List<ImageView> imageViewList = new ArrayList<>();
        imageViewList.add(bigImageView);
        imageViewList.add(imageView1);
        imageViewList.add(imageView2);
        imageViewList.add(imageView3);
        imageViewList.add(imageView4);
        List<String> urls = new ArrayList<>();
        urls.add("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+p_id+".png");
        urls.add("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+p_id+".png");
        urls.add("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/"+p_id+".png");
        urls.add("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/"+p_id+".png");
        urls.add("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/shiny/"+p_id+".png");
        leftInfoPresenter.loadImage(this,imageViewList,urls,5);

    }

    //findViewById
    public void fvbID(){
        //进行绑定操作
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


        nameTv = findViewById(R.id.pokemon_name);
        bigImageView = findViewById(R.id.pokemon_img);

        imageView1 = findViewById(R.id.pokemon_img_1);
        imageView2 = findViewById(R.id.pokemon_img_2);
        imageView3 = findViewById(R.id.pokemon_img_3);
        imageView4 = findViewById(R.id.pokemon_img_4);
    }

    //设置具体数值
    @Override
    public void setData(Pokemon pokemon){
        attrLayout.setVisibility(View.VISIBLE);

        atk_bar.setProgress(pokemon.getAttack());
        atkTv.setText("攻击      "+pokemon.getAttack()+"/150");
        defence_bar.setProgress(pokemon.getDefense());
        defenceTv.setText("防御      "+pokemon.getDefense()+"/150");
        speed_bar.setProgress(pokemon.getSpeed());
        speedTv.setText("速度      "+pokemon.getSpeed()+"/150");
        hp_bar.setProgress(pokemon.getHp());
        hpTv.setText(pokemon.getHp()+"/150 HP");

        nameTv.setText(pokemon.getName());
        weightTv.setText("重 "+pokemon.getWeight()+" KG");
        heightTv.setText("高 "+pokemon.getHeight()+" M");
    }

    @Override
    public void hideProcess() {
        loadingIndicatorView.hide();
    }

    @Override
    public void loadFragment(Fragment fragment) {

    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(this, "网络异常，数据加载失败", Toast.LENGTH_SHORT).show();
    }
}