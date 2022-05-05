package com.ocproject7.go4lunch.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.ActivityNavigationBinding;
import com.ocproject7.go4lunch.ui.list.ListFragment;
import com.ocproject7.go4lunch.ui.map.MapFragment;
import com.ocproject7.go4lunch.ui.workmates.WorkmatesFragment;

import java.util.Arrays;
import java.util.List;


public class NavigationActivity extends AppCompatActivity {

    ActivityNavigationBinding binding;
    private static final int RC_SIGN_IN = 123;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new MapFragment());
        startSignInActivity();


    }

        private void replaceFragment(Fragment fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_frame,fragment);
            fragmentTransaction.commit();

            binding.bottomNavigation.setOnItemSelectedListener(item -> {
                switch (item.getItemId()){

                    case R.id.map:
                        replaceFragment(new MapFragment());
                        break;
                    case R.id.list:
                        replaceFragment(new ListFragment());
                        break;
                    case R.id.workmates:
                        replaceFragment(new WorkmatesFragment());
                        break;
                }
                return true;
            });
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