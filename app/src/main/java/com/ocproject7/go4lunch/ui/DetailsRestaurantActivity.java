package com.ocproject7.go4lunch.ui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
    private RestaurantViewModel mRestaurantViewModel;
    private static String TAG = "TAG_DetailsRestaurantActivity";
    private Restaurant mRestaurant;
    private boolean isSubscribed;

    ActivityDetailsRestaurantBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mRestaurantViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mRestaurant = getIntent().getParcelableExtra("DETAILS");
        configureUi(mRestaurant);

    }

    private void checkIsSubscribed() {
        mRestaurantViewModel.getUser(mRestaurantViewModel.getCurrentUser().getUid()).addOnSuccessListener(user -> {
            Log.d(TAG, "checkIsSubscribed: userRestaurantId = "+user.getRestaurantId());
            Log.d(TAG, "checkIsSubscribed: detailsRestaurantId = "+mRestaurant.getPlaceId());
            if (user.getRestaurantId() == null || !user.getRestaurantId().equals( mRestaurant.getPlaceId())){
                Log.d(TAG, "checkIsSubscribed: restaurantId=null");
                isSubscribed = false;
                binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check);
            } else {
                isSubscribed = true;
                binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check_circle);
            }
        });
    }

    private void configureUi(Restaurant restaurant) {

        checkIsSubscribed();

        binding.tvNameRestaurant.setText(restaurant.getName());
        binding.ratingBar.setRating(restaurant.getRating().floatValue()/ 5 * 3);
        binding.tvAddressRestaurant.setText(restaurant.getFormattedAddress());

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

        binding.fabSubscribeRestaurant.setOnClickListener(view -> {
            if (isSubscribed){
                mRestaurantViewModel.updateRestaurant(null, null);
                binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check);
                isSubscribed = false;
            } else {
                mRestaurantViewModel.updateRestaurant(restaurant.getPlaceId(), restaurant.getName());
                binding.fabSubscribeRestaurant.setImageResource(R.drawable.ic_check_circle);
                isSubscribed = true;
            }
        });

        Log.d(TAG, "configureUi: ID : "+restaurant.getPlaceId());


        Log.d(TAG, "configureUi: photo ref "+restaurant.getPhotos().get(0).getPhotoReference());
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference="+restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.GOOGLE_API_KEY;

        Log.d(TAG, "onCreate: "+url);
        loadImage( DetailsRestaurantActivity.this , url, binding.ivRestaurantPhoto);
    }


    public static void loadImage(Context context, String  url, ImageView view)
    {
        Glide.with(context).load(url).apply(RequestOptions.centerCropTransform()).into(view);
    }
}
