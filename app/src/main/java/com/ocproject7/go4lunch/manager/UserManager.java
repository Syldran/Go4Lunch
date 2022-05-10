package com.ocproject7.go4lunch.manager;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.ocproject7.go4lunch.repositories.UserRepository;

public class UserManager extends AppCompatActivity {
    private static volatile UserManager instance;
    private UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public boolean isCurrentUserLogged(){
        return (UserRepository.getInstance().getCurrentUser() != null);
    }

    public FirebaseUser getCurrentUser(){
        return UserRepository.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return userRepository.signOut(context);
    }
}