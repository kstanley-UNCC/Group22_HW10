// Group22_HW10
// CreateTripFragment.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.group22_hw10.databinding.FragmentCreateTripBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class CreateTripFragment extends Fragment {

    FragmentCreateTripBinding binding;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateTripBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textViewCurrentLocationStatus.setText("Loading ...");
        binding.textViewCurrentLocationStatus.setTextColor(Color.YELLOW);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(getContext(), new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            currentLocation = location;
//                        }
//                    });
        }

        // when we get current location
        binding.textViewCurrentLocationStatus.setText("Success");
        binding.textViewCurrentLocationStatus.setTextColor(Color.GREEN);

        binding.buttonSubmit.setOnClickListener(v -> {
            String tripName = binding.editTextTripName.getText().toString();
           // mListener.createTrip(tripName, currentLocation);
        });

        requireActivity().setTitle(R.string.create_trip_title);
    }

    AddTripListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AddTripListener) context;
    }

    public void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();
    }

    interface AddTripListener {
        void createTrip(String trip_name, Location location);
        void goTrips();
    }
}