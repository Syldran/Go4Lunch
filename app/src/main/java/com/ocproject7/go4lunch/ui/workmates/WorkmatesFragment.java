package com.ocproject7.go4lunch.ui.workmates;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.ui.DetailsRestaurantActivity;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

import java.util.ArrayList;

public class WorkmatesFragment extends Fragment implements WorkmatesAdapter.OnWorkmateListener {

    private RestaurantViewModel mViewModel;
    private WorkmatesAdapter adapter;

    private static final String TAG = "TAG_WorkmatesFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        Log.d(TAG, "onCreateView: ");

        configRecyclerView(view);
        initData();
        mViewModel.getUsers();
        return view;
    }

    private void initData() {
        mViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mViewModel.mDetail.setValue(null);

        mViewModel.allUsers.observe(getViewLifecycleOwner(), users -> {
            if (adapter != null) {
                adapter.updateResults(users);
            }
        });

        mViewModel.mDetail.observe(getViewLifecycleOwner(), restaurant -> {
            if (restaurant != null) {
                Log.d(TAG, "initData: restaurant " + restaurant.getName());
                Intent intent = new Intent(getActivity(), DetailsRestaurantActivity.class);
                intent.putExtra("DETAILS", restaurant);
                detailsLauncher.launch(intent);
            }
        });
    }

    private void configRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_workmates);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new WorkmatesAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onWorkmateClick(User user) {
        mViewModel.fetchDetailsRestaurant(user.getRestaurantId());

    }

    ActivityResultLauncher<Intent> detailsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            mViewModel.getUsers();
            mViewModel.updateUserFromFirestore(mViewModel.getCurrentUser().getUid());
            Log.d(TAG, "onActivityResult: workmate");
        }
    });
}