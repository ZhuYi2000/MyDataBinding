package com.example.mydatabinding.model;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.mydatabinding.R;
import com.example.mydatabinding.enity.Pokemon;
import com.example.mydatabinding.remote.IPokemonInfoService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * 实现IModel接口，负责实际的数据获取操作（数据库读取，网络加载等），然后通过自己的接口（LoadDataCallback）反馈出去
 */
public class LeftModel implements IModel {

    private final IPokemonInfoService infoService;

    public LeftModel(String base_url) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(base_url).build();
        infoService = retrofit.create(IPokemonInfoService.class);
    }

    @Override
    public void getListData(int offset, int limit, LeftModel.LoadDataCallback callback) {
        Call<ResponseBody> call = infoService.getPokemonList(offset,limit);
        List<String> p_name_list = new ArrayList<>();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject res_json = new JSONObject(response.body().string());
                    JSONArray raw_list = res_json.getJSONArray("results");
                    for(int i=0;i<limit;i++){
                        String pokemon_name = raw_list.getJSONObject(i).getString("name");
                        p_name_list.add(pokemon_name);
                    }
                    callback.successList(p_name_list);//通过回调接口完成数据传递
                }catch (Exception e){
                    callback.failureList();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.failureList();
            }
        });
    }

    @Override
    public void getOneData(int id, LeftModel.LoadDataCallback callback) {
        Pokemon pokemon = new Pokemon(id);
        Call<ResponseBody> call = infoService.getPokemon(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject res_json = new JSONObject(response.body().string());
                    //Log.e("zhu","测试："+res_json.getString("name"));
                    pokemon.setName(res_json.getString("name"));
                    pokemon.setExp(res_json.getInt("base_experience"));
                    pokemon.setHeight(res_json.getDouble("height")/10);
                    pokemon.setWeight(res_json.getDouble("weight")/10);

                    JSONArray stats = res_json.getJSONArray("stats");

                    pokemon.setHp(stats.getJSONObject(0).getInt("base_stat"));
                    pokemon.setAttack(stats.getJSONObject(1).getInt("base_stat"));
                    pokemon.setDefense(stats.getJSONObject(2).getInt("base_stat"));
                    pokemon.setSpeed(stats.getJSONObject(5).getInt("base_stat"));

                    callback.successOne(pokemon);

                } catch (Exception e){
                    callback.failureOne();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.failureOne();
            }
        });
    }

    @Override
    public void getImageData(Context context, List<ImageView> imageViewList, List<String> urls, int number) {
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)  //用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存所有图片(原图,转换图)
                .fitCenter();

        for(int i=0;i<5;i++){
            Glide.with(context).load(urls.get(i)).apply(options).
                    thumbnail(Glide.with(imageViewList.get(i)).load(R.drawable.loading)).into(imageViewList.get(i));
        }
    }

    public interface LoadDataCallback{
        void successList(List<String> p_name_list);
        void failureList();

        void successOne(Pokemon pokemon);
        void failureOne();
    }
}

