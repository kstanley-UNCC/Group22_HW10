// Group22_HW10
// TripDetailsFragment.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.group22_hw10.databinding.FragmentTripDetailsBinding;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripDetailsFragment extends Fragment implements OnMapReadyCallback {

    FragmentTripDetailsBinding binding;

    private static final String ARG_TRIPID = "tripId";
    private static final String ARG_TRIPNAME = "tripName";
    private static final String ARG_TRIPSTART = "tripStart";
    private static final String ARG_TRIPEND = "tripEnd";
    private static final String ARG_TRIPSTATUS = "tripStatus";
    private static final String ARG_TRIPMILES = "tripMiles";
    private static final String ARG_STARTLATITUDE = "startLatitude";
    private static final String ARG_STARTLONGITUDE = "startLongitude";
    private static final String ARG_ENDLATITUDE = "endLatitude";
    private static final String ARG_ENDLONGITUDE = "endLongitude";

    private String tripId;
    private String tripName;
    private Timestamp tripStart;
    private Timestamp tripEnd;
    private String tripStatus;
    private double tripMiles;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;

    public TripDetailsFragment() {
        // Required empty public constructor
    }

    public static TripDetailsFragment newInstance(String tripId, String tripName, Timestamp tripStart, Timestamp tripEnd, String tripStatus, double tripMiles, double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        TripDetailsFragment fragment = new TripDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRIPID, tripId);
        args.putString(ARG_TRIPNAME, tripName);
        args.putParcelable(ARG_TRIPSTART, tripStart);
        args.putParcelable(ARG_TRIPEND, tripEnd);
        args.putString(ARG_TRIPSTATUS, tripStatus);
        args.putDouble(ARG_TRIPMILES, tripMiles);
        args.putDouble(ARG_STARTLATITUDE, startLatitude);
        args.putDouble(ARG_STARTLONGITUDE, startLongitude);
        args.putDouble(ARG_ENDLATITUDE, endLatitude);
        args.putDouble(ARG_ENDLONGITUDE, endLongitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tripId = getArguments().getString(ARG_TRIPID);
            tripName = getArguments().getString(ARG_TRIPNAME);
            tripStart = getArguments().getParcelable(ARG_TRIPSTART);
            tripEnd = getArguments().getParcelable(ARG_TRIPEND);
            tripStatus = getArguments().getString(ARG_TRIPSTATUS);
            tripMiles = getArguments().getDouble(ARG_TRIPMILES);
            startLatitude = getArguments().getDouble(ARG_STARTLATITUDE);
            startLongitude = getArguments().getDouble(ARG_STARTLONGITUDE);
            endLatitude = getArguments().getDouble(ARG_ENDLATITUDE);
            endLongitude = getArguments().getDouble(ARG_ENDLONGITUDE);
        }

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.map = map;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().setTitle(R.string.trip_details_title);

        binding.textViewTripName.setText(tripName);
        startDateFormat(tripStart);
        endDateFormat(tripEnd);
        binding.textViewTripStatus.setText(tripStatus);

        if (endLatitude != 0) {
            binding.textViewTripStatus.setTextColor(Color.GREEN);
        } else {
            binding.textViewTripStatus.setTextColor(Color.RED);
        }

        if (tripEnd == null) {
            binding.buttonComplete.setEnabled(true);
            binding.buttonComplete.setOnClickListener(v -> tripEnd = Timestamp.now());
        } else {
            binding.buttonComplete.setEnabled(false);
            binding.textViewTripMiles.setText(String.valueOf(tripMiles));
        }

        // Build the map.
        MapView mapView = binding.mapView;
        mapView.getMapAsync(this);
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
}