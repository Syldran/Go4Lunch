package com.ocproject7.go4lunch.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ocproject7.go4lunch.data.repositories.RestaurantRepository;
import com.ocproject7.go4lunch.data.repositories.UserRepository;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.models.User;

public class RestaurantViewModel extends ViewModel {

    private static String TAG = "TAG_RestaurantListViewModel";

    public MutableLiveData<List<Restaurant>> mRestaurants;
    public MutableLiveData<List<Restaurant>> mDetails;
    public List<Restaurant> mDetailsRestaurants;
    public MutableLiveData<Restaurant> mRestaurant;
    private RestaurantRepository mRestaurantRepository;
    private UserRepository mUserRepository;
    public LatLng mLocation;
    public String mName;
    public MutableLiveData<List<User>> allUsers;

    public void createUser(){
        mUserRepository.createUser();
    }

//    public boolean isExistingUser(String id){
//        return mUserRepository.isExistingUser(id);
//    }

    public FirebaseUser getCurrentUser(){
        return mUserRepository.getCurrentUser();
    }

    public boolean isCurrentUserLogged(){
        return (mUserRepository.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }

    public Task<User> getUser(String id){
        // Get the user from Firestore and cast it to a User model Object
        return mUserRepository.getUserData(id).continueWith(task -> task.getResult().toObject(User.class)) ;
    }

    public void getUsers(){
        List<User> users = new ArrayList<>();
        mUserRepository.getUsersCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "onComplete: document = "+document.getId());
                                User user = document.toObject(User.class);
                                users.add(user);
                            }
                            allUsers.setValue(users);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


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

    public RestaurantViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository){
        mRestaurantRepository = restaurantRepository;
        mUserRepository = userRepository;
        mRestaurants = new MutableLiveData<>();
        mRestaurant = new MutableLiveData<>();
        mDetails = new MutableLiveData<>();
        mDetailsRestaurants = new ArrayList<>();
        allUsers = new MutableLiveData<>();
        mName=null;
    }
}
