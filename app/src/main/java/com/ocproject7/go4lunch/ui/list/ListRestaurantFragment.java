package com.ocproject7.go4lunch.ui.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

public class ListRestaurantFragment extends Fragment {

    RestaurantViewModel mViewModel;
    private final List<Restaurant> mRestaurants = new ArrayList<>();

    private static String TAG = "TAG_ListRestaurantFragment";

    private RecyclerView mRecyclerView;

    private void initData() {
        mViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        mViewModel.mRestaurants.observe(requireActivity(), restaurants -> {
            if (restaurants != null){
//                for (Restaurant restaurant : restaurants){
//                    Log.d(TAG, "onChanged: "+restaurant.getName());
//                }
                mRestaurants.clear();
                mRestaurants.addAll(restaurants);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
            else{
                Log.d(TAG, "onChanged: is null");
            }
        });
    }

    public void configRecyclerView(View view){
        Log.d(TAG, "configRecyclerView: ");
        // Set the adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mRestaurants);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        Log.d(TAG, "onCreateView: ");

        configRecyclerView(view);
        initData();
        return view;
    }
}