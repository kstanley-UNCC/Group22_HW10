// Group22_HW10
// MainActivity.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.group22_hw10.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, CreateAccountFragment.SignUpListener, TripsFragment.TripsListener, CreateTripFragment.AddTripListener {
    public final static int LOCATION_PERMISSION_REQUEST_CODE = 1;

    ActivityMainBinding binding;
    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

    @Override
    public void authenticate(String username, String password) {
        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Exception exception = task.getException();
                assert exception != null;
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("An Error Occurred")
                        .setMessage(exception.getLocalizedMessage())
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                        .show();

                return;
            }

            this.firebaseUser = task.getResult().getUser();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rootView, TripsFragment.newInstance(this.firebaseUser))
                    .commit();
        });
    }

    @Override
    public void goCreateNewAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateAccountFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void createAccount(String name, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createTask -> {
            if (!createTask.isSuccessful()) {
                Exception exception = createTask.getException();
                assert exception != null;
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("An Error Occurred")
                        .setMessage(exception.getLocalizedMessage())
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                        .show();

                return;
            }

            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            FirebaseUser user = createTask.getResult().getUser();
            assert user != null;
            user.updateProfile(request).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Exception exception = task.getException();
                    assert exception != null;
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("An Error Occurred")
                            .setMessage(exception.getLocalizedMessage())
                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                            .show();

                    return;
                }

                this.firebaseUser = user;

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.rootView, TripsFragment.newInstance(this.firebaseUser))
                        .commit();
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean denied = false;

            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    denied = true;
                    break;
                }
            }

            if (denied) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Permissions Denied")
                        .setMessage("One or more location permissions were not granted, and as such the app cannot continue to function as anticipated.")
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                        .show();
                return;
            }
        } else {
            Log.d("demo", "onRequestPermissionsResult: Unknown permission request code: " + requestCode);
        }
    }

    public void getLastLocation(Runnable runnable) {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Unable to obtain last location.", Toast.LENGTH_LONG).show();
                });
                return;
            }

            Location location = task.getResult();
            if (location == null) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Unable to obtain last location.", Toast.LENGTH_LONG).show();
                });
            }

            runnable.run();
        });
    }

    @Override
    public void goLogin() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goAddTrip() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateTripFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void createTrip(String trip_name, Location location) {

    }

    @Override
    public void goTrips() {

    }
}