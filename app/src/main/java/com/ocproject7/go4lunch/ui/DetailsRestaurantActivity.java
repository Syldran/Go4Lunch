package com.ocproject7.go4lunch.ui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
    private List<User> mSubscribers = new ArrayList<>();
    private DetailsAdapter adapter;
    private RecyclerView mRecyclerView;

    ActivityDetailsRestaurantBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRestaurantViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mRestaurant = getIntent().getParcelableExtra("DETAILS");
        configureUi(mRestaurant);
        initData();
        configRecyclerView();
        mRestaurantViewModel.getUsers();
    }

    private void checkIsSubscribed() {
        mRestaurantViewModel.getUser(mRestaurantViewModel.getCurrentUser().getUid()).addOnSuccessListener(user -> {
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
    }

    private void configureUi(Restaurant restaurant) {

        checkIsSubscribed();

        binding.tvNameRestaurant.setText(restaurant.getName());
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
            if (isSubscribed) {
                mRestaurantViewModel.updateRestaurant(null, null);
                binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check);
                isSubscribed = false;
                mRestaurantViewModel.getUsers();
            } else {
                mRestaurantViewModel.updateRestaurant(restaurant.getPlaceId(), restaurant.getName());
                binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check_circle);
                isSubscribed = true;
                mRestaurantViewModel.getUsers();
            }
        });

        if (restaurant.getPhotos() != null) {
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.GOOGLE_API_KEY;
            loadImage(DetailsRestaurantActivity.this, url, binding.ivRestaurantPhoto);
        }
    }

    private void initData() {
        mRestaurantViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
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
            adapter.updateResults(mSubscribers);
        });
    }

    private void configRecyclerView() {
        mRecyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new DetailsAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(adapter);
    }


    public static void loadImage(Context context, String url, ImageView view) {
        Glide.with(context).load(url).apply(RequestOptions.centerCropTransform()).into(view);
    }
}
