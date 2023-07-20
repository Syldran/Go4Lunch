package com.ocproject7.go4lunch.data.repositories;

import androidx.annotation.NonNull;

import com.ocproject7.go4lunch.BuildConfig;
import com.ocproject7.go4lunch.data.PlacesApi;
import com.ocproject7.go4lunch.data.callback.OnDetailsRestaurant;
import com.ocproject7.go4lunch.data.callback.OnGetRestaurants;
import com.ocproject7.go4lunch.data.responses.DetailsResponse;
import com.ocproject7.go4lunch.data.responses.NearbyResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {
    private PlacesApi placesApi;

    public RestaurantRepository(PlacesApi placesApi) {
        this.placesApi = placesApi;
    }

    public void getRestaurants(String location, String radius, String rankBy, OnGetRestaurants onGetRestaurantsCallBack) {
        placesApi.getNearBy(location, radius, "restaurant", rankBy, BuildConfig.GOOGLE_API_KEY).enqueue(new Callback<NearbyResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyResponse> call, @NonNull Response<NearbyResponse> response) {
                if (response.code() == 200 && response.body() != null) {
                    onGetRestaurantsCallBack.onGetRestaurantData(response.body().getResults());
                }

            }

            @Override
            public void onFailure(Call<NearbyResponse> call, Throwable t) {
            }

        });
    }


    public void getDetailsRestaurant(String id, OnDetailsRestaurant onDetailsRestaurantCallBack) {
        placesApi.getDetails(id, BuildConfig.GOOGLE_API_KEY).enqueue(new Callback<DetailsResponse>() {
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
