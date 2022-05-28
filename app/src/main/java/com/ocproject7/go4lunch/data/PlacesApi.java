package com.ocproject7.go4lunch.data;

import com.ocproject7.go4lunch.data.responses.NearbyResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesApi {
    @GET("nearbysearch/json")
    Call<NearbyResponse> getNearBy(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("key") String key
    );
}
