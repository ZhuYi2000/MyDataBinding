package com.example.mydatabinding.presenter;

import android.content.Context;
import android.util.Log;

import com.example.mydatabinding.enity.Trainer;
import com.example.mydatabinding.model.IDBModel;
import com.example.mydatabinding.model.RightModel;
import com.example.mydatabinding.view.IDBView;

import java.util.ArrayList;
import java.util.List;

public class RightPresenter implements IDBPresenter, RightModel.DataBaseCallback {
    final String TAG = "zhu";
    IDBModel rightModel = new RightModel(this);
    Context context;
    IDBView rightFragment;

    List<String> trainer_name_list = new ArrayList<>();
    String temp_password;

    public RightPresenter(Context context, IDBView rightFragment) {
        this.context = context;
        this.rightFragment = rightFragment;
    }

    @Override
    public void loginTrainer(String t_name, String password) {
        temp_password = password;
        new Thread(){
            @Override
            public void run() {
                super.run();
                rightModel.selectTrainerByName(t_name);
            }
        }.start();
    }

    @Override
    public void addTrainer(String t_name, String password) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                boolean exist = false;
                rightModel.selectAllTrainerName();//查询所有用户名称
                try {
                    //等待返回结果
                    while (trainer_name_list.isEmpty()){
                        Thread.sleep(100);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                for(int i=0;i<trainer_name_list.size();i++){
                    if(t_name.equals(trainer_name_list.get(i))){
                        exist = true;
                    }
                    Log.d(TAG,trainer_name_list.get(i));
                }
                if(exist){
                    Log.d(TAG,"存在该用户");
                    rightFragment.showExistMessage(t_name);
                }else{
                    rightModel.insertTrainer(t_name,password);
                }
            }
        }.start();
    }

    @Override
    public void getPokemonByTrainer(long trainer_id) {

    }

    @Override
    public void addPokemonByTrainer(long trainer_id, long pokemon_id) {

    }

    @Override
    public void isLogin() {

    }


    //回调函数
    @Override
    public void successAllTrainerName(List<String> name_list) {
        trainer_name_list = name_list;
    }

    @Override
    public void successTrainerByName(Trainer trainer) {
        Log.d(TAG,trainer.getT_name());
        Log.d(TAG,trainer.getT_id()+"");
        Log.d(TAG,trainer.getPassword());
        if(trainer.getPassword().equals(temp_password)){
            rightFragment.showAfterLogin(trainer);
        }else {
            rightFragment.showPassError();
        }
    }

    @Override
    public void failureTrainerByName(Trainer trainer) {
        rightFragment.showNotExitMessage();
    }

    @Override
    public void successInsertTrainer() {
        rightFragment.showSuccessRegister();
    }

    @Override
    public void failureInsertTrainer() {
        rightFragment.showErrorMessage();
    }

    @Override
    public void successPokemonByTrainer(long trainer_id, List<Long> pid_list) {

    }

    @Override
    public void successInsertPT() {

    }

    @Override
    public void failureInsertPT() {

    }
}
