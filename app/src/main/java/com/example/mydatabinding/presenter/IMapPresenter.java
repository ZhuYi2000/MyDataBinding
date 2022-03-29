package com.example.mydatabinding.presenter;

import com.amap.api.maps.model.LatLng;

/**
 * Presenter层接口---控制Model层的数据操作及调用View层的UI操作来完成“中间人”工作
 */
public interface IMapPresenter {
    void searchPokemon(LatLng currentLocation,int bound_meter);

    void isLogin(String p_name);//点击捕捉时，判断用户是否登录
}
