package com.ocproject7.go4lunch.ui;

import static com.ocproject7.go4lunch.ui.SettingsActivity.MyPREFERENCES;
import static com.ocproject7.go4lunch.ui.SettingsActivity.RADIUS;
import static com.ocproject7.go4lunch.ui.SettingsActivity.RANKBY;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
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
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class NavigationActivity extends AppCompatActivity {

    ActivityNavigationBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private RestaurantViewModel mRestaurantViewModel;
    private int radius;
    private String rankBy;
    private LatLng mUserPos;

    private static String TAG = "TAG_NavigationActivity";

    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.getString(RANKBY, null) == null) {
            sharedpreferences.edit().putString(RANKBY, "prominence").apply();
        }

        rankBy = sharedpreferences.getString(RANKBY, "prominence");
        Log.d(TAG, "onCreate: rankby " + rankBy);
        radius = sharedpreferences.getInt(RADIUS, 1500);
        Log.d(TAG, "onCreate: radius " + radius);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_API_KEY);
        }
        initToolbar();
        initNavigation();
        initViewModel();
        if (mRestaurantViewModel.isCurrentUserLogged()) {
            initDrawerUi();
            mRestaurantViewModel.getUsers();
        } else {
            Log.d(TAG, "onCreate: is not logged");
            startSignInActivity();
        }

    }


    private void initToolbar() {
        setSupportActionBar(binding.appBarMain.toolbar);
    }

    private void initNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(binding.appBarMain.contentMain1.bottomNavView, navController);
        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_lunch: {
                    //dialog with chosen restaurant
                    String uid = mRestaurantViewModel.getCurrentUser().getUid();
                    AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                    NavigationActivity.this.checkFirestoreIsSubscribed(uid, builder);
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
                        if (task.isSuccessful()) {
                            NavigationActivity.this.startSignInActivity();
                            Toast.makeText(NavigationActivity.this.getApplicationContext(), "LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(NavigationActivity.this, "LOGOUT FAILED", Toast.LENGTH_LONG).show();
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
            Log.d(TAG, "onActivityResult: resultCode " + result.getResultCode());
            rankBy = sharedpreferences.getString(RANKBY, "prominence");
            Log.d(TAG, "onCreate: rankby " + rankBy);
            radius = sharedpreferences.getInt(RADIUS, 1500);
            Log.d(TAG, "onCreate: radius " + radius);

            mRestaurantViewModel.fetchRestaurants(radius, rankBy);

            if (result.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: HERE");
            }
        }
    });

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int resultCode, IdpResponse response) {

        // SUCCESS
        if (resultCode == RESULT_OK) {
            checkFirestoreData(mRestaurantViewModel.getCurrentUser().getUid());
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
        mRestaurantViewModel.getUser(id).addOnSuccessListener(user -> {
            if (user == null) {
                Log.d(TAG, "handleResponseAfterSignIn:  current user not in firestore ");
                mRestaurantViewModel.createUser();
                mRestaurantViewModel.getUsers();
            } else {
                Log.d(TAG, "handleResponseAfterSignIn: user is in firestore : " + mRestaurantViewModel.getUser(mRestaurantViewModel.getCurrentUser().getUid()));
            }
        });
    }

    private void checkFirestoreIsSubscribed(String id, AlertDialog.Builder builder) {
        mRestaurantViewModel.getUser(id).addOnSuccessListener(user -> {
            if (user != null) {
                if (user.getRestaurantId() != null) {
                    builder.setMessage(getString(R.string.dialog_message) + user.getRestaurantName())
                            .setTitle(R.string.dialog_title);
                } else {
                    builder.setMessage(R.string.dialog_message_subscribed).setTitle(R.string.dialog_title);
                }
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void initDrawerUi() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        ImageView picture = header.findViewById(R.id.iv_header_picture);
        TextView name = header.findViewById(R.id.tv_header_name);
        TextView email = header.findViewById(R.id.tv_header_mail);
        if (mRestaurantViewModel.getCurrentUser().getPhotoUrl() != null) {
            DetailsRestaurantActivity.loadImage(this, mRestaurantViewModel.getCurrentUser().getPhotoUrl().toString(), picture);
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
        switch (menuId) {
            case R.id.action_search: {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                // Start autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(this);
                autocompleteLaunch.launch(intent);
                break;
            }
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> autocompleteLaunch = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Intent data = result.getData();
            if (result.getResultCode() == Activity.RESULT_OK) {
                Place mPlace = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + mPlace.getName() + ", " + mPlace.getId());
                mRestaurantViewModel.setCurrentPosName(mPlace.getName());
                mRestaurantViewModel.setLocation(mPlace.getLatLng());
                mRestaurantViewModel.fetchRestaurants(radius, rankBy);
            } else if (result.getResultCode() == com.google.android.libraries.places.widget.AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (result.getResultCode() == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    });

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "RestaurantNotificationChannel";
            String description = "Notification for Go4Lunch";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyGo4Lunch", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(NavigationActivity.this, ReminderNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NavigationActivity.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 58);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Log.d(TAG, "createNotificationChannel: "+calendar.getTime());
        long time = calendar.getTimeInMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
//        alarmManager.setRepeating();
    }
}