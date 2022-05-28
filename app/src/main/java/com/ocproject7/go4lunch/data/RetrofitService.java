package com.ocproject7.go4lunch.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static Retrofit retrofit =
            new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/place/")
                    .addConverterFactory(GsonConverterFactory.create()).build();

    private static PlacesApi mPlacesApi = retrofit.create(PlacesApi.class);

    public static PlacesApi getPlacesApi(){
        return mPlacesApi;
    }
}
