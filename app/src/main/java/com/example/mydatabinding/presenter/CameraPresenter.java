package com.example.mydatabinding.presenter;

import com.example.mydatabinding.enity.Trainer;
import com.example.mydatabinding.model.CameraModel;
import com.example.mydatabinding.model.IDBModel;
import com.example.mydatabinding.view.CameraActivity;

public class CameraPresenter implements IDBPresenter, CameraModel.CameraCallback {

    final String TAG = "zhu";
    private IDBModel cameraModel = new CameraModel(this);
    private CameraActivity cameraActivity;

    public CameraPresenter(CameraActivity cameraActivity) {
        this.cameraActivity = cameraActivity;
    }

    //为用户背包增加一条记录
    @Override
    public void addPokemonByTrainer(long trainer_id, int pokemon_id) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                cameraModel.insertPokemonByTrainer(trainer_id,pokemon_id);
            }
        }.start();
    }
    //以下方法不用管,useless方法
    @Override
    public void loginTrainer(String t_name, String password) {

    }
    @Override
    public void addTrainer(String t_name, String password) {

    }
    @Override
    public void getPokemonByTrainer(long trainer_id) {

    }
    @Override
    public void isLogin() {

    }
    @Override
    public void saveLogin(Trainer trainer) {

    }
    @Override
    public void trainerLogout(Trainer the_trainer) {

    }

    //回调方法
    @Override
    public void successInsert(long trainer_id, int pokemon_id) {
        cameraActivity.successCatch(trainer_id,pokemon_id);
    }

    @Override
    public void failureInsert() {

    }
}
