package com.example.mydatabinding.model;

/**
 * Model层接口---实现该接口的类负责实际的获取数据操作，如数据库读取、网络加载
 */
public interface IMapModel {
    void getPokemonPosition(String keyword);

    void isLogin(String p_name);//判断用户是否登录
}
