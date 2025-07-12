package com.example.smartrecycle.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartrecycle.R;
import com.example.smartrecycle.api.ApiService;
import com.example.smartrecycle.api.RetrofitClient;
import com.example.smartrecycle.model.User;
import com.example.smartrecycle.model.WasteResult;
import com.example.smartrecycle.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(Bundle args) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        TextView firstNameTextView = view.findViewById(R.id.nametext1);
        TextView lastNameTextView = view.findViewById(R.id.nametext2);
        TextView emailTextView = view.findViewById(R.id.emailText);
        TextView fullNameText = view.findViewById(R.id.fullNameText);
        TextView emailDisplayText = view.findViewById(R.id.emailDisplayText);
        TextView totalScansCount = view.findViewById(R.id.totalScansCount);
        TextView totalPointsCount = view.findViewById(R.id.totalPointsCount);

        ApiService apiService = RetrofitClient.getAuthenticatedApiService(requireContext());
        apiService.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    firstNameTextView.setText(user.getFirstName());
                    lastNameTextView.setText(user.getLastName());
                    emailTextView.setText(user.getEmail());
                    fullNameText.setText(user.getFirstName() + " " + user.getLastName());
                    emailDisplayText.setText(user.getEmail());
                } else {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch waste history to calculate stats
        apiService.getWasteHistory().enqueue(new Callback<List<WasteResult>>() {
            @Override
            public void onResponse(Call<List<WasteResult>> call, Response<List<WasteResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WasteResult> history = response.body();
                    int totalScans = history.size();
                    int totalPoints = 0;
                    for (WasteResult result : history) {
                        if (result.getWastePoints() != null) {
                            totalPoints += result.getWastePoints();
                        }
                    }
                    totalScansCount.setText(String.valueOf(totalScans));
                    totalPointsCount.setText(String.valueOf(totalPoints));
                } else {
                    totalScansCount.setText("0");
                    totalPointsCount.setText("0");
                }
            }

            @Override
            public void onFailure(Call<List<WasteResult>> call, Throwable t) {
                totalScansCount.setText("0");
                totalPointsCount.setText("0");
            }
        });

        view.findViewById(R.id.logoutButton).setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}

