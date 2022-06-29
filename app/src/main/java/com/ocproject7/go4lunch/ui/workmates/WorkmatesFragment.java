package com.ocproject7.go4lunch.ui.workmates;

import android.content.Context;
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
    private RecyclerView mRecyclerView;
    private WorkmatesAdapter adapter;
    private Context mContext;

    private static final String TAG = "TAG_WorkmatesFragment";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        Log.d(TAG, "onCreateView: ");

        configRecyclerView(view);
//        initData();
        mViewModel.getUsers();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private void initData() {
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mViewModel.mDetail.setValue(null);
        mViewModel.allUsers.setValue(null);
        mViewModel.allUsers.observe(requireActivity(), users -> {
            if(adapter != null){
                adapter.updateResults(users);
            }
        });

        mViewModel.mDetail.observe(requireActivity(), restaurant -> {
            if( restaurant != null) {
            Log.d(TAG, "initData: restaurant "+restaurant.getName());
                Intent intent = new Intent(mContext, DetailsRestaurantActivity.class);
                intent.putExtra("DETAILS", restaurant);
                detailsLauncher.launch(intent);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
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