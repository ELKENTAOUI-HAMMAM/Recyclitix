package com.example.smartrecycle.viewmodel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smartrecycle.R;
import com.example.smartrecycle.adapter.WasteHistoryAdapter;
import com.example.smartrecycle.api.RetrofitClient;
import com.example.smartrecycle.model.WasteResult;
import com.example.smartrecycle.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment implements WasteHistoryAdapter.OnWasteResultClickListener {
    
    private static final String TAG = "HistoryFragment";
    
    private RecyclerView recyclerView;
    private WasteHistoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<WasteResult> wasteResults = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupRecyclerView();
        loadHistory();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        
        // Back button
        view.findViewById(R.id.backButton).setOnClickListener(v -> 
            requireActivity().onBackPressed());
        
        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadHistory);
    }

    private void setupRecyclerView() {
        adapter = new WasteHistoryAdapter(requireContext(), wasteResults, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnWasteResultLongClickListener((wasteResult, position) -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Delete analysis")
                .setMessage("Are you sure you want to delete this analysis?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    wasteResults.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (wasteResults.isEmpty()) showEmptyState("No analysis found in your history");
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }

    private void loadHistory() {
        showLoading(true);
        
        // Vérifier si l'utilisateur est connecté
        SessionManager sessionManager = new SessionManager(requireContext());
        String token = sessionManager.getToken();
        
        if (token == null || token.isEmpty()) {
            showEmptyState("Please log in to view your history");
            return;
        }
        
        // Charger l'historique depuis le backend
        RetrofitClient.getAuthenticatedApiService(requireContext())
                .getWasteHistory()
                .enqueue(new Callback<List<WasteResult>>() {
                    @Override
                    public void onResponse(Call<List<WasteResult>> call, Response<List<WasteResult>> response) {
                        showLoading(false);
                        swipeRefreshLayout.setRefreshing(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            wasteResults = response.body();
                            if (wasteResults.isEmpty()) {
                                showEmptyState("No analysis found in your history");
                            } else {
                                showContent();
                                adapter.updateData(wasteResults);
                            }
                        } else {
                            Log.e(TAG, "Error loading history: " + response.code());
                            showEmptyState("Error loading history");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<WasteResult>> call, Throwable t) {
                        showLoading(false);
                        swipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "Network error loading history", t);
                        showEmptyState("Connection error");
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
    }

    private void showEmptyState(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.VISIBLE);
        emptyStateText.setText(message);
    }

    @Override
    public void onWasteResultClick(WasteResult wasteResult) {
        // Naviguer vers la page de détail de l'analyse
        WasteDetailFragment fragment = new WasteDetailFragment();
        
        Bundle args = new Bundle();
        args.putString("wasteType", wasteResult.getWasteType());
        args.putString("wasteCategory", wasteResult.getWasteCategory());
        args.putString("objectDescription", wasteResult.getObjectDescription());
        args.putString("instructions", wasteResult.getInstructions());
        args.putString("wasteIcon", wasteResult.getWasteIcon());
        args.putInt("wastePoints", wasteResult.getWastePoints() != null ? wasteResult.getWastePoints() : 0);
        args.putString("timeAgo", wasteResult.getTimeAgo());
        args.putString("wasteDate", wasteResult.getWasteDate());
        
        fragment.setArguments(args);
        
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("waste_detail")
                .commit();
    }
}
