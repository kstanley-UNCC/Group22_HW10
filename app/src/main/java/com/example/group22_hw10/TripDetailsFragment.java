// Group22_HW10
// TripDetailsFragment.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.group22_hw10.databinding.FragmentTripDetailsBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

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
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(trip.getStart_latitude(), trip.getStart_longitude()))
                .title("Start"));

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

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

        binding.textViewTripName.setText(trip.getTrip_name());
        startDateFormat(trip.getCreated_at());
        endDateFormat(trip.getCompleted_at());
        binding.textViewTripStatus.setText(trip.getStatus());

        if (trip.getCompleted_at() != null) {
            binding.textViewTripStatus.setTextColor(Color.GREEN);
            binding.buttonComplete.setEnabled(true);
            binding.textViewTripMiles.setText(String.valueOf(trip.getTotal_miles()));
        } else {
            binding.textViewTripStatus.setTextColor(Color.RED);
            binding.buttonComplete.setEnabled(false);
        }

        binding.buttonComplete.setOnClickListener(v -> {
            trip.setCompleted_at(Timestamp.now());
        });
    }

    void startDateFormat(Timestamp tripStart) {
        Date date = tripStart.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy hh:mm a", Locale.getDefault());
        String dateFormat = sdf.format(date);
        binding.textViewTripStart.setText(dateFormat);
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

    private TripDetailsInterface listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (TripDetailsInterface) context;
    }

    public interface TripDetailsInterface {
        void updateTrip(Trip trip);
    }
}