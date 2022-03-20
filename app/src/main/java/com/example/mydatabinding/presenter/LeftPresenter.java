package com.example.mydatabinding.presenter;

import android.content.Context;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.mydatabinding.view.LeftFragment;
import com.example.mydatabinding.enity.Pokemon;
import com.example.mydatabinding.model.IModel;
import com.example.mydatabinding.model.LeftModel;
import com.example.mydatabinding.view.IView;

import java.util.List;

//负责处理业务逻辑，但是rv相关的事务在adapter中处理
public class LeftPresenter implements IPresenter, LeftModel.LoadDataCallback {
    private final IView mainView;
    private final IModel leftModel;

    //构造器
    public LeftPresenter(IView mainView) {
        this.mainView = mainView;
        this.leftModel = new LeftModel("https://pokeapi.co/api/v2/");
    }


    @Override
    public void getPokemonList(int offset, int limit) {
        leftModel.getListData(offset,limit,this);//异步获取宝可梦列表
    }

    @Override
    public void getOnePokemon(int id) {

    }

    //先空着
    @Override
    public void loadImage(Context context, List<ImageView> imageViewList, List<String> urls, int number) {

    }


    //model的回调方法
    //处理列表，启动fragment
    @Override
    public void successList(List<String> p_name_list) {
        Fragment fragment = new LeftFragment(p_name_list);
        mainView.loadFragment(fragment);
        mainView.hideProcess();
    }

    @Override
    public void failureList() {
        mainView.showErrorMessage();
    }

    @Override
    public void successOne(Pokemon pokemon) {

    }

    @Override
    public void failureOne() {

    }
}
