package com.ocproject7.go4lunch.data.callback;

import java.util.List;

import com.ocproject7.go4lunch.models.Restaurant;

public interface OnGetRestaurants {
    void onGetRestaurantData(List<Restaurant> restaurants);

}
