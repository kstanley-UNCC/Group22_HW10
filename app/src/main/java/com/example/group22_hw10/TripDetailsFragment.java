// Homework Assignment 10
// Group22_HW10
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.group22_hw10.databinding.FragmentTripDetailsBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripDetailsFragment extends Fragment implements OnMapReadyCallback {

    FragmentTripDetailsBinding binding;

    private static final String ARG_TRIP = "trip";

    private Trip trip;

    public static TripDetailsFragment newInstance(Trip trip) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, trip);

        TripDetailsFragment fragment = new TripDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng latLngStart = new LatLng(trip.getStart_latitude(), trip.getStart_longitude());

        googleMap.addMarker(new MarkerOptions()
                .position(latLngStart)
                .title("Start"));

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 15));

        // if the trip has been completed
        if (trip.getCompleted_at() != null) {
            // add 'end' marker
            LatLng latLngEnd = new LatLng(trip.getEnd_latitude(), trip.getEnd_longitude());

            googleMap.addMarker(new MarkerOptions()
                    .position(latLngEnd)
                    .title("End"));

            // zoom camera to fit both markers
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(latLngStart);
            builder.include(latLngEnd);
            LatLngBounds bounds = builder.build();

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }

        Log.d("demo", "onMapReady: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.trip = (Trip) getArguments().getSerializable(ARG_TRIP);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripDetailsBinding.inflate(inflater, container, false);

        // Initialize map fragment
        binding.mapView.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().setTitle(R.string.trip_details_title);

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        // Async map
        supportMapFragment.getMapAsync(this);

        binding.textViewTripName.setText(trip.getTrip_name());
        startDateFormat(trip.getCreated_at());
        endDateFormat(trip.getCompleted_at());
        binding.textViewTripStatus.setText(trip.getStatus());

        // if the trip has been completed
        if (trip.getCompleted_at() != null) {
            // set status color to green
            binding.textViewTripStatus.setTextColor(Color.GREEN);
            // disable and hide complete button
            binding.buttonComplete.setEnabled(false);
            binding.buttonComplete.setVisibility(View.GONE);
            // show total miles
            binding.textViewTripMiles.setVisibility(View.VISIBLE);
            DecimalFormat df = new DecimalFormat("0.00");
            binding.textViewTripMiles.setText(df.format(trip.getTotal_miles()) + " Miles");
        } else { //if the trip is on going
            // hide miles
            binding.textViewTripMiles.setVisibility(View.GONE);
            // set status color to red
            binding.textViewTripStatus.setTextColor(Color.RED);
            //enable and show complete button
            binding.buttonComplete.setEnabled(true);
            binding.buttonComplete.setVisibility(View.VISIBLE);
        }

        binding.buttonComplete.setOnClickListener(v -> {
            // Disable to avoid clicking more than once
            binding.buttonComplete.setEnabled(false);

            listener.getCurrentLocation(task -> {
                if (!task.isSuccessful()) {
                    Exception exception = task.getException();

                    assert exception != null;
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Error")
                            .setMessage(exception.getLocalizedMessage())
                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                            .show();

                    // Re-enable so user can interact with button again
                    binding.buttonComplete.setEnabled(true);
                    return;
                }

                Location location = (Location) task.getResult();

                trip.setCompleted_at(Timestamp.now());
                trip.setEnd_latitude(location.getLatitude());
                trip.setEnd_longitude(location.getLongitude());
                calculateTotalMiles(trip);
                listener.updateTrip(trip);
            });
        });
    }

    void startDateFormat(Timestamp tripStart) {
        Date date = tripStart.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy hh:mm a", Locale.getDefault());
        String dateFormat = sdf.format(date);        binding.textViewTripStart.setText(dateFormat);
    }

    void endDateFormat(Timestamp tripEnd) {
        if (tripEnd != null) {
            Date date = tripEnd.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy hh:mm a", Locale.getDefault());
            String dateFormat = sdf.format(date);
            binding.textViewTripCompleted.setText("Completed at: " + dateFormat);
        } else {
            binding.textViewTripCompleted.setText("Completed at: N/A");
        }
    }

    void calculateTotalMiles(Trip trip) {
        if (trip.getCompleted_at() != null) {
            Location start = new Location("");
            start.setLatitude(trip.getStart_latitude());
            start.setLongitude(trip.getStart_longitude());
            Location end = new Location("");
            end.setLatitude(trip.getEnd_latitude());
            end.setLongitude(trip.getEnd_longitude());
            double totalMiles = start.distanceTo(end);
            trip.setTotal_miles(totalMiles);
        }
    }

    private TripDetailsInterface listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (TripDetailsInterface) context;
    }

    public interface TripDetailsInterface {
        void getCurrentLocation(@Nullable OnCompleteListener onCompleteListener);
        void updateTrip(Trip trip);
    }
}