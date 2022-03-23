package com.example.mydatabinding.view;

import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * View层接口---执行各种UI操作，定义的方法主要是给Presenter中来调用的
 */
public interface IMapView {
    void cleanMarker();
    void setMarker(List<LatLng> position_list,int pokemon_id,String pokemon_name,List<String> addressList);
    void hideProcess();
    void showErrorMessage();
}
