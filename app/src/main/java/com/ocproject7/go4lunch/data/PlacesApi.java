package com.ocproject7.go4lunch.data;

import com.ocproject7.go4lunch.data.responses.DetailsResponse;
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
            @Query("rankby") String rankby,
            @Query("key") String key
    );

    @GET("nearbysearch/json")
    Call<NearbyResponse> getNearBy(
            @Query("location") String location,
            @Query("type") String type,
            @Query("rankby") String rankby,
            @Query("key") String key
    );

    @GET("details/json")
    Call<DetailsResponse> getDetails(
            @Query("place_id") String id,
            @Query("key") String key
    );
}
