package com.example.mydatabinding.presenter;

import android.content.Context;
import android.widget.ImageView;

import com.example.mydatabinding.enity.Pokemon;
import com.example.mydatabinding.model.IModel;
import com.example.mydatabinding.model.LeftModel;
import com.example.mydatabinding.view.IView;

import java.util.List;

public class LeftInfoPresenter implements IPresenter, LeftModel.LoadDataCallback {
    private final IView pokemonInfoView;
    private final IModel leftModel;

    public LeftInfoPresenter(IView pokemonInfoView) {
        this.pokemonInfoView = pokemonInfoView;
        this.leftModel = new LeftModel("https://pokeapi.co/api/v2/");
    }

    @Override
    public void getPokemonList(int offset, int limit) {

    }

    @Override
    public void getOnePokemon(int id) {
        this.leftModel.getOneData(id,this);
    }

    @Override
    public void loadImage(Context context, List<ImageView> imageViewList, List<String> urls, int number) {
        this.leftModel.getImageData(context,imageViewList,urls,number);
    }

    @Override
    public void successList(List<String> p_name_list) {

    }

    @Override
    public void failureList() {

    }

    @Override
    public void successOne(Pokemon pokemon) {
        pokemonInfoView.setData(pokemon);
        pokemonInfoView.hideProcess();
    }

    @Override
    public void failureOne() {
        pokemonInfoView.showErrorMessage();
    }
}
