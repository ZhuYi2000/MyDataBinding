package com.example.mydatabinding.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IPokemonInfoService {

    @GET("pokemon/")
    Call<ResponseBody> getPokemonList(@Query("offset") int offset, @Query("limit") int limit);

    @GET("pokemon/{id}")
    Call<ResponseBody> getPokemon(@Path("id") int id);

}
