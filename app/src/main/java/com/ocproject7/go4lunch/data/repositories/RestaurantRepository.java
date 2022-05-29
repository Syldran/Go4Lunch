package com.ocproject7.go4lunch.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import com.ocproject7.go4lunch.data.RetrofitService;
import com.ocproject7.go4lunch.data.callback.OnGetRestaurants;
import com.ocproject7.go4lunch.data.responses.NearbyResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private static String TAG = "TAG_RestaurantRepository";


    public void getRestaurants(String location, int radius, OnGetRestaurants onGetRestaurantsCallBack){
        Log.d(TAG, "getRestaurants: location & radius = "+location+" & "+ radius);


        RetrofitService.getPlacesApi().getNearBy(location, radius, "restaurant", "AIzaSyDh9-vXD67X64ASMqxSS-JQUy06g2mF2OE").enqueue(new Callback<NearbyResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyResponse> call, @NonNull Response<NearbyResponse> response) {
                if (response.code() == 200 && response.body() != null){
                    Log.d(TAG, "onResponse: ok");
                    onGetRestaurantsCallBack.onGetRestaurantData(response.body().getResults());
                }else {
                    try {
                        Log.d(TAG, "onResponse: ERROR : "+response.errorBody().string());
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NearbyResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }

        });
    }
}
