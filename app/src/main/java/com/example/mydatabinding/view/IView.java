package com.example.mydatabinding.view;


import androidx.fragment.app.Fragment;

import com.example.mydatabinding.enity.Pokemon;

/**
 * View层接口---执行各种UI操作，定义的方法主要是给Presenter中来调用的
 */
public interface IView {
    void hideProcess();
    void loadFragment(Fragment fragment);
    void showErrorMessage();
    void setData(Pokemon pokemon);
}
