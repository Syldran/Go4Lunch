package com.ocproject7.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.WorkmatesItemBinding;
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.ui.DetailsRestaurantActivity;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder> {

    private List<User> mUsers;
    private final OnWorkmateListener mOnWorkmateListener;

    public WorkmatesAdapter(List<User> user, OnWorkmateListener onWorkmateListener) {
        mUsers = user;
        mOnWorkmateListener = onWorkmateListener;
    }

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesAdapter.ViewHolder(WorkmatesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        if (user.getUrlPicture() != null) {
            DetailsRestaurantActivity.loadImage(holder.itemView.getContext(), user.getUrlPicture(), holder.mBinding.ivUserPicture);
        }

        if (user.getRestaurantId() != null) {
            holder.mBinding.tvLunchAt.setText(user.getUsername() + holder.itemView.getContext().getString(R.string.workmate_eating_at) + user.getRestaurantName());
        } else {
            holder.mBinding.tvLunchAt.setText(user.getUsername() + holder.itemView.getContext().getString(R.string.workmate_undecided));
        }

        holder.itemView.setOnClickListener(v -> {
            mOnWorkmateListener.onWorkmateClick(user);
        });
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

    public interface OnWorkmateListener {
        void onWorkmateClick(User user);
    }
}
