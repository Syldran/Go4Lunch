package com.ocproject7.go4lunch.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import com.ocproject7.go4lunch.data.repositories.RestaurantRepository;
import com.ocproject7.go4lunch.models.Restaurant;

public class RestaurantViewModel extends ViewModel {

    private static String TAG = "TAG_RestaurantListViewModel";

    public MutableLiveData<List<Restaurant>> mRestaurants;
    public MutableLiveData<List<Restaurant>> mDetails;
    public List<Restaurant> mDetailsRestaurants;
    public MutableLiveData<Restaurant> mRestaurant;
    RestaurantRepository mRestaurantRepository;
    public LatLng mLocation;
    public String mName;



    public void fetchDetailsRestaurants(){
        if (mDetailsRestaurants!=null){ mDetailsRestaurants.clear();}
        for (int i = 0; i < mRestaurants.getValue().size(); i++) {
            mRestaurantRepository.getDetailsRestaurant(mRestaurants.getValue().get(i).getPlaceId(), restaurant -> {
                mDetailsRestaurants.add(restaurant);
                mDetails.setValue(mDetailsRestaurants);
            });
        }
    }



    public void fetchRestaurants(String name, LatLng location, int radius){
        Log.d(TAG, "fetchRestaurants: ");
        String sLocation= location.latitude + "," + location.longitude;
        mRestaurantRepository.getRestaurants(sLocation, radius, restaurants -> {
            mRestaurants.setValue(restaurants);
            fetchDetailsRestaurants();
        });
        mLocation = location;
        mName = name;
        Log.d(TAG, "fetchRestaurants: mlocation : "+mLocation);
    }

    public RestaurantViewModel(RestaurantRepository restaurantRepository){
        mRestaurantRepository = restaurantRepository;
        mRestaurants = new MutableLiveData<>();
        mRestaurant = new MutableLiveData<>();
        mDetails = new MutableLiveData<>();
        mDetailsRestaurants = new ArrayList<>();
        mName=null;
    }
}
