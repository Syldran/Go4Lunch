package com.ocproject7.go4lunch.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.models.Restaurant;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Restaurant> mRestaurants = new ArrayList<>();


    public RecyclerViewAdapter(List<Restaurant> restaurants) {
        mRestaurants = restaurants;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextTitle().setText(mRestaurants.get(position).getName());
        holder.getTextVicinity().setText(mRestaurants.get(position).getVicinity());
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
        private final TextView textTitle;
        private final TextView textVicinity;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textTitle = (TextView) view.findViewById(R.id.text_recycler_view_title);
            textVicinity = (TextView) view.findViewById(R.id.text_recycler_view_address);
        }

        public TextView getTextVicinity() {
            return textVicinity;
        }

        public TextView getTextTitle() {
            return textTitle;
        }
    }

}
