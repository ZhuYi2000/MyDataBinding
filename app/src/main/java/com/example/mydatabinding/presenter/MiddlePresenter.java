package com.example.mydatabinding.presenter;

import android.content.Context;

import com.amap.api.maps.model.LatLng;
import com.example.mydatabinding.enity.Trainer;
import com.example.mydatabinding.model.IMapModel;
import com.example.mydatabinding.model.MiddleModel;
import com.example.mydatabinding.view.IMapView;

import java.util.List;

public class MiddlePresenter implements IMapPresenter, MiddleModel.SearchCallback {

    Context context;
    IMapView middleFragment;
    IMapModel middleModel;

    public MiddlePresenter(Context context, IMapView middleFragment) {
        this.context = context;
        this.middleFragment = middleFragment;
    }

    @Override
    public void searchPokemon(LatLng currentLocation, int bound_meter) {
        //每次搜索都要清空上次的标记
        middleFragment.cleanMarker();
        middleModel = new MiddleModel(currentLocation,bound_meter,context,this);
        middleModel.getPokemonPosition("公园");
        middleModel.getPokemonPosition("烧烤");
        middleModel.getPokemonPosition("游泳馆");
        middleModel.getPokemonPosition("购物中心");
        middleFragment.hideProcess();
    }

    @Override
    public void isLogin(String p_name) {
        middleModel.isLogin(p_name);
    }

    //回调方法
    @Override
    public void success(List<LatLng> positionList,String this_keyword,List<String> addressList) {
        if(this_keyword.equals("公园"))
            middleFragment.setMarker(positionList,1,"妙蛙种子",addressList);
        if(this_keyword.equals("烧烤"))
            middleFragment.setMarker(positionList,4,"小火龙",addressList);
        if(this_keyword.equals("游泳馆"))
            middleFragment.setMarker(positionList,7,"杰尼龟",addressList);
        if(this_keyword.equals("购物中心"))
            middleFragment.setMarker(positionList,25,"皮卡丘",addressList);
    }

    @Override
    public void failure() {
        middleFragment.showErrorMessage();
    }

    @Override
    public void alreadyLogin(Trainer trainer, String p_name) {
        middleFragment.showAlreadyLogin(trainer,p_name);
    }

    @Override
    public void notLogin() {
        middleFragment.showNotLogin();
    }
}
