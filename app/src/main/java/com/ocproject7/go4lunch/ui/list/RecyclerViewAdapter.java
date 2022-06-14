package com.ocproject7.go4lunch.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.data.entities.OpeningHours;
import com.ocproject7.go4lunch.data.entities.Period;
import com.ocproject7.go4lunch.databinding.ListItemBinding;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.ui.DetailsRestaurantActivity;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Restaurant> mRestaurants = new ArrayList<>();
    private final OnRestaurantListener mOnRestaurantListener;
    private LatLng mLocation;


    public RecyclerViewAdapter(List<Restaurant> restaurants, OnRestaurantListener onRestaurantListener) {
        mRestaurants = restaurants;
        mOnRestaurantListener = onRestaurantListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference="+mRestaurants.get(position).getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDCXBbnL9Tw5L_0G6MMtr-F7ibrX-oAx40";
        final Restaurant restaurant = mRestaurants.get(position);
        holder.mBinding.textRecyclerViewTitle.setText(restaurant.getName());
        holder.mBinding.textRecyclerViewAddress.setText(restaurant.getVicinity());

        holder.mBinding.textRecyclerViewOpening.setText(displayOpeningHours(mRestaurants.get(position).getOpeningHours()));


        if (mRestaurants.get(position).getRating() != null) {
            float rating = mRestaurants.get(position).getRating().floatValue() / 5f * 3f;
            holder.mBinding.ratingBarRecyclerView.setRating(rating);
        }
        if (mRestaurants.get(position).getPhotos() != null){
            DetailsRestaurantActivity.loadImage(holder.itemView.getContext(), url, holder.mBinding.imgRecyclerViewRestaurant);
        }

        LatLng start = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
        double distance = SphericalUtil.computeDistanceBetween( start, mLocation);
        holder.mBinding.textRecyclerViewDistance.setText((int) distance +"m");

        holder.itemView.setOnClickListener(view -> {
            mOnRestaurantListener.onRestaurantClick(restaurant);
        });
    }

    public String displayOpeningHours(OpeningHours openingHours){
        if(openingHours == null || openingHours.getPeriods() == null) return "No Time available";
        if(openingHours.getOpenNow() != null && !openingHours.getOpenNow()){
            return "closed";
        }

        int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) -1;
        if(openingHours.getPeriods().size() >= dayOfTheWeek+1){
            Period periodOfTheDay = openingHours.getPeriods().get(dayOfTheWeek);


            if(periodOfTheDay.getClose() == null) return "open 27/7";

            String closureString = periodOfTheDay.getClose().getTime();
            int closure = Integer.parseInt(closureString);

            Date todayDate = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("HHmm");
            String todayDateString = dateFormat.format(todayDate);
            int timeNow = Integer.parseInt(todayDateString);
            int timeBeforeClosure = closure - timeNow;
            if(timeBeforeClosure <= 100){
                return "closing soon";
            } else {
                return "open until "+closureString;
            }


        }
        return "no time";

    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public void updateResults(@NonNull final List<Restaurant> restaurants, LatLng userLocation) {
        mRestaurants = restaurants;
        mLocation = userLocation;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding mBinding;

        public ViewHolder(ListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface OnRestaurantListener{
        void onRestaurantClick(Restaurant restaurant);
    }
}
