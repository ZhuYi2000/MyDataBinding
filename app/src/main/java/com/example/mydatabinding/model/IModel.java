package com.example.mydatabinding.model;


import android.content.Context;
import android.widget.ImageView;

import java.util.List;

/**
 * Model层接口---实现该接口的类负责实际的获取数据操作，如数据库读取、网络加载
 */
public interface IModel {
    void getListData(int offset, int limit, LeftModel.LoadDataCallback callback);
    void getOneData(int id, LeftModel.LoadDataCallback callback);
    void getImageData(Context context, List<ImageView> imageViewList, List<String> urls, int number);
}
