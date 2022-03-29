package com.example.mydatabinding.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.mydatabinding.R;
import com.example.mydatabinding.enity.Trainer;
import com.example.mydatabinding.presenter.IMapPresenter;
import com.example.mydatabinding.presenter.MiddlePresenter;

import java.util.ArrayList;
import java.util.List;

public class MiddleFragment extends Fragment implements IMapView, AMap.OnMyLocationChangeListener {

    private static final String TAG = "zhu";
    private TextureMapView textureMapView;
    private AMap aMap;
    private final Context parent_context;
    private List<Marker> markerList = new ArrayList<>();
    private LatLng currentLocation = new LatLng(0,0);
    private IMapPresenter middlePresenter;

    public MiddleFragment(Context context) {
        this.parent_context = context;
    }

    public static MiddleFragment newInstance(Context context) {
        MiddleFragment fragment = new MiddleFragment(context);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐私接口,必须设置为true
        MapsInitializer.updatePrivacyShow(parent_context,true,true);
        MapsInitializer.updatePrivacyAgree(parent_context,true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_middle, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textureMapView = (TextureMapView) getView().findViewById(R.id.map);
        if(textureMapView!=null){
            textureMapView.onCreate(savedInstanceState);
            aMap = textureMapView.getMap();
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            //初始化定位蓝点样式类
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
            // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。

            myLocationStyle.interval(20000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。

            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style

            aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。

            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

            aMap.setOnMyLocationChangeListener(this);

            AMap.OnInfoWindowClickListener infoListener = new AMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker arg0) {
                    showAlertDialog(arg0.getTitle());
                }
            };
            //绑定信息窗点击事件
            aMap.setOnInfoWindowClickListener(infoListener);

            //初始化presenter
            middlePresenter = new MiddlePresenter(parent_context,this);

            //开始搜索,但是需要等待1s以便系统进行定位操作
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Looper.prepare();
                        while (currentLocation.latitude==0.0){
                            Thread.sleep(100);
                        }
                        Log.e("zhu","LAT"+currentLocation.latitude);
                        Log.e("zhu","Lon"+currentLocation.longitude);
                        middlePresenter.searchPokemon(currentLocation,3000);
                        Looper.loop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        Log.d(TAG,"onActivityCreated");
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        textureMapView.onResume();
        Log.d(TAG,"onResume");
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        textureMapView.onPause();
        Log.d(TAG,"onPause");
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        textureMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        textureMapView.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    /**
     * 回调函数
     */
    @Override
    public void cleanMarker() {
        if(!markerList.isEmpty()){
            for(int i=0;i<markerList.size();i++){
                markerList.get(i).destroy();
            }
        }
    }

    @Override
    public void setMarker(List<LatLng> position_list, int pokemon_id, String pokemon_name, List<String> addressList) {
        int size = position_list.size();
        for(int i=0;i<size;i++){
            MarkerOptions options = new MarkerOptions();
            options.position(position_list.get(i));//坐标
            options.title(pokemon_name);//姓名
            options.snippet(addressList.get(i));//位置
            //根据不同的ID设置不同的icon
            if(pokemon_id==1) options.icon(BitmapDescriptorFactory.fromResource(R.drawable.go001_small));
            if(pokemon_id==4) options.icon(BitmapDescriptorFactory.fromResource(R.drawable.go004_small));
            if(pokemon_id==7) options.icon(BitmapDescriptorFactory.fromResource(R.drawable.go007_small));
            if(pokemon_id==25) options.icon(BitmapDescriptorFactory.fromResource(R.drawable.go025_small));
            Marker marker = aMap.addMarker(options);
            markerList.add(marker);
        }
    }

    @Override
    public void hideProcess() {
        Toast.makeText(parent_context, "搜索成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(parent_context, "搜索失败！！！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNotLogin() {
        Toast.makeText(parent_context, "还没有登录，请登录！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAlreadyLogin(Trainer trainer, String p_name) {
        long t_id = trainer.getT_id();
        String t_name = trainer.getT_name();
        int p_id = 0;
        if(p_name.equals("妙蛙种子")) p_id = 1;
        if(p_name.equals("小火龙")) p_id = 4;
        if(p_name.equals("杰尼龟")) p_id = 7;
        if(p_name.equals("皮卡丘")) p_id = 25;
        Intent camera_intent = new Intent(parent_context,CameraActivity.class);
        camera_intent.putExtra("p_id",p_id);
        camera_intent.putExtra("t_id",t_id);
        camera_intent.putExtra("t_name",t_name);
        startActivity(camera_intent);
    }

    /**
     * 位置监听
     */
    @Override
    public void onMyLocationChange(Location location) {
        currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
    }

    //捕捉界面从这里打开
    public void showAlertDialog(String p_name){
        //创建构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(parent_context);
        builder.setIcon(R.drawable.pokeball3d)
                .setTitle("恭喜！可捕捉的精灵“"+p_name+"”")
                .setMessage("你需要在游戏世界中寻找宝可梦并捕捉它们，千里之行，始于足下，动起来去寻找更多的宝可梦吧！")
                .setPositiveButton("捕捉精灵", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //预留的捕捉功能的接口
                        //Toast.makeText(parent_context, "按下捕捉："+p_name, Toast.LENGTH_SHORT).show();
                        //捕捉时需要判断是否登录，若已经登录，则打开对应页面
                        middlePresenter.isLogin(p_name);
                    }
                })
                .setNeutralButton("查看精灵信息", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(parent_context, "按下查看："+p_name, Toast.LENGTH_SHORT).show();
                        int p_id = 0;
                        if(p_name.equals("妙蛙种子")) p_id = 1;
                        if(p_name.equals("小火龙")) p_id = 4;
                        if(p_name.equals("杰尼龟")) p_id = 7;
                        if(p_name.equals("皮卡丘")) p_id = 25;
                        Intent info_intent = new Intent(parent_context, PokemonInfoActivity.class);
                        info_intent.putExtra("p_id",p_id);
                        startActivity(info_intent);
                    }
                })
                .create()
                .show();
    }
}