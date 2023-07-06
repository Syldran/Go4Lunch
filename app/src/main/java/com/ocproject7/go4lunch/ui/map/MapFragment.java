package com.ocproject7.go4lunch.ui.map;

import static com.ocproject7.go4lunch.ui.SettingsActivity.MyPREFERENCES;
import static com.ocproject7.go4lunch.ui.SettingsActivity.RADIUS;
import static com.ocproject7.go4lunch.ui.SettingsActivity.RANKBY;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;
import com.ocproject7.go4lunch.viewmodels.ViewModelFactory;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MapFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "TAG_MapFragment";
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 101;

    private SupportMapFragment mapFragment;
    private String rankBy;
    private int radius;
    private GoogleMap map;
    private FusedLocationProviderClient client;
    private RestaurantViewModel mViewModel;
    private LatLng myLocation = null;
    BitmapDescriptor bitmapMarker;
    BitmapDescriptor bitmapMarkerSubscribed;
    List<User> users = null;

    SharedPreferences sharedPreferences;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

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
            users = mViewModel.allUsers.getValue();
            bitmapMarker = vectorToBitmap(R.drawable.ic_restaurant_marker);
            bitmapMarkerSubscribed = vectorToBitmap(R.drawable.ic_restaurant_marker_subscribed);

            map.setOnMyLocationClickListener(location -> Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show());
            map.setOnMyLocationButtonClickListener(() -> {
                if (myLocation != null) {
                    mViewModel.setCurrentPosName("My Location");
                    mViewModel.setLocation(myLocation);
                    mViewModel.fetchRestaurants(radius, rankBy);
                }
                return false;
            });

            mViewModel.allUsers.observe(requireActivity(), users1 -> {
                users = users1;
                addMarkers(mViewModel.mRestaurants.getValue());
            });

            mViewModel.mRestaurants.observe(requireActivity(), restaurants -> {
                addMarkers(restaurants);
            });


            if (hasLocationPermission()) {
                getCurrentLocation();
                enableMyLocation();
            } else {
                requestLocationPermission();
            }
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
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantViewModel.class);
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        rankBy = sharedPreferences.getString(RANKBY, "prominence");
        radius = sharedPreferences.getInt(RADIUS, 1500);
        initMap();
    }

    public void initMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void enableMyLocation() {
        map.setMyLocationEnabled(true);
    }

    private void moveCamera(LatLng latLng, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getCurrentLocation() {
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
                            mViewModel.setCurrentPosName("My Location");
                            mViewModel.setLocation(myLocation);
                            mViewModel.fetchRestaurants(radius, rankBy);
                        }

                    } else {
                        Toast.makeText(getContext(), "Unable to get current location.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (SecurityException e) {
        }
    }

    private void addMarkers(List<Restaurant> restaurants) {
        map.clear();
        if (restaurants != null && users != null) {
            for (Restaurant restaurant : restaurants) {
                LatLng restoLocation = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
                if (getSubscribed(restaurant)) {
                    map.addMarker(new MarkerOptions().position(restoLocation).title(restaurant.getName()).icon(bitmapMarkerSubscribed));
                } else {
                    map.addMarker(new MarkerOptions().position(restoLocation).title(restaurant.getName()).icon(bitmapMarker));
                }
            }
        }
        map.addMarker(new MarkerOptions().position(mViewModel.mLocation).title(mViewModel.mName));
        moveCamera(mViewModel.mLocation, DEFAULT_ZOOM);
    }

    public boolean getSubscribed(Restaurant restaurant) {
        if (users != null) {
            for (User user : users) {
                if (Objects.equals(user.getRestaurantId(), restaurant.getPlaceId())) {
                    return true;
                }
            }
        }
        return false;
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


    private boolean hasLocationPermission() {
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
    }


    private void requestLocationPermission() {
        EasyPermissions.requestPermissions(this, "This application cannot work without Location Permission.", PERMISSION_LOCATION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(requireContext(), "permissions granted", Toast.LENGTH_SHORT).show();
        getCurrentLocation();
        enableMyLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestLocationPermission();
        }
    }
}