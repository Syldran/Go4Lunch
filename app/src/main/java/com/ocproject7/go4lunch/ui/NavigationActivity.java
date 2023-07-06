package com.ocproject7.go4lunch.ui;

import static com.ocproject7.go4lunch.ui.SettingsActivity.MyPREFERENCES;
import static com.ocproject7.go4lunch.ui.SettingsActivity.RADIUS;
import static com.ocproject7.go4lunch.ui.SettingsActivity.RANKBY;
import static com.ocproject7.go4lunch.utils.Utils.loadImage;
import static com.ocproject7.go4lunch.utils.Utils.notifyGo4Lunch;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.ocproject7.go4lunch.BuildConfig;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.ActivityNavigationBinding;
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class NavigationActivity extends AppCompatActivity {

    ActivityNavigationBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private RestaurantViewModel mRestaurantViewModel;
    private int radius;
    private String rankBy;
    private boolean isSubscribed;

    private static final String TAG = "TAG_NavigationActivity";
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();
        //set up variables
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        rankBy = sharedpreferences.getString(RANKBY, "prominence");
        radius = sharedpreferences.getInt(RADIUS, 1500);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_API_KEY);
        }

        //init interface
        initToolbar();
        initNavigation();
        initViewModel();

        // maj subscribed restaurant & activate notifications
        mRestaurantViewModel.currentUser.observe(this, user -> {
            if (user != null) {
                isSubscribed = user.getRestaurantId() != null;
            } else {
                isSubscribed = false;
            }
            String message = "";
            if (isSubscribed) {
                message = getString(R.string.eat_at) + Objects.requireNonNull(mRestaurantViewModel.currentUser.getValue()).getRestaurantName();
                notifyGo4Lunch(message, getApplicationContext(), true);
            } else {
                notifyGo4Lunch(message, getApplicationContext(), false);
            }
            mRestaurantViewModel.getUsers();
        });

        //check if user logged for displaying log in screen
        if (mRestaurantViewModel.isCurrentUserLogged()) {
            mRestaurantViewModel.updateUserFromFirestore(mRestaurantViewModel.getCurrentUser().getUid());
            initDrawerUi();
            FirebaseUser currentUser = mRestaurantViewModel.getCurrentUser();
        } else {
            startSignInActivity();
        }

    }

    private void initToolbar() {
        setSupportActionBar(binding.appBarMain.toolbar);
    }

    private void initNavigation() {
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.bottom_map, R.id.bottom_list, R.id.bottom_workmates)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.appBarMain.contentMain1.bottomNavView, navController);
        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_lunch: {
                    //dialog with chosen restaurant
                    String uid = mRestaurantViewModel.getCurrentUser().getUid();
                    AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                    checkFirestoreIsSubscribed(uid, builder);
                }
                break;
                case R.id.nav_settings: {
                    Intent intent = new Intent(this, SettingsActivity.class);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    settingsLauncher.launch(intent);
                }
                break;
                case R.id.nav_logout: {
                    mRestaurantViewModel.signOut(NavigationActivity.this).addOnCompleteListener(task -> {
                        String message = "";
                        if (task.isSuccessful()) {
                            notifyGo4Lunch(message, getApplicationContext(), false);
                            NavigationActivity.this.startSignInActivity();
                            Toast.makeText(NavigationActivity.this.getApplicationContext(), "LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NavigationActivity.this, "LOGOUT FAILED", Toast.LENGTH_LONG).show();
                        }
                    });
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
                default:
                    break;
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

        signInLauncher.launch(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setAlwaysShowSignInMethodScreen(true)
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.Theme_Go4Lunch)
                        .build());
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(new FirebaseAuthUIActivityResultContract(),
            result -> NavigationActivity.this.handleResponseAfterSignIn(result.getResultCode(), result.getIdpResponse())
    );

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            rankBy = sharedpreferences.getString(RANKBY, "prominence");
            radius = sharedpreferences.getInt(RADIUS, 1500);
            mRestaurantViewModel.fetchRestaurants(radius, rankBy);
        }
    });

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int resultCode, IdpResponse response) {

        // SUCCESS
        if (resultCode == RESULT_OK) {
            FirebaseUser currentUser = mRestaurantViewModel.getCurrentUser();
            checkFirestoreData(currentUser.getUid());
            initDrawerUi();

            Toast.makeText(this, "Connection Succeeded", Toast.LENGTH_SHORT).show();
        } else {
            // ERRORS
            if (response == null) {
                Toast.makeText(this, "Error authentication canceled", Toast.LENGTH_SHORT).show();
            } else if (response.getError() != null) {
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Error no internet", Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void checkFirestoreData(String id) {
        if (mRestaurantViewModel.currentUser.getValue() == null) {
            mRestaurantViewModel.createUser();
            mRestaurantViewModel.getUsers();
            mRestaurantViewModel.updateUserFromFirestore(id);
        }
    }


    private void checkFirestoreIsSubscribed(String id, AlertDialog.Builder builder) {
        User user = mRestaurantViewModel.currentUser.getValue();
        if (user != null) {
            if (user.getRestaurantId() != null) {
                builder.setMessage(getString(R.string.eat_at) + user.getRestaurantName())
                        .setTitle(getString(R.string.your_lunch));
                isSubscribed = true;
            } else {
                builder.setMessage(getString(R.string.dialog_message)).setTitle(getString(R.string.your_lunch));
                isSubscribed = false;
            }
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void initDrawerUi() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        ImageView picture = header.findViewById(R.id.iv_header_picture);
        TextView name = header.findViewById(R.id.tv_header_name);
        TextView email = header.findViewById(R.id.tv_header_mail);
        if (mRestaurantViewModel.getCurrentUser().getPhotoUrl() != null) {
            loadImage(this, mRestaurantViewModel.getCurrentUser().getPhotoUrl().toString(), picture);
        } else {
            picture.setImageResource(R.drawable.baseline_account_circle_24);
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
        if (menuId == R.id.action_search) {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

            // Start autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            autocompleteLaunch.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> autocompleteLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent data = result.getData();
            if (result.getResultCode() == Activity.RESULT_OK) {
                Place mPlace = Autocomplete.getPlaceFromIntent(data);
                mRestaurantViewModel.setCurrentPosName(mPlace.getName());
                mRestaurantViewModel.setLocation(mPlace.getLatLng());

                mRestaurantViewModel.fetchRestaurants(radius, rankBy);
            } else if (result.getResultCode() == com.google.android.libraries.places.widget.AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (result.getResultCode() == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    });

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RestaurantNotificationChannel";
            String description = "Notification for Go4Lunch";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyGo4Lunch", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}