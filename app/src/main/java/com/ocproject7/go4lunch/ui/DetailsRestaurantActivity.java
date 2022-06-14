package com.ocproject7.go4lunch.ui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ocproject7.go4lunch.BuildConfig;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.ActivityDetailsRestaurantBinding;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

public class DetailsRestaurantActivity extends AppCompatActivity {
    RestaurantViewModel mRestaurantViewModel;
    TextView mTextView;
    private static String TAG = "TAG_DetailsRestaurantActivity";
    ImageView mRestaurantPhoto;
    Restaurant mRestaurant;

    ActivityDetailsRestaurantBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRestaurant = getIntent().getParcelableExtra("DETAILS");
        configureUi(mRestaurant);

    }

    private void configureUi(Restaurant restaurant) {
        binding.phoneButton.setOnClickListener(view -> {
            Toast.makeText(this, "Click on phone", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+restaurant.getFormattedPhoneNumber()));
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

        Log.d(TAG, "configureUi: ID : "+restaurant.getPlaceId());

        mTextView = findViewById(R.id.name_restaurant);
        mRestaurantPhoto = findViewById(R.id.restaurant_photo);

        mTextView.setText(restaurant.getName());

        Log.d(TAG, "configureUi: photo ref "+restaurant.getPhotos().get(0).getPhotoReference());
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference="+restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.GOOGLE_API_KEY;

        Log.d(TAG, "onCreate: "+url);
        loadImage( DetailsRestaurantActivity.this , url, mRestaurantPhoto);
    }


    public static void loadImage(Context context, String  url, ImageView view)
    {
        Glide.with(context).load(url).apply(RequestOptions.centerCropTransform()).into(view);
    }
}
