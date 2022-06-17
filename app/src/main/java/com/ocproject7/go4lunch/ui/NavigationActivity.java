package com.ocproject7.go4lunch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.ocproject7.go4lunch.BuildConfig;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.ActivityNavigationBinding;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Arrays;
import java.util.List;


public class NavigationActivity extends AppCompatActivity {

    ActivityNavigationBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private RestaurantViewModel mRestaurantViewModel;
    private boolean isInFirestore;

    private static String TAG = "TAG_NavigationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_API_KEY);
        }
        initToolbar();
        initNavigation();
        initViewModel();

        if (mRestaurantViewModel.isCurrentUserLogged()) {
            initDrawerUi();
        } else {
            Log.d(TAG, "onCreate: is not logged");
            startSignInActivity();
        }

    }



    private void initToolbar(){
        setSupportActionBar(binding.appBarMain.toolbar);
    }

    private void initNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.bottom_map, R.id.bottom_list, R.id.bottom_workmates)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController( this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_lunch:{
                    //boite dialog avec restau inscrit
                } break;
                case R.id.nav_settings:{

                } break;
                case R.id.nav_logout: {
                    mRestaurantViewModel.signOut(this).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            startSignInActivity();
                            Toast.makeText(getApplicationContext(), "LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(this, "LOGOUT FAILED", Toast.LENGTH_LONG).show();
                    });
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } break;
            }
            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void startSignInActivity() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.sign_in_layout)
                .setEmailButtonId(R.id.login_mail_btn)
                .setGoogleButtonId(R.id.login_google_btn)
                .setFacebookButtonId(R.id.login_facebook_btn)
                .setTwitterButtonId(R.id.login_twitter_btn)
                .build();

        mResultLauncher.launch(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setAlwaysShowSignInMethodScreen(true)
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.Theme_Go4Lunch)
                        .build());
    }

    ActivityResultLauncher<Intent> mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            handleResponseAfterSignIn(result.getResultCode(), result.getData());
        }
    });

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);
        // SUCCESS
        if (resultCode == RESULT_OK) {
            //check if present in firestore
            checkFirestoreData(mRestaurantViewModel.getCurrentUser().getUid());
            if ( !isInFirestore ) {
                Log.d(TAG, "handleResponseAfterSignIn:  current user not in firestore ");
                mRestaurantViewModel.createUser();
            } else {
                Log.d(TAG, "handleResponseAfterSignIn: user is in firestore : "+mRestaurantViewModel.getUser(mRestaurantViewModel.getCurrentUser().getUid()));
            }
            initDrawerUi();
            Toast.makeText(this, "Connection Succeeded", Toast.LENGTH_SHORT).show();
        } else {
            // ERRORS
            if (response == null) {
                Toast.makeText(this, "Error authentication canceled", Toast.LENGTH_SHORT).show();
            } else if (response.getError()!= null) {
                if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    Toast.makeText(this, "Error no internet", Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkFirestoreData(String id) {
        mRestaurantViewModel.getUser(id).addOnSuccessListener(user -> {
            if (user != null){
                isInFirestore = true;
            } else {isInFirestore = false;}
        });
    }
    private void initDrawerUi(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        ImageView picture = header.findViewById(R.id.iv_header_picture);
        TextView title = header.findViewById(R.id.tv_header_go4lunch);
        TextView name = header.findViewById(R.id.tv_header_name);
        TextView email = header.findViewById(R.id.tv_header_mail);
        if (mRestaurantViewModel.getCurrentUser().getPhotoUrl() != null){
            DetailsRestaurantActivity.loadImage(this, mRestaurantViewModel.getCurrentUser().getPhotoUrl().toString(), picture);
        }
        name.setText(mRestaurantViewModel.getCurrentUser().getDisplayName());
        email.setText(mRestaurantViewModel.getCurrentUser().getEmail());
    }

    private void initViewModel() {
        mRestaurantViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        int menuId = item.getItemId();
        switch (menuId){
            case R.id.action_search: {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(this);
                autocompleteLaunch.launch(intent);
                break;
            }
            default:break;

        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> autocompleteLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent data = result.getData();
            if (result.getResultCode() == Activity.RESULT_OK){
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                mRestaurantViewModel.fetchRestaurants(place.getName(), place.getLatLng(), 1500);
            }else if (result.getResultCode() == com.google.android.libraries.places.widget.AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            }else if (result.getResultCode() == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    });
}