package com.ocproject7.go4lunch.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ocproject7.go4lunch.data.repositories.RestaurantRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }

    private final RestaurantRepository mRestaurantRepository = new RestaurantRepository();

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(RestaurantViewModel.class)){
            return (T) new RestaurantViewModel(mRestaurantRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
