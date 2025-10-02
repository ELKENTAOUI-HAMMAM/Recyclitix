package com.example.smartrecycle.viewmodel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartrecycle.R;
import com.example.smartrecycle.api.ApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.smartrecycle.api.RetrofitClient;
import com.example.smartrecycle.model.RecyclingPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private CardView bottomSheet;
    private EditText searchInput;
    private FloatingActionButton btnFilter;
    private View rootView;
    private List<RecyclingPoint> recyclingPoints = new ArrayList<>();
    private Map<Marker, RecyclingPoint> markerToRecyclingPoint = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
        setupToolbar();
        setupSearch();
        setupFilterButton();
        initializeMap();
    }

    private void initializeViews() {
        bottomSheet = rootView.findViewById(R.id.bottomSheet);
        searchInput = rootView.findViewById(R.id.searchInput);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    private void setupToolbar() {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        } else {
            // Si pas de toolbar, on peut ajouter un bouton de retour alternatif
            // ou simplement ignorer cette fonctionnalité
            android.util.Log.w("MapsFragment", "Toolbar not found in layout");
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void enableMyLocation() {
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14));
                        fetchRecyclingPoints(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(getContext(), "Position non disponible", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchRecyclingPoints(double lat, double lng) {
        int radius = 2000; // 2km
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getRecyclingPoints(lat, lng, radius).enqueue(new Callback<List<RecyclingPoint>>() {
            @Override
            public void onResponse(Call<List<RecyclingPoint>> call, Response<List<RecyclingPoint>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recyclingPoints = response.body();
                    if (recyclingPoints.isEmpty()) {
                        Toast.makeText(getContext(), "Aucun point de tri trouvé à proximité", Toast.LENGTH_SHORT).show();
                    }
                    displayRecyclingPoints();
                } else {
                    Toast.makeText(getContext(), "Erreur lors du chargement des points de tri", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RecyclingPoint>> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRecyclingPoints() {
        if (mMap == null) return;
        mMap.clear();
        markerToRecyclingPoint.clear();

        for (RecyclingPoint point : recyclingPoints) {
            float markerColor = getMarkerColor(point.type);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(point.latitude, point.longitude))
                    .title(point.name)
                    .snippet(point.address)
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
            if (marker != null) {
                markerToRecyclingPoint.put(marker, point);
            }
        }
    }

    private float getMarkerColor(String type) {
        switch (type) {
            case "PLASTIC": return BitmapDescriptorFactory.HUE_YELLOW;
            case "GLASS": return BitmapDescriptorFactory.HUE_GREEN;
            case "PAPER": return BitmapDescriptorFactory.HUE_AZURE;
            case "METAL": return BitmapDescriptorFactory.HUE_ORANGE;
            default: return BitmapDescriptorFactory.HUE_RED;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        RecyclingPoint point = markerToRecyclingPoint.get(marker);
        if (point != null) {
            showBottomSheet(point);
            return true;
        }
        return false;
    }

    private void showBottomSheet(RecyclingPoint point) {
        if (bottomSheet == null) return;

        TextView locationName = rootView.findViewById(R.id.locationName);
        TextView locationAddress = rootView.findViewById(R.id.locationAddress);
        TextView locationHours = rootView.findViewById(R.id.locationHours);
        TextView locationContact = rootView.findViewById(R.id.locationContact);
        ChipGroup materialsChipGroup = rootView.findViewById(R.id.materialsChipGroup);
        ImageView locationTypeIcon = rootView.findViewById(R.id.locationTypeIcon);

        if (locationName != null) locationName.setText(point.name);
        if (locationAddress != null) locationAddress.setText(point.address);
        if (locationHours != null) locationHours.setText(point.hours != null ? point.hours : "");
        if (locationContact != null) locationContact.setText(point.contact != null ? point.contact : "");

        // Affiche l'icône selon le type
        if (locationTypeIcon != null) {
            int iconRes = R.drawable.ic_recycling_center;
            switch (point.type) {
                case "PLASTIC": iconRes = R.drawable.ic_plastic; break;
                case "GLASS": iconRes = R.drawable.ic_glass; break;
                case "PAPER": iconRes = R.drawable.ic_paper; break;
                case "METAL": iconRes = R.drawable.ic_metal; break;
            }
            locationTypeIcon.setImageResource(iconRes);
        }

        // Clear existing chips and add new ones
        if (materialsChipGroup != null) {
            materialsChipGroup.removeAllViews();
            if (point.acceptedMaterials != null) {
                for (String material : point.acceptedMaterials) {
                    Chip chip = new Chip(getContext());
                    chip.setText(material);
                    chip.setChipBackgroundColorResource(R.color.successGreen);
                    chip.setTextColor(getResources().getColor(android.R.color.white));
                    materialsChipGroup.addView(chip);
                }
            }
        }

        bottomSheet.setVisibility(View.VISIBLE);

        View btnDirections = rootView.findViewById(R.id.btnDirections);
        if (btnDirections != null) {
            btnDirections.setOnClickListener(v -> {
                LatLng location = new LatLng(point.latitude, point.longitude);
                String uri = "google.navigation:q=" + location.latitude + "," + location.longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");

                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Fallback to generic maps intent
                    String mapsUri = "geo:" + location.latitude + "," + location.longitude + "?q=" +
                            location.latitude + "," + location.longitude + "(" + point.name + ")";
                    Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(mapsUri));
                    startActivity(fallbackIntent);
                }
            });
        }
    }

    private void setupSearch() {
        if (searchInput != null) {
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    searchRecyclingPoints(s.toString());
                }
            });
        }
    }

    private void searchRecyclingPoints(String query) {
        if (query.isEmpty()) {
            displayRecyclingPoints();
            return;
        }
        if (mMap == null) return;
        mMap.clear();
        markerToRecyclingPoint.clear();
        for (RecyclingPoint point : recyclingPoints) {
            if (point.name.toLowerCase().contains(query.toLowerCase()) ||
                    point.address.toLowerCase().contains(query.toLowerCase())) {
                float markerColor = getMarkerColor(point.type);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title(point.name)
                        .snippet(point.address)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
                if (marker != null) {
                    markerToRecyclingPoint.put(marker, point);
                }
            }
        }
    }

    private void setupFilterButton() {
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Filter options coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(getContext(), "Location permission is required to show nearby recycling points",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up map fragment to prevent memory leaks
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(mapFragment)
                    .commitAllowingStateLoss();
        }
    }
}
