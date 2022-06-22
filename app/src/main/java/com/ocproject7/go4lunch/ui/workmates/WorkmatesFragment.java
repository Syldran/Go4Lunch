package com.ocproject7.go4lunch.ui.workmates;

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
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.ui.DetailsRestaurantActivity;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

import java.util.ArrayList;

public class WorkmatesFragment extends Fragment implements WorkmatesAdapter.OnWorkmateListener {

    private RestaurantViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private WorkmatesAdapter adapter;

    private static final String TAG = "TAG_WorkmatesFragment";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        configRecyclerView(view);
        initData();
        mViewModel.getUsers();
        return view;
    }

    private void initData() {
        mViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mViewModel.allUsers.observe(requireActivity(), users -> {
            adapter.updateResults(users);
        });
        mViewModel.mDetail.observe(requireActivity(), restaurant -> {
            Intent intent = new Intent(WorkmatesFragment.this.getActivity(), DetailsRestaurantActivity.class);
            intent.putExtra("DETAILS", mViewModel.mDetail.getValue());
            detailsLauncher.launch(intent);
        });
    }

    private void configRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_workmates);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new WorkmatesAdapter(new ArrayList<>(), this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onWorkmateClick(User user) {
        mViewModel.fetchDetailRestaurant(user.getRestaurantId());
    }

    ActivityResultLauncher<Intent> detailsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            mViewModel.getUsers();
        }
    });
}