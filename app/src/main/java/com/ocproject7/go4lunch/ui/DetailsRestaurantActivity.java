package com.ocproject7.go4lunch.ui;


import static com.ocproject7.go4lunch.utils.Utils.loadImage;
import static com.ocproject7.go4lunch.utils.Utils.notifyGo4Lunch;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ocproject7.go4lunch.BuildConfig;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.ActivityDetailsRestaurantBinding;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailsRestaurantActivity extends AppCompatActivity {
    private RestaurantViewModel mRestaurantViewModel;
    private static String TAG = "TAG_DetailsRestaurantActivity";
    private Restaurant mRestaurant;
    private boolean isSubscribed;
    private final List<User> mSubscribers = new ArrayList<>();
    private DetailsAdapter adapter;

    ActivityDetailsRestaurantBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRestaurantViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mRestaurantViewModel.currentUser.observe(this, user -> {
            if (user != null) {
                if (user.getRestaurantId() == null || !user.getRestaurantId().equals(mRestaurant.getPlaceId())) {
                    isSubscribed = false;
                    binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check);
                } else {
                    isSubscribed = true;
                    binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check_circle);
                }
            } else {
                isSubscribed = false;
            }
        });
        mRestaurantViewModel.updateUserFromFirestore(mRestaurantViewModel.getCurrentUser().getUid());
        mRestaurant = getIntent().getParcelableExtra("DETAILS");
        configureUi(mRestaurant);
        initData();
        configRecyclerView();
        mRestaurantViewModel.getUsers();
    }


    private void configureUi(Restaurant restaurant) {


        binding.tvNameRestaurant.setText(restaurant.getName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvNameRestaurant.setTooltipText(restaurant.getName());
        }

        if (restaurant.getRating() != null) {
            binding.ratingBar.setRating(restaurant.getRating().floatValue() / 5 * 3);
        }
        binding.tvAddressRestaurant.setText(restaurant.getFormattedAddress());

        binding.phoneButton.setOnClickListener(view -> {
            Toast.makeText(this, "Click on phone", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + restaurant.getFormattedPhoneNumber()));
            startActivity(intent);

        });
        binding.rateButton.setOnClickListener(view -> {
            Toast.makeText(this, "Click on star", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(restaurant.getUrl()));
            startActivity(i);
        });

        binding.websiteButton.setOnClickListener(view -> {
            Toast.makeText(this, "Click on Web", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(restaurant.getUrl()));
            startActivity(i);
        });

        binding.fabSubscribeRestaurant.setOnClickListener(view -> {
            String message = "";
            if (isSubscribed) {
                mRestaurantViewModel.updateRestaurant(null, null);
                binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check);
                isSubscribed = false;
                mRestaurantViewModel.getUsers();
                notifyGo4Lunch(message, getApplicationContext(), false);
            } else {
                mRestaurantViewModel.updateRestaurant(restaurant.getPlaceId(), restaurant.getName());
                binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check_circle);
                isSubscribed = true;
                mRestaurantViewModel.getUsers();
                message = getString(R.string.eat_at) + restaurant.getName();
                notifyGo4Lunch(message, getApplicationContext(), true);
            }
        });

        if (restaurant.getPhotos() != null) {
            String url = getString(R.string.url_start) + restaurant.getPhotos().get(0).getPhotoReference() + getString(R.string.url_key) + BuildConfig.GOOGLE_API_KEY;
            loadImage(DetailsRestaurantActivity.this, url, binding.ivRestaurantPhoto);
        }
    }

    private void initData() {
        mRestaurantViewModel.allUsers.observe(this, users -> {
            if (users != null) {
                if (mSubscribers != null) {
                    mSubscribers.clear();
                }
                for (User user : users) {
                    if (Objects.equals(user.getRestaurantId(), mRestaurant.getPlaceId())) {
                        mSubscribers.add(user);
                    }
                }
            }
            if (mSubscribers != null) {
                adapter.updateResults(mSubscribers);
            }
        });
    }

    private void configRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DetailsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }
}
