package com.ocproject7.go4lunch.ui;

import static com.ocproject7.go4lunch.utils.Utils.loadImage;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.databinding.SubscribedItemBinding;
import com.ocproject7.go4lunch.models.User;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    private List<User> mUsers;

    public DetailsAdapter(List<User> users) {
        mUsers = users;
    }

    @NonNull
    @Override
    public DetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailsAdapter.ViewHolder(SubscribedItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        if (user.getUrlPicture() != null) {
            loadImage(holder.itemView.getContext(), user.getUrlPicture(), holder.mBinding.ivSubscribedPicture);
        } else {
            holder.mBinding.ivSubscribedPicture.setImageResource(R.drawable.baseline_account_circle_24);
        }

        holder.mBinding.tvSubscriber.setText(user.getUsername() + holder.itemView.getContext().getString(R.string.details_joining));
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
        SubscribedItemBinding mBinding;

        public ViewHolder(SubscribedItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
