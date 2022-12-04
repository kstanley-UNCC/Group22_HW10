// Group22_HW10
// CreateTripFragment.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.group22_hw10.databinding.FragmentCreateTripBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;

public class CreateTripFragment extends Fragment {

    FusedLocationProviderClient fusedLocationClient;

    FragmentCreateTripBinding binding;
    LocationRequest locationRequest;
    LatLng currentLocation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateTripBinding.inflate(inflater, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textViewCurrentLocationStatus.setText(R.string.location_status_loading);
        binding.textViewCurrentLocationStatus.setTextColor(Color.RED);
        binding.buttonSubmit.setEnabled(false);

        requireActivity().setTitle(R.string.create_trip_title);

        if (hasLocationPermission(requireContext())) {
            mListener.getCurrentLocation(null);
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showCustomDialog((dialog, which) -> multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}));
            } else {
                multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            }
        }

        binding.buttonSubmit.setOnClickListener(v -> {
            String tripName = binding.editTextTripName.getText().toString();

            if (tripName.isEmpty()) {
                new androidx.appcompat.app.AlertDialog.Builder(requireActivity())
                        .setTitle("Error")
                        .setMessage("A trip name must be given before you can save the trip.")
                        .setPositiveButton("Ok", (dialog, which) -> {
                            dialog.dismiss();
                            binding.editTextTripName.requestFocus();
                        })
                        .show();
                return;
            }

            mListener.createTrip(tripName, currentLocation.latitude, currentLocation.longitude);
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
        void createTrip(String trip_name, double lat, double longi);
        void getCurrentLocation(OnCompleteListener onCompleteListener);
    }

    public ActivityResultLauncher<String[]> multiplePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        boolean finePermissionAllowed;
        if (result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null) {
            finePermissionAllowed = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
            if (finePermissionAllowed) {
                mListener.getCurrentLocation(task -> {
                    if (task.isSuccessful()) {
                        Location location = (Location)task.getResult();
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d("demo", "getCurrentLocation: " + currentLocation);
                        binding.textViewCurrentLocationStatus.setText(R.string.location_status_success);
                        binding.textViewCurrentLocationStatus.setTextColor(Color.GREEN);
                        binding.buttonSubmit.setEnabled(true);
                    } else {
                        task.getException().printStackTrace();
                    }
                });
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showCustomDialog((dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivity(intent);
                    });
                }
            }
        }
    });

    void showCustomDialog(DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Location Permission")
                .setMessage("This app needs the location permission to track your location!")
                .setPositiveButton("Allow", positiveListener)
                .setNegativeButton("Deny", null);
        builder.create().show();
    }

    private boolean hasLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}