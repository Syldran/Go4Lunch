package com.ocproject7.go4lunch.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ocproject7.go4lunch.BuildConfig;
import com.ocproject7.go4lunch.data.RetrofitService;
import com.ocproject7.go4lunch.data.callback.OnDetailsRestaurant;
import com.ocproject7.go4lunch.data.callback.OnGetRestaurants;
import com.ocproject7.go4lunch.data.responses.DetailsResponse;
import com.ocproject7.go4lunch.data.responses.NearbyResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private static String TAG = "TAG_RestaurantRepository";


    public void getRestaurants(String location, int radius, String rankBy, OnGetRestaurants onGetRestaurantsCallBack) {
        Log.d(TAG, "getRestaurants: location & radius = " + location + " & " + radius);


        RetrofitService.getPlacesApi().getNearBy(location, radius, "restaurant", rankBy, BuildConfig.GOOGLE_API_KEY).enqueue(new Callback<NearbyResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyResponse> call, @NonNull Response<NearbyResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    Log.d(TAG, "onResponse: ok");
                    onGetRestaurantsCallBack.onGetRestaurantData(response.body().getResults());
                } else {
                    try {
                        Log.d(TAG, "onResponse: ERROR : " + response.errorBody().string());
                    } catch (IOException e) {
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

    public void getDetailsRestaurant(String id, OnDetailsRestaurant onDetailsRestaurantCallBack) {
        RetrofitService.getPlacesApi().getDetails(id, "AIzaSyDh9-vXD67X64ASMqxSS-JQUy06g2mF2OE").enqueue(new Callback<DetailsResponse>() {
            @Override
            public void onResponse(Call<DetailsResponse> call, Response<DetailsResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    onDetailsRestaurantCallBack.onGetDetailsRestaurantData(response.body().getResult());
                }
            }

            @Override
            public void onFailure(Call<DetailsResponse> call, Throwable t) {

            }
        });
    }
}
