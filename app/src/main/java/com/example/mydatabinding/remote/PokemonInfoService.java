package com.example.mydatabinding.remote;

import android.util.Log;


import com.example.mydatabinding.enity.Pokemon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PokemonInfoService {
    private Retrofit retrofit;
    private IPokemonInfoService infoService;

    //构造器，根据URL构建相对应的service接口
    public PokemonInfoService(String base_url) {
        retrofit = new Retrofit.Builder().baseUrl(base_url).build();
        infoService = retrofit.create(IPokemonInfoService.class);
    }

    //异步的方式获取单个宝可梦的详细信息，返回值是宝可梦对象
    public Pokemon getOnePokemonAsync(int id){
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

                    //pokemon.printAttr();

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("zhu","错误"+t.toString());
            }
        });
        return pokemon;
    }

    //异步的方式获取宝可梦的列表，返回值是宝可梦名字的列表，顺序就是ID顺序，宝可梦ID=index+1+offset
    public List<String> getPokemonListAsync(int offset, int limit){

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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("zhu","错误"+t.toString());
            }
        });

        return p_name_list;
    }
}
