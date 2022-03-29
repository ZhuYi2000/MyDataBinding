package com.example.mydatabinding.presenter;

import com.example.mydatabinding.enity.Trainer;

/**
 * Presenter层接口---控制Model层的数据操作及调用View层的UI操作来完成“中间人”工作
 */
public interface IDBPresenter {
    void loginTrainer(String t_name,String password);//用户登录
    void addTrainer(String t_name,String password);//用户注册
    //暂不提供删除方法
    void getPokemonByTrainer(long trainer_id);//根据用户ID查询其拥有的宝可梦ID
    void addPokemonByTrainer(long trainer_id,int pokemon_id);//增加用户捕捉到的一只宝可梦

    void isLogin();//查询本地数据，判断用户是否已经登录
    void saveLogin(Trainer trainer);//保存登录状态

    void trainerLogout(Trainer the_trainer);
}
