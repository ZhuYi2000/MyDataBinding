package com.example.mydatabinding.presenter;

import android.content.Context;
import android.widget.ImageView;

import com.example.mydatabinding.enity.Pokemon;

import java.util.List;

/**
 * Presenter层接口---控制Model层的数据操作及调用View层的UI操作来完成“中间人”工作
 */
public interface IPresenter {
    void getPokemonList(int offset,int limit);
    void getOnePokemon(int id);
    void loadImage(Context context, List<ImageView> imageViewList, List<String> urls,int number);
}
