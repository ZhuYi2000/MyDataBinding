package com.example.mydatabinding.model;

import android.content.Context;

import com.example.mydatabinding.enity.Trainer;

/**
 * Model层接口---实现该接口的类负责实际的获取数据操作，如数据库读取、网络加载
 */
public interface IDBModel {
    void selectAllTrainerName();//查询所有的用户名称，用以判重
    void selectTrainerByName(String trainer_name);//根据用户名称查询对应用户
    void insertTrainer(String t_name,String password);//用户注册
    //暂不提供删除方法
    void selectPokemonByTrainer(long trainer_id);//根据用户ID查询其拥有的宝可梦ID
    void insertPokemonByTrainer(long trainer_id,int pokemon_id);//增加用户捕捉到的一只宝可梦

    void isLogin(Context context);//查询本地SQLite数据库
    void saveLogin(Trainer trainer, Context context);//保存登录状态

    void logout(Trainer the_trainer, Context context);
}
