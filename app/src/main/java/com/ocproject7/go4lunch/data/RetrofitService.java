package com.ocproject7.go4lunch.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static RetrofitService retrofitService;
    private final PlacesApi mPlacesApi;

    private RetrofitService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        this.mPlacesApi = retrofit.create(PlacesApi.class);
    }

    public static RetrofitService getInstance() {
        if (retrofitService == null) {
            retrofitService = new RetrofitService();
        }
        return retrofitService;
    }

    public PlacesApi getPlacesApi() {
        return this.mPlacesApi;
    }
}