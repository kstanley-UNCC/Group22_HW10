// Group22_HW10
// TripsFragment.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.group22_hw10.databinding.FragmentTripsBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripsFragment extends Fragment {

    FragmentTripsBinding binding;

    private static final String ARG_USER = "user";

    private FirebaseUser firebaseUser;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<Trip, TripHolder> adapter;

    public TripsFragment() {
        // Required empty public constructor
    }

    public static TripsFragment newInstance(FirebaseUser user) {
        TripsFragment fragment = new TripsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            firebaseUser = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_bar_logout) {
            mListener.logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTripsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonNewTrip.setOnClickListener(v -> mListener.goAddTrip());

        binding.tripsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("Trips")
                .orderBy("created_at", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Trip> options = new FirestoreRecyclerOptions.Builder<Trip>()
                .setQuery(query, Trip.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Trip, TripHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TripHolder holder, int position, @NonNull Trip model) {
                holder.setTrip_name(model.getTrip_name());
                holder.setTrip_start(model.getCreated_at());
                holder.setTrip_end(model.getCompleted_at());
                holder.setTrip_status(model.getStatus(), model.getEnd_latitude());
                holder.setTrip_miles(model.getTotal_miles());
                holder.setTrip_id(model.getTrip_id(), model.getTrip_name(), model.getCreated_at(), model.getCompleted_at(), model.getStatus(), model.getTotal_miles(), model.getStart_latitude(), model.getStart_longitude(), model.getEnd_latitude(), model.getEnd_longitude());
            }

            @NonNull
            @Override
            public TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_row_item, parent, false);
                return new TripHolder(view);
            }
        };

        binding.tripsRecyclerView.setAdapter(adapter);

        requireActivity().setTitle(R.string.trips_title);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public class TripHolder extends RecyclerView.ViewHolder {

        private final View view;

        public TripHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }

        void setTrip_name(String trip_name) {
            TextView textView = view.findViewById(R.id.textViewTripName);
            textView.setText(trip_name);
        }

        void setTrip_start(Timestamp created_at) {
            TextView textView = view.findViewById(R.id.textViewTripStart);
            Date date = created_at.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy hh:mm a", Locale.getDefault());
            String dateFormat = sdf.format(date);
            textView.setText("Started at: " + dateFormat);
        }

        void setTrip_end(Timestamp completed_at) {
            TextView textView = view.findViewById(R.id.textViewTripCompleted);
            if (completed_at == null) {
                textView.setText("Completed at: N/A");
            } else {
                Date date = completed_at.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy hh:mm a", Locale.getDefault());
                String dateFormat = sdf.format(date);
                textView.setText("Completed at: " + dateFormat);
            }
        }

        void setTrip_miles(double trip_miles) {
            TextView textView = view.findViewById(R.id.textViewTripMiles);
            textView.setText(trip_miles + " Miles");
        }

        void setTrip_status(String trip_status, double end_latitude) {
            TextView textView = view.findViewById(R.id.textViewTripStatus);
            TextView tripMiles = view.findViewById(R.id.textViewTripMiles);

            textView.setText(trip_status);

            if (end_latitude != 0) {
                textView.setTextColor(Color.GREEN);
                tripMiles.setVisibility(View.VISIBLE);
            } else {
                textView.setTextColor(Color.RED);
                tripMiles.setVisibility(View.INVISIBLE);
            }
        }

        void setTrip_id(String trip_id, String trip_name, Timestamp created_at, Timestamp completed_at, String status, double total_miles, double start_latitude, double start_longitude, double end_latitude, double end_longitude) {
            itemView.setOnClickListener(view -> mListener.goToTrip(trip_id, trip_name, created_at, completed_at, status, total_miles, start_latitude, start_longitude, end_latitude, end_longitude));
        }
    }

    TripsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (TripsListener) context;
    }

    interface TripsListener {
        void goAddTrip();
        void goToTrip(String trip_id, String trip_name, Timestamp created_at, Timestamp completed_at, String status, double total_miles, double start_latitude, double start_longitude, double end_latitude, double end_longitude);
        void logout();
    }
}