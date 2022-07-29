package com.ocproject7.go4lunch.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ocproject7.go4lunch.data.repositories.RestaurantRepository;
import com.ocproject7.go4lunch.data.repositories.UserRepository;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantViewModel extends ViewModel {

    private static String TAG = "TAG_RestaurantListViewModel";

    public MutableLiveData<List<Restaurant>> mRestaurants;
    public MutableLiveData<List<Restaurant>> mDetails;
    public List<Restaurant> mDetailsRestaurants;
    public MutableLiveData<Restaurant> mRestaurant;
    private RestaurantRepository mRestaurantRepository;
    private UserRepository mUserRepository;
    public MutableLiveData<Restaurant> mDetail;
    public LatLng mLocation;
    public String mName;
    public MutableLiveData<List<User>> allUsers;
    public MutableLiveData<User> currentUser;

    // __________ USER __________

    public void createUser() {
        Log.d(TAG, "createUser: ");
        mUserRepository.createUser();
    }

    public FirebaseUser getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }

    public boolean isCurrentUserLogged() {
        return (mUserRepository.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        currentUser = new MutableLiveData<>();
        return mUserRepository.signOut(context);
    }

    public Task<User> getUser(String id) {
        // Get the user from Firestore and cast it to a User model Object
        return mUserRepository.getUserData(id).continueWith(task -> task.getResult().toObject(User.class));
    }

//    public void updateUserFromFirestore(String id){
//        mUserRepository.getUserData(id).continueWith(task -> task.getResult().toObject(User.class)).addOnSuccessListener(new OnSuccessListener<User>() {
//            @Override
//            public void onSuccess(User user) {
//                Log.d(TAG, "onSuccess: "+user);
//                currentUser.setValue(user);
//            }
//        });
//    }

    public void updateUserFromFirestore(String id){
        mUserRepository.getUserData(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser.setValue(documentSnapshot.toObject(User.class));
            }
        });
    }


    public void getUsers() {
        List<User> users = new ArrayList<>();
        mUserRepository.getUsersCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
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



    // ________ RESTAURANT ________

    // Update Chosen Restaurant
    public void updateRestaurant(String id, String name) {
        String uid = getCurrentUser().getUid();
        mUserRepository.getUsersCollection().document(uid).update("restaurantId", id);
        mUserRepository.getUsersCollection().document(uid).update("restaurantName", name);
    }

    public void fetchDetailsRestaurants() {
        if (mDetailsRestaurants != null) {
            mDetailsRestaurants.clear();
        }
        for (int i = 0; i < Objects.requireNonNull(mRestaurants.getValue()).size(); i++) {
            mRestaurantRepository.getDetailsRestaurant(mRestaurants.getValue().get(i).getPlaceId(), restaurant -> {
                mDetailsRestaurants.add(restaurant);
                mDetails.setValue(mDetailsRestaurants);
            });
        }
    }

    public void fetchDetailsRestaurant(String id){
        mRestaurantRepository.getDetailsRestaurant(id, restaurant -> {
            Log.d(TAG, "fetchDetailRestaurant: ");
            mDetail.setValue(restaurant);
        });
    }

    public void fetchRestaurants(int radius, String rankBy) {
//        Log.d(TAG, "fetchRestaurants: ");
        String sLocation = mLocation.latitude + "," + mLocation.longitude;
        mRestaurantRepository.getRestaurants(sLocation, radius, rankBy, restaurants -> {
            mRestaurants.setValue(restaurants);
            fetchDetailsRestaurants();
            getUsers();
        });
        Log.d(TAG, "fetchRestaurants: mlocation : " + mLocation);
    }

    public void setLocation(LatLng location){
        mLocation = location;
    }

    public void setCurrentPosName(String name){
        mName = name;
    }

    public RestaurantViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        mRestaurantRepository = restaurantRepository;
        mUserRepository = userRepository;
        mRestaurants = new MutableLiveData<>();
        mRestaurant = new MutableLiveData<>();
        mDetails = new MutableLiveData<>();
        mDetailsRestaurants = new ArrayList<>();
        allUsers = new MutableLiveData<>();
        mName = null;
        mDetail = new MutableLiveData<>();
        mLocation = new LatLng(48.856614,2.3522219);
        currentUser = new MutableLiveData<>();
    }
}
