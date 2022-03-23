package com.example.mydatabinding.model;

import com.amap.api.maps.model.LatLng;

/**
 * Model层接口---实现该接口的类负责实际的获取数据操作，如数据库读取、网络加载
 */
public interface IMapModel {
    void getPokemonPosition(String keyword);
}
