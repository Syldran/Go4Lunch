package com.ocproject7.go4lunch.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ocproject7.go4lunch.models.User;

public class UserRepository extends AppCompatActivity {
    private static volatile UserRepository instance;
    private static final String TAG = "TAG_UserRepository";

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(@NonNull Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    // Get the Collection Reference
    public CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();
            String mail = user.getEmail();

            User userToCreate = new User(uid, username, mail, urlPicture, null, null);

            getUsersCollection().document(uid).set(userToCreate);
        }
    }



    public Task<DocumentSnapshot> getUserData(String uid) {
        if (uid != null) {
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }


    // Update User Username
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUser().getUid();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update("username", username);
        } else {
            return null;
        }
    }
}
