package com.example.mydatabinding.model;

import android.content.Context;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.mydatabinding.enity.Trainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MiddleModel implements IMapModel, PoiSearch.OnPoiSearchListener {

    PoiSearch.Query query;
    PoiSearch poiSearch;
    LatLng currentLocation;
    int bound_meter;
    Context context;
    SearchCallback callback;

    public MiddleModel(LatLng currentLocation, int bound_meter, Context context, SearchCallback callback) {
        this.currentLocation = currentLocation;
        this.bound_meter = bound_meter;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public void getPokemonPosition(String keyword) {
        query = new PoiSearch.Query(keyword,"","");
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(4);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查询页码
        try {
            //构造PoiSearch对象
            poiSearch = new PoiSearch(context, query);
            poiSearch.setOnPoiSearchListener(this);
            LatLonPoint temp = new LatLonPoint(currentLocation.latitude,currentLocation.longitude);
            //设置以用户定位为中心，进行周边搜索，范围bound_meter公里
            poiSearch.setBound(new PoiSearch.SearchBound(temp,bound_meter));
            //send request
            poiSearch.searchPOIAsyn();
        }catch (Exception e){
            e.printStackTrace();
            callback.failure();
        }

    }

    @Override
    public void isLogin(String p_name) {
        String file_url = context.getFilesDir().getPath()+"/trainerState.txt";
        File file = new File(file_url);
        if(file.exists()){
            Trainer trainer = new Trainer();
            try {
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String str_tid = bufferedReader.readLine();
                long t_id = Long.parseLong(str_tid);

                String password = bufferedReader.readLine();
                String trainer_name = bufferedReader.readLine();

                String str_timestamp = bufferedReader.readLine();
                long timestamp = Long.parseLong(str_timestamp);
                long current_time = (long) System.currentTimeMillis()/1000;
                long daySeconds = 86400;//一天有86400秒
                if(current_time-timestamp<daySeconds){
//                    Log.d("zhu","登录文件未过期");
                    trainer.setT_id(t_id);
                    trainer.setPassword(password);
                    trainer.setT_name(trainer_name);
                    callback.alreadyLogin(trainer,p_name);
                }else {
//                    Log.d("zhu","登录文件已过期");
                    callback.notLogin();
                }
                bufferedReader.close();
                reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            callback.notLogin();
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
    //返回结果成功或者失败的响应码。1000为成功，其他为失败
        if(rCode == 1000){
            String this_keyword = poiResult.getQuery().getQueryString();
            List<String> addressList = new ArrayList<>();
            List<PoiItem> poiItemList = poiResult.getPois();
            List<LatLng> positionList = new ArrayList<>();
            int size = poiItemList.size();
            for(int i=0;i<size;i++){
                LatLonPoint temp = poiItemList.get(i).getLatLonPoint();
                LatLng position = new LatLng(temp.getLatitude(),temp.getLongitude());
                positionList.add(position);
                addressList.add(poiItemList.get(i).getSnippet());
            }
            if(positionList.size()==size){
                callback.success(positionList,this_keyword,addressList);
            }

        }else {
            Log.e("zhu","失败代码："+rCode);
            callback.failure();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
    }

    public interface SearchCallback{
        void success(List<LatLng> positionList,String this_keyword,List<String> addressList);
        void failure();
        void alreadyLogin(Trainer trainer, String p_name);
        void notLogin();
    }
}
