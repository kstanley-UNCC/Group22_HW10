// Group22_HW10
// CreateTripFragment.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.group22_hw10.databinding.FragmentCreateTripBinding;
import com.example.group22_hw10.databinding.FragmentLoginBinding;

public class CreateTripFragment extends Fragment {

    FragmentCreateTripBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateTripBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSubmit.setOnClickListener(v -> {
            String tripName = binding.editTextTripName.getText().toString();
            //  Location userLocation;
           // mListener.createTrip(tripName, userLocation);
        });

        requireActivity().setTitle(R.string.create_trip_title);
    }

    AddTripListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AddTripListener) context;
    }

    interface AddTripListener {
        void createTrip(String trip_name, Location location);
        void goTrips();
    }
}