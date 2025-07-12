package com.example.smartrecycle.viewmodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.smartrecycle.R;
import com.example.smartrecycle.adapter.WasteHistoryItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class WasteDetailFragment extends Fragment implements OnMapReadyCallback {

    private ImageView imageView;
    private TextView wasteTypeTextView, instructionsTextView, objectDescriptionTextView, categoryTextView;
    private Chip difficultyChip, recyclableChip;
    private Button addToHistoryButton, shareButton;
    private String imagePath;
    private byte[] imageByteArray;
    private GoogleMap mMap;
    private String wasteType;

    public static WasteDetailFragment newInstance(String wasteType, byte[] imageBytes, String imagePath) {
        WasteDetailFragment fragment = new WasteDetailFragment();
        Bundle args = new Bundle();
        args.putString("wasteType", wasteType);
        args.putByteArray("image", imageBytes);
        args.putString("imagePath", imagePath);
        fragment.setArguments(args);
        return fragment;
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
        // Try to get WasteResult from arguments if available
        WasteResult wasteResult = null;
        if (getArguments() != null && getArguments().containsKey("wasteResult")) {
            wasteResult = (WasteResult) getArguments().getSerializable("wasteResult");
        }
        // If wasteResult is available and has imageUrl, load from backend
        if (wasteResult != null && wasteResult.getImageUrl() != null && !wasteResult.getImageUrl().isEmpty()) {
            String baseUrl = "http://10.0.2.2:8080"; // Change to your backend base URL if needed
            Glide.with(requireContext())
                .load(baseUrl + wasteResult.getImageUrl())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(imageView);
        } else if (imageByteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            imageView.setImageBitmap(bitmap);
            if (imagePath == null || imagePath.isEmpty()) {
                imagePath = saveImageToInternalStorage(bitmap);
            }
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
        wasteResult.setWasteType(type);
        wasteResult.setWasteCategory(getWasteCategory(type));
        wasteResult.setObjectDescription(objectDescriptionTextView.getText().toString());
        wasteResult.setInstructions(instructionsTextView.getText().toString());
        wasteResult.setWastePoints(new Random().nextInt(16) + 5);
        wasteResult.setWasteDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date()));
        wasteResult.setTimeAgo("just now");
        // Ajoute d'autres champs si besoin (imageUrl, confidence, etc.)
        // wasteResult.setImageUrl(...);

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
        LatLng recyclingCenter = new LatLng(48.8566, 2.3522);
        mMap.addMarker(new MarkerOptions().position(recyclingCenter).title("Centre de recyclage Paris"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(recyclingCenter, 12f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        addRecyclingPoints();
    }

    private void addRecyclingPoints() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(48.8606, 2.3376)).title("Point de collecte - Louvre"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(48.8738, 2.2950)).title("Point de collecte - Arc de Triomphe"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(48.8584, 2.2945)).title("Centre de tri - Trocadéro"));
    }
}
