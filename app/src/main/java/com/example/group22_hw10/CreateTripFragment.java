// Group22_HW10
// CreateTripFragment.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.Manifest;
import android.app.Activity;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;

public class CreateTripFragment extends Fragment {
    final static String[] PERMISSIONS = {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    FragmentCreateTripBinding binding;
    LocationRequest locationRequest;
    Location currentLocation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateTripBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textViewCurrentLocationStatus.setText(R.string.location_status_loading);
        binding.textViewCurrentLocationStatus.setTextColor(Color.RED);

        binding.buttonSubmit.setOnClickListener(v -> {
            String tripName = binding.editTextTripName.getText().toString();
            mListener.createTrip(tripName, currentLocation);
        });
        binding.buttonSubmit.setEnabled(false);

        requireActivity().setTitle(R.string.create_trip_title);

        Runnable callback = () -> {
            // when we get current location
            binding.textViewCurrentLocationStatus.setText(R.string.location_status_success);
            binding.textViewCurrentLocationStatus.setTextColor(Color.GREEN);
            binding.buttonSubmit.setEnabled(true);
        };

        if (!hasPermissions(requireActivity(), requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, MainActivity.LOCATION_PERMISSION_REQUEST_CODE);
        }
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

    public static boolean hasPermissions(Activity activity, Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }
}