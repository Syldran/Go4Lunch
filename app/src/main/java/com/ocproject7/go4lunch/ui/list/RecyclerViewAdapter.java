package com.ocproject7.go4lunch.ui.list;

import static com.ocproject7.go4lunch.utils.Utils.loadImage;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.data.entities.OpeningHours;
import com.ocproject7.go4lunch.data.entities.Period;
import com.ocproject7.go4lunch.databinding.ListItemBinding;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.ui.DetailsRestaurantActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Restaurant> mRestaurants = new ArrayList<>();
    private final OnRestaurantListener mOnRestaurantListener;
    private LatLng mLocation;
    private List<User> mUsers;

    private static final String TAG = "TAG_RecyclerViewAdapter";

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
        final Restaurant restaurant = mRestaurants.get(position);
        if (restaurant.getPhotos() != null) {
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + restaurant.getPhotos().get(0).getPhotoReference() + "&key=AIzaSyDCXBbnL9Tw5L_0G6MMtr-F7ibrX-oAx40";
            loadImage(holder.itemView.getContext(), url, holder.mBinding.imgRecyclerViewRestaurant);
        } else {
            holder.mBinding.imgRecyclerViewRestaurant.setImageResource(R.drawable.lunch_blurred);
        }

        holder.mBinding.textRecyclerViewTitle.setText(restaurant.getName());
        holder.mBinding.textRecyclerViewAddress.setText(restaurant.getVicinity());
        holder.mBinding.textRecyclerViewOpening.setText(displayOpeningHours(restaurant.getOpeningHours()));

        if (restaurant.getRating() != null) {
            float rating = restaurant.getRating().floatValue() / 5f * 3f;
            holder.mBinding.ratingBarRecyclerView.setRating(rating);
        }

        LatLng start = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
        double distance = SphericalUtil.computeDistanceBetween(start, mLocation);
        holder.mBinding.textRecyclerViewDistance.setText((int) distance + "m");

        holder.mBinding.textRecyclerViewSubscribed.setText("(" + getCountSubscribers(restaurant) + ")");

        holder.itemView.setOnClickListener(view -> {
            mOnRestaurantListener.onRestaurantClick(restaurant);
        });

    }

    public String displayOpeningHours(OpeningHours openingHours) {
        if (openingHours == null || openingHours.getPeriods() == null) {
            return "Unspecified opening hours";
        }
        if (openingHours.getOpenNow() != null && !openingHours.getOpenNow()) {
            return "Closed";
        }
        int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        List<Period> periodsOfCurrentDay = new ArrayList<>();

        //find periods for the current day
        for (Period period : openingHours.getPeriods()) {
            if (period.getClose() == null) {
                return "Open 27/7";
            }
            if (period.getOpen().getDay() == currentDayOfWeek) {
                periodsOfCurrentDay.add(period);
            }
        }

        Date todayDate = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("HHmm");
        String todayDateString = dateFormat.format(todayDate);
        int timeNow = Integer.parseInt(todayDateString);


        for (Period period : periodsOfCurrentDay) { // we look for period matching current time
            if (Objects.equals(period.getOpen().getDay(), period.getClose().getDay())) { // check closing time is on same day
                if (Integer.parseInt(period.getOpen().getTime()) < timeNow && Integer.parseInt(period.getClose().getTime()) > timeNow) {  // if current time between closure and opening
                    int timeBeforeClosure = Integer.parseInt(period.getClose().getTime()) - timeNow;
                    if (timeBeforeClosure <= 100) {
                        return "Closing soon";
                    } else {
                        return "Open until " + formattedTime(period, true);
                    }
                }
            } else { // closure is next day
                // we add 2400 to close time to add a day
                boolean afterMidnight = false;
                if (timeNow < Integer.parseInt(period.getOpen().getTime())) { // if current time < opening time then we passed midnight and we need to add 24h
                    timeNow += 2400;
                    afterMidnight = true;
                }
                if ((Integer.parseInt(period.getOpen().getTime()) < timeNow) && (Integer.parseInt(period.getClose().getTime()) + 2400 > timeNow)) {
                    int timeBeforeClosure = Integer.parseInt(period.getClose().getTime()) + 2400 - timeNow;
                    if (timeBeforeClosure < 0) {
                        return "Closed";
                    }
                    if (timeBeforeClosure <= 100) {
                        return "Closing soon";
                    } else {
                        return "Open until " + formattedTime(period, afterMidnight);
                    }
                }
            }
        }
        return "Unknown";
    }

    public String formattedTime(Period period, boolean bool) {
        String timeClosure = period.getClose().getTime();
        if (bool) {
            return "" + timeClosure.charAt(0) + timeClosure.charAt(1) + "h" + timeClosure.charAt(2) + timeClosure.charAt(3);
        } else {
            return String.format("%s%sh%s%s%s", timeClosure.charAt(0), timeClosure.charAt(1), timeClosure.charAt(2), timeClosure.charAt(3), " tomorrow");
        }
    }

//    public String displayOpeningHourss(OpeningHours openingHours){
//        if(openingHours == null || openingHours.getPeriods() == null) {
//            return "No Time available";
//        }
//        if(openingHours.getOpenNow() != null && !openingHours.getOpenNow()){
//            return "closed";
//        }
//
//        Log.d(TAG, "displayOpeningHours: periods size="+openingHours.getPeriods().size());
//        Log.d(TAG, "displayOpeningHours: current Hour = "+Calendar.getInstance().get(Calendar.HOUR));
//        Log.d(TAG, "displayOpeningHours: current Min = "+Calendar.getInstance().get(Calendar.MINUTE));
//        Log.d(TAG, "displayOpeningHours: zone "+Calendar.getInstance().getTimeZone().getDisplayName());
//
//
//        int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1 ;
//        Log.d(TAG, "displayOpeningHours: dayOfTheWeek="+dayOfTheWeek);
//        if(openingHours.getPeriods().size() >= dayOfTheWeek+1){
//            Period periodOfTheDay = openingHours.getPeriods().get(dayOfTheWeek);
//
//            if(periodOfTheDay.getClose() == null) return "open 27/7";
//
//            String closureString = periodOfTheDay.getClose().getTime();
//            int closure = Integer.parseInt(closureString);
//
//            Date todayDate = Calendar.getInstance().getTime();
//            DateFormat dateFormat = new SimpleDateFormat("HHmm");
//            String todayDateString = dateFormat.format(todayDate);
//            int timeNow = Integer.parseInt(todayDateString);
//            int timeBeforeClosure = closure - timeNow;
//            Log.d(TAG, "displayOpeningHours: timeBeforeClosure = "+timeBeforeClosure);
//            if(timeBeforeClosure <= 60){
//                return "closing soon";
//            } else {
//                return "open until "+closureString;
//            }
//
//        }
//        return "no time";
//
//    }

    public int getCountSubscribers(Restaurant restaurant) {
        int countSubscribers = 0;
        if (mUsers != null) {
            for (User user : mUsers) {
                if (Objects.equals(user.getRestaurantId(), restaurant.getPlaceId())) {
                    countSubscribers++;
                }
            }
        }
        return countSubscribers;
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public void updateResults(@NonNull final List<Restaurant> restaurants, LatLng userLocation, List<User> users) {
        mRestaurants = restaurants;
        mLocation = userLocation;
        mUsers = users;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding mBinding;

        public ViewHolder(ListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }

    public interface OnRestaurantListener {
        void onRestaurantClick(Restaurant restaurant);
    }
}
