package com.example.smartrecycle.viewmodel;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.smartrecycle.R;
import com.example.smartrecycle.adapter.WasteHistoryItem;
import com.example.smartrecycle.model.RecyclingPoint;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.example.smartrecycle.model.WasteResult;
import com.bumptech.glide.Glide;
import com.example.smartrecycle.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WasteDetailFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "WasteDetailFragment";
    private ImageView imageView;
    private TextView wasteTypeTextView, instructionsTextView, objectDescriptionTextView, categoryTextView;
    private Chip difficultyChip, recyclableChip;
    private Button addToHistoryButton, shareButton;
    private String imagePath;
    private byte[] imageByteArray;
    private GoogleMap mMap;
    private String wasteType;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private LatLng userLocation;

    public static WasteDetailFragment newInstance(String wasteType, byte[] imageBytes, String imagePath) {
        WasteDetailFragment fragment = new WasteDetailFragment();
        Bundle args = new Bundle();
        args.putString("wasteType", wasteType);
        args.putByteArray("image", imageBytes);
        args.putString("imagePath", imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        setupPermissionLauncher();
    }

    private void setupPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted || coarseLocationGranted) {
                        enableMyLocation();
                    } else {
                        Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waste_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupToolbar(view);
        loadMap();

        if (getArguments() != null) {
            wasteType = getArguments().getString("wasteType");
            imageByteArray = getArguments().getByteArray("image");
            imagePath = getArguments().getString("imagePath");

            displayWasteDetails(wasteType);
            loadAndDisplayImage();
            setupButtons(wasteType);
        }
    }

    private void initializeViews(View view) {
        imageView = view.findViewById(R.id.imageView);
        wasteTypeTextView = view.findViewById(R.id.wasteType);
        instructionsTextView = view.findViewById(R.id.instructions);
        objectDescriptionTextView = view.findViewById(R.id.objectDescription);
        categoryTextView = view.findViewById(R.id.wasteCategory);
        difficultyChip = view.findViewById(R.id.difficultyChip);
        recyclableChip = view.findViewById(R.id.recyclableChip);
        addToHistoryButton = view.findViewById(R.id.addToHistoryButton);
        shareButton = view.findViewById(R.id.shareButton);
    }

    private void setupToolbar(View view) {
        View toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void displayWasteDetails(String type) {
        wasteTypeTextView.setText(formatWasteTypeDisplay(type));
        String category = getWasteCategory(type);
        categoryTextView.setText(category);
        String description = WasteClassifier.getWasteDescription(type);
        objectDescriptionTextView.setText(description);
        String instructions = WasteClassifier.getRecyclingInstructions(type);
        instructionsTextView.setText(instructions);
        setupChips(type);
    }

    private String formatWasteTypeDisplay(String type) {
        switch (type.toLowerCase()) {
            case "plastic": return "Plastic (Recyclable plastic)";
            case "paper": return "Paper (Recyclable)";
            case "glass": return "Glass (Recyclable)";
            case "metal": return "Metal (Recyclable)";
            case "cardboard": return "Cardboard (Recyclable)";
            case "trash": return "Non-recyclable waste";
            default: return type;
        }
    }

    private String getWasteCategory(String type) {
        switch (type.toLowerCase()) {
            case "plastic":
            case "paper":
            case "glass":
            case "metal":
            case "cardboard":
                return "Recyclable waste";
            case "trash":
                return "Waste disposal";
            default:
                return "Unknown category";
        }
    }

    private void setupChips(String type) {
        difficultyChip.setText("Easy");
        boolean isRecyclable = WasteClassifier.isRecyclable(type);
        recyclableChip.setVisibility(isRecyclable ? View.VISIBLE : View.GONE);
        if (isRecyclable) recyclableChip.setText("Recyclable");
    }

    private void loadAndDisplayImage() {
        // Try local byte array first (immediate result after scan)
        if (imageByteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            imageView.setImageBitmap(bitmap);
            if (imagePath == null || imagePath.isEmpty()) {
                imagePath = saveImageToInternalStorage(bitmap);
            }
            return;
        }

        // Fallback to WasteResult URL (useful for loading history items)
        WasteResult wasteResult = null;
        if (getArguments() != null && getArguments().containsKey("wasteResult")) {
            wasteResult = (WasteResult) getArguments().getSerializable("wasteResult");
        }
        
        if (wasteResult != null && wasteResult.getImageUrl() != null && !wasteResult.getImageUrl().isEmpty()) {
            String baseUrl = RetrofitClient.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            
            String imageUrl = wasteResult.getImageUrl();
            if (!imageUrl.startsWith("/")) {
                imageUrl = "/" + imageUrl;
            }
            
            String fullUrl = baseUrl + imageUrl;
            
            Glide.with(requireContext())
                .load(fullUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(imageView);
        } else if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.logo);
                Toast.makeText(requireContext(), "Impossible de charger l'image", Toast.LENGTH_SHORT).show();
            }
        } else {
            imageView.setImageResource(R.drawable.logo);
            Toast.makeText(requireContext(), "Aucune image disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtons(String type) {
        addToHistoryButton.setOnClickListener(v -> saveToBackend(type));
        shareButton.setOnClickListener(v -> shareImage());
    }

    private void saveToBackend(String type) {
        WasteResult wasteResult = new WasteResult();

        WasteResult scanResult = null;
        if (getArguments() != null && getArguments().containsKey("wasteResult")) {
            scanResult = (WasteResult) getArguments().getSerializable("wasteResult");
        }
        wasteResult.setWasteType(type);
        wasteResult.setWasteCategory(getWasteCategory(type));
        wasteResult.setObjectDescription(objectDescriptionTextView.getText().toString());
        wasteResult.setInstructions(instructionsTextView.getText().toString());
        wasteResult.setWastePoints(new Random().nextInt(16) + 5);
        wasteResult.setWasteDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date()));
        wasteResult.setTimeAgo("just now");

        if (scanResult != null) {
            wasteResult.setImageUrl(scanResult.getImageUrl());
            wasteResult.setConfidence(scanResult.getConfidence());
            wasteResult.setEnvironmentalImpact(scanResult.getEnvironmentalImpact());
            if (scanResult.getWastePoints() > 0) {
                wasteResult.setWastePoints(scanResult.getWastePoints());
            }
        }
        
        

        RetrofitClient.getAuthenticatedApiService(requireContext())
            .saveWasteResult(wasteResult)
            .enqueue(new Callback<WasteResult>() {
                @Override
                public void onResponse(Call<WasteResult> call, Response<WasteResult> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Added to history!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Error saving to backend", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<WasteResult> call, Throwable t) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void shareImage() {
        if (imageView.getDrawable() == null) {
            Toast.makeText(requireContext(), "Aucune image à partager", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable) imageView.getDrawable()).getBitmap();
        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            Uri contentUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    file
            );
            if (contentUri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Voici une image scannée avec SmartRecycle ♻️");
                startActivity(Intent.createChooser(shareIntent, "Partager l'image via"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Erreur lors du partage", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            File directory = new File(requireContext().getFilesDir(), "waste_images");
            if (!directory.exists()) directory.mkdirs();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "WASTE_" + timeStamp + ".jpg";
            File file = new File(directory, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            enableMyLocation();
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f));
                fetchNearbyRecyclingPoints(location.getLatitude(), location.getLongitude());
            } else {
                LatLng defaultLoc = new LatLng(31.6295, -7.9811);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 12f));
                fetchNearbyRecyclingPoints(31.6295, -7.9811);
            }
        });
    }

    private void fetchNearbyRecyclingPoints(double lat, double lng) {
        RetrofitClient.getApiService().getRecyclingPoints(lat, lng, 5000)
                .enqueue(new Callback<List<RecyclingPoint>>() {
                    @Override
                    public void onResponse(Call<List<RecyclingPoint>> call, Response<List<RecyclingPoint>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            addPointsToMap(response.body());
                        } else {
                            Log.e(TAG, "Failed to fetch points: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RecyclingPoint>> call, Throwable t) {
                        Log.e(TAG, "Error fetching points", t);
                        Toast.makeText(getContext(), "Unable to load nearby points", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPointsToMap(List<RecyclingPoint> points) {
        if (mMap == null) return;
        mMap.clear();

        for (RecyclingPoint point : points) {
            LatLng pos = new LatLng(point.latitude, point.longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(point.name)
                    .snippet(point.address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }
}
