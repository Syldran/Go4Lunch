package com.ocproject7.go4lunch.ui.list;

import android.content.Intent;
import android.os.Bundle;
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
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.ui.DetailsRestaurantActivity;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

public class ListRestaurantFragment extends Fragment implements RecyclerViewAdapter.OnRestaurantListener {

    RestaurantViewModel mViewModel;
    private RecyclerViewAdapter adapter;


    private void initData() {
        mViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mViewModel.mDetails.observe(requireActivity(), restaurants -> {
            if (restaurants != null) {
                adapter.updateResults(restaurants, mViewModel.mLocation, mViewModel.allUsers.getValue());
            }
        });
        mViewModel.allUsers.observe(requireActivity(), users -> {
            if (users != null && mViewModel.mDetails.getValue() != null) {
                adapter.updateResults(Objects.requireNonNull(mViewModel.mDetails.getValue()), mViewModel.mLocation, mViewModel.allUsers.getValue());
            }
        });
    }

    public void configRecyclerView(View view) {
        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        configRecyclerView(view);
        initData();
        return view;
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(getActivity(), DetailsRestaurantActivity.class);
        intent.putExtra("DETAILS", restaurant);
        detailsLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> detailsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            mViewModel.getUsers();
            mViewModel.updateUserFromFirestore(mViewModel.getCurrentUser().getUid());
        }
    });
}