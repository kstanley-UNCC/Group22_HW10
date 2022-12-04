// Homework Assignment 10
// Group22_HW10
// Ken Stanley & Stephanie Karp

package com.example.group22_hw10;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.group22_hw10.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    FragmentLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(requireActivity(), "An email is required for authentication", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(requireActivity(), "A password is required for authentication", Toast.LENGTH_SHORT).show();
            } else {
                mListener.authenticate(email, password);
            }
        });

        binding.buttonCreateNewAccount.setOnClickListener(v -> mListener.goCreateNewAccount());

        requireActivity().setTitle(R.string.login_title);
    }

    LoginListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (LoginListener) context;
    }

    interface LoginListener {
        void authenticate(String username, String password);
        void goCreateNewAccount();
    }
}