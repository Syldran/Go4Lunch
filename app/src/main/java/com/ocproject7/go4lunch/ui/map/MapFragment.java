package com.ocproject7.go4lunch.ui.map;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ocproject7.go4lunch.R;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment {

    private static String TAG = "TAG_MapFragment";
    private static final float DEFAULT_ZOOM = 15f;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private FusedLocationProviderClient client;
    private RestaurantViewModel mViewModel;
    private LatLng myLocation=null;
    BitmapDescriptor bitmapMarker;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            map = googleMap;
            map.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    Log.d(TAG, "onMyLocationClick: current location clicked and toast location informations");
                    Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
                }
            });
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (myLocation != null){
                        mViewModel.fetchRestaurants("My Location", myLocation, 1500);
                    }
                    return false;
                }
            });

            bitmapMarker = vectorToBitmap(R.drawable.ic_restaurant_marker);
            mViewModel.mRestaurants.observe(requireActivity(), restaurants -> {
                if (restaurants != null){
                    Log.d(TAG, "onMapReady: HERRREEEE");
                    addMarkers(restaurants);
                }
            });
            getCurrentLocation();
            enableMyLocation();

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantViewModel.class);

        initMap();
    }

    public void initMap(){
        Log.d(TAG, "initMap: ");
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: to lat : " + latLng.latitude + " & lng " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation: ");
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        try {

            Task<Location> location = client.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        moveCamera(myLocation, DEFAULT_ZOOM);
                        if (mViewModel.mRestaurants.getValue() != null) {
                            addMarkers(mViewModel.mRestaurants.getValue());
                        } else {
                            mViewModel.fetchRestaurants("My Location", myLocation, 1500);
                        }

                    } else {
                        Toast.makeText(getContext(), "Unable to get current location.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e(TAG, "getCurrentLocation: Security Exception " + e.getMessage());
        }
    }

    private void addMarkers(List<Restaurant> restaurants) {
        Log.d(TAG, "addMarkers: markers nearby : "+mViewModel.mLocation);
        map.clear();
        for (Restaurant restaurant : restaurants) {
            LatLng restoLocation = new LatLng(restaurant.getGeometry().getLocation().getLat(),restaurant.getGeometry().getLocation().getLng());
            map.addMarker(new MarkerOptions().position(restoLocation).title(restaurant.getName()).icon(bitmapMarker));
        }
        map.addMarker(new MarkerOptions().position(mViewModel.mLocation).title(mViewModel.mName));
        moveCamera(mViewModel.mLocation, DEFAULT_ZOOM);
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}