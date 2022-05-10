package com.ocproject7.go4lunch.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.ActivityNavigationBinding;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Arrays;
import java.util.List;


public class NavigationActivity extends AppCompatActivity {

    ActivityNavigationBinding binding;
    private static final int RC_SIGN_IN = 123;
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initToolbar();
        initNavigation();
        startSignInActivity();
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

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setAlwaysShowSignInMethodScreen(true)
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.Theme_Go4Lunch)
                        .build(),
                RC_SIGN_IN);
    }
}