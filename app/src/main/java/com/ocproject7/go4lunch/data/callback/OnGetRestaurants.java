package com.ocproject7.go4lunch.data.callback;

import com.ocproject7.go4lunch.models.Restaurant;

import java.util.List;

public interface OnGetRestaurants {
    void onGetRestaurantData(List<Restaurant> restaurants);

}
