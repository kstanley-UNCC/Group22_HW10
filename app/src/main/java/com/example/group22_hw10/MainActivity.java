// Group22_HW10
// MainActivity.java
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.group22_hw10.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }
}