package com.ocproject7.go4lunch.ui.workmates;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.ui.list.RecyclerViewAdapter;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

import java.util.ArrayList;

public class WorkmatesFragment extends Fragment {

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
            if (users != null){
                for (User user : users){
                    Log.d(TAG, "initData: user is "+user.getUsername());
                }
            }
            Log.d(TAG, "initData: HERE");
            adapter.updateResults(users);
        });
    }

    private void configRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_workmates);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new WorkmatesAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(adapter);
    }

}