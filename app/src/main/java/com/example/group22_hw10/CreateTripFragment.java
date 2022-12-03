// Group22_HW10
// CreateTripFragment.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.group22_hw10.databinding.FragmentCreateTripBinding;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class CreateTripFragment extends Fragment {

    FusedLocationProviderClient fusedLocationClient;

//    final static String[] PERMISSIONS = {
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//    };

    FragmentCreateTripBinding binding;
    LocationRequest locationRequest;
    Location currentLocation;

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

        Runnable callback = () -> {
            // when we get current location
            binding.textViewCurrentLocationStatus.setText(R.string.location_status_success);
            binding.textViewCurrentLocationStatus.setTextColor(Color.GREEN);
            binding.buttonSubmit.setEnabled(true);
        };

//        if (!hasPermissions(requireActivity(), requireContext(), PERMISSIONS)) {
//            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, MainActivity.LOCATION_PERMISSION_REQUEST_CODE);
//        }

        if (hasLocationPermission(requireContext())) {
            getCurrentLocation();
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showCustomDialog((dialog, which) -> multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}));
            } else {
                multiplePermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            }
        }

        binding.buttonSubmit.setOnClickListener(v -> {
            String tripName = binding.editTextTripName.getText().toString();
            mListener.createTrip(tripName, currentLocation);
        });
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

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder()
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setDurationMillis(5000)
                .setMaxUpdateAgeMillis(0)
                .build();

        CancellationTokenSource cnclTokenSrc = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(currentLocationRequest, cnclTokenSrc.getToken()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentLocation = task.getResult();
                binding.textViewCurrentLocationStatus.setText(R.string.location_status_success);
                binding.textViewCurrentLocationStatus.setTextColor(Color.GREEN);
                binding.buttonSubmit.setEnabled(true);
            } else {
                task.getException().printStackTrace();
            }
        });
    }

    interface AddTripListener {
        void createTrip(String trip_name, Location location);
        void goTrips();
    }

//    public static boolean hasPermissions(Activity activity, Context context, String... permissions) {
//        if (context != null && permissions != null) {
//            for (String permission : permissions) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
//                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                        return false;
//                    }
//                } else {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    public ActivityResultLauncher<String[]> multiplePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        boolean finePermissionAllowed;
        if (result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null) {
            finePermissionAllowed = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
            if (finePermissionAllowed) {
                getCurrentLocation();
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