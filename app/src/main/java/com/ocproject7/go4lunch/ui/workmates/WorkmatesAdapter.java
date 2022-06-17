package com.ocproject7.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.ocproject7.go4lunch.databinding.WorkmatesItemBinding;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.ui.list.RecyclerViewAdapter;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder> {

    private List<User> mUsers;

    public WorkmatesAdapter(List<User> user){
        mUsers = user;
    }

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesAdapter.ViewHolder(WorkmatesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        if(user.getRestaurantId() != null){
            holder.mBinding.tvLunchAt.setText(user.getUsername()+ " is eating at "+user.getRestaurantName());
        } else {
            holder.mBinding.tvLunchAt.setText(user.getUsername()+ " hasn't decided yet");
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void updateResults(@NonNull final List<User> users) {
        mUsers = users;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
         WorkmatesItemBinding mBinding;

        public ViewHolder(WorkmatesItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

    }
}
