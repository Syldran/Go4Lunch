package com.ocproject7.go4lunch.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.ListItemBinding;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.ui.DetailsRestaurantActivity;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Restaurant> mRestaurants = new ArrayList<>();
    private final OnRestaurantListener mOnRestaurantListener;


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
        if (restaurant.getOpeningHours() == null) {
            holder.mBinding.textRecyclerViewOpening.setText("Undefined");
        } else {
            holder.mBinding.textRecyclerViewOpening.setText(mRestaurants.get(position).getOpeningHours().getOpenNow() ? "Open" : "Closed");
        }

        if (mRestaurants.get(position).getRating() != null) {
            float rating = mRestaurants.get(position).getRating().floatValue() / 5f * 3f;
            holder.mBinding.ratingBarRecyclerView.setRating(rating);
        }
        if (mRestaurants.get(position).getPhotos() != null){
            DetailsRestaurantActivity.loadImage(holder.itemView.getContext(), url, holder.mBinding.imgRecyclerViewRestaurant);
        }

        LatLng start = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
        double distance = SphericalUtil.computeDistanceBetween( start, RestaurantViewModel.mLocation);
        holder.mBinding.textRecyclerViewDistance.setText((int) distance +"m");

        holder.itemView.setOnClickListener(view -> {
            mOnRestaurantListener.onRestaurantClick(restaurant);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public void updateResults(@NonNull final List<Restaurant> restaurants) {
        mRestaurants = restaurants;
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
