package com.example.smartrecycle.viewmodel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartrecycle.R;
import com.example.smartrecycle.api.ApiClient;
import com.example.smartrecycle.api.RetrofitClient;
import com.example.smartrecycle.model.ImageRequest;
import com.example.smartrecycle.model.WasteResult;
import com.example.smartrecycle.model.ImageUploadResponse;
import com.example.smartrecycle.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ScanFragment extends Fragment {
    private static final String TAG = "ScanFragment";
    private static final int TAKE_PHOTO = 2;

    private ImageView imageView;
    private ProgressBar progressBar;
    private CardView progressCard;
    private LinearLayout emptyImageOverlay;
    private MaterialButton scanButton;
    private WasteClassifier wasteClassifier;
    private Uri imageUri;
    private boolean hasImage = false;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;

    public static ScanFragment newInstance(Uri imageUri) {
        ScanFragment fragment = new ScanFragment();
        if (imageUri != null) {
            Bundle args = new Bundle();
            args.putString("imageUri", imageUri.toString());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("imageUri")) {
            imageUri = Uri.parse(getArguments().getString("imageUri"));
            hasImage = true;
        }

        setupActivityResultLaunchers();
    }

    private void setupActivityResultLaunchers() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Bitmap bitmap = getBitmapFromUri(imageUri);
                        if (bitmap != null) {
                            displayImage(bitmap);
                            hasImage = true;
                            updateUIForImageLoaded();
                        }
                    }
                });

        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        if (bitmap != null) {
                            displayImage(bitmap);
                            hasImage = true;
                            updateUIForImageLoaded();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeClassifier();
        setupClickListeners(view);

        if (hasImage && imageUri != null) {
            loadInitialImage();
        }
    }

    private void initializeViews(View view) {
        imageView = view.findViewById(R.id.imageView);
        progressBar = view.findViewById(R.id.progressBar);
        progressCard = view.findViewById(R.id.progressCard);
        emptyImageOverlay = view.findViewById(R.id.emptyImageOverlay);
        scanButton = view.findViewById(R.id.scanButton);

        // Back button
        view.findViewById(R.id.backButton).setOnClickListener(v ->
            requireActivity().onBackPressed());
    }

    private void initializeClassifier() {
        try {
            wasteClassifier = new WasteClassifier(requireContext());
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error initializing classifier", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners(View view) {
        // Gallery button (embedded in card)
        view.findViewById(R.id.pickImageButton).setOnClickListener(v -> openGallery());

        // Camera button (embedded in card)
        view.findViewById(R.id.takePhotoButton).setOnClickListener(v -> takePhoto());

        // Scan button
        scanButton.setOnClickListener(v -> {
            if (!hasImage) {
                Toast.makeText(getContext(), "Please select or take a photo first", Toast.LENGTH_SHORT).show();
                return;
            }
            processImage();
        });
    }

    private void loadInitialImage() {
        Bitmap bitmap = getBitmapFromUri(imageUri);
        if (bitmap != null) {
            displayImage(bitmap);
            updateUIForImageLoaded();
        }
    }

    private void displayImage(Bitmap bitmap) {
        Bitmap scaledBitmap = getResizedBitmap(bitmap, 512);
        imageView.setImageBitmap(scaledBitmap);
    }

    private void updateUIForImageLoaded() {
        emptyImageOverlay.setVisibility(View.GONE);
        scanButton.setEnabled(true);
        scanButton.setAlpha(1.0f);
    }

    private void updateUIForImageEmpty() {
        emptyImageOverlay.setVisibility(View.VISIBLE);
        scanButton.setEnabled(false);
        scanButton.setAlpha(0.5f);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoLauncher.launch(intent);
    }

    private void processImage() {
        try {
            Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable) imageView.getDrawable()).getBitmap();
            showProgress(true);

            new Thread(() -> {
                try {
                    // Classification locale
                    String wasteType = wasteClassifier.classifyImage(bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();

                    // Upload image puis envoyer au backend
                    requireActivity().runOnUiThread(() -> uploadImageAndScan(bitmap, wasteType, byteArray));

                } catch (Exception e) {
                    requireActivity().runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageAndScan(Bitmap bitmap, String wasteType, byte[] imageBytes) {
        final File tempFile;
        try {
            tempFile = File.createTempFile("scan_", ".jpg", requireContext().getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (IOException e) {
            showProgress(false);
            Toast.makeText(getContext(), "Erreur lors de la préparation de l'image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody reqFile = RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), tempFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", tempFile.getName(), reqFile);

        RetrofitClient.getAuthenticatedApiService(requireContext())
            .uploadImage(body)
            .enqueue(new Callback<ImageUploadResponse>() {
                @Override
                public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String imageUrl = response.body().getImageUrl();
                        sendToBackendWithImageUrl(wasteType, imageBytes, imageUrl);
                    } else {
                        showProgress(false);
                        Toast.makeText(getContext(), "Erreur upload image", Toast.LENGTH_SHORT).show();
                    }
                    tempFile.delete();
                }

                @Override
                public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(getContext(), "Erreur réseau upload image", Toast.LENGTH_SHORT).show();
                    tempFile.delete();
                }
            });
    }

    private void sendToBackendWithImageUrl(String wasteType, byte[] imageBytes, String imageUrl) {
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setImageData(base64Image);
        imageRequest.setImageUrl(imageUrl);

        Log.d(TAG, "imageRequest: imageData=" + (base64Image != null ? base64Image.substring(0, Math.min(30, base64Image.length())) + "..." : "null") + ", imageUrl=" + imageUrl);

        SessionManager sessionManager = new SessionManager(requireContext());
        String token = sessionManager.getToken();

        if (token != null && !token.isEmpty()) {
            RetrofitClient.getAuthenticatedApiService(requireContext())
                    .scanWaste(imageRequest)
                    .enqueue(new Callback<WasteResult>() {
                        @Override
                        public void onResponse(Call<WasteResult> call, Response<WasteResult> response) {
                            requireActivity().runOnUiThread(() -> {
                                showProgress(false);
                                if (response.isSuccessful() && response.body() != null) {
                                    WasteResult wasteResult = response.body();
                                    Log.d(TAG, "Waste result saved to backend: " + wasteResult.getId());
                                    Toast.makeText(getContext(), "Analysis completed and saved!", Toast.LENGTH_SHORT).show();
                                    navigateToWasteDetail(wasteType, imageBytes, imageUri != null ? imageUri.toString() : null);
                                } else {
                                    Log.e(TAG, "Backend error: " + response.code());
                                    Toast.makeText(getContext(), "Error saving to backend", Toast.LENGTH_SHORT).show();
                                    navigateToWasteDetail(wasteType, imageBytes, imageUri != null ? imageUri.toString() : null);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<WasteResult> call, Throwable t) {
                            requireActivity().runOnUiThread(() -> {
                                showProgress(false);
                                Log.e(TAG, "Network error", t);
                                Toast.makeText(getContext(), "Network error, but analysis completed", Toast.LENGTH_SHORT).show();
                                navigateToWasteDetail(wasteType, imageBytes, imageUri != null ? imageUri.toString() : null);
                            });
                        }
                    });
        } else {
            RetrofitClient.getApiService()
                    .scanWaste(imageRequest)
                    .enqueue(new Callback<WasteResult>() {
                        @Override
                        public void onResponse(Call<WasteResult> call, Response<WasteResult> response) {
                            requireActivity().runOnUiThread(() -> {
                                showProgress(false);
                                if (response.isSuccessful() && response.body() != null) {
                                    WasteResult wasteResult = response.body();
                                    Log.d(TAG, "Waste result saved to backend (public): " + wasteResult.getId());
                                    Toast.makeText(getContext(), "Analysis completed!", Toast.LENGTH_SHORT).show();
                                    navigateToWasteDetail(wasteType, imageBytes, imageUri != null ? imageUri.toString() : null);
                                } else {
                                    Log.e(TAG, "Backend error: " + response.code());
                                    Toast.makeText(getContext(), "Error saving to backend", Toast.LENGTH_SHORT).show();
                                    navigateToWasteDetail(wasteType, imageBytes, imageUri != null ? imageUri.toString() : null);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<WasteResult> call, Throwable t) {
                            requireActivity().runOnUiThread(() -> {
                                showProgress(false);
                                Log.e(TAG, "Network error", t);
                                Toast.makeText(getContext(), "Network error, but analysis completed", Toast.LENGTH_SHORT).show();
                                navigateToWasteDetail(wasteType, imageBytes, imageUri != null ? imageUri.toString() : null);
                            });
                        }
                    });
        }
    }

    private void showProgress(boolean show) {
        progressCard.setVisibility(show ? View.VISIBLE : View.GONE);
        scanButton.setEnabled(!show);
        scanButton.setAlpha(show ? 0.5f : 1.0f);
    }

    private void navigateToWasteDetail(String wasteType, byte[] imageBytes, String imagePath) {
        WasteDetailFragment fragment = new WasteDetailFragment();

        Bundle args = new Bundle();
        args.putString("wasteType", wasteType);
        args.putByteArray("image", imageBytes);
        args.putString("imagePath", imagePath);
        fragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("waste_detail")
                .commit();
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float ratio = (float) width / height;
        if (ratio > 1) {
            width = maxSize;
            height = (int) (width / ratio);
        } else {
            height = maxSize;
            width = (int) (height * ratio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TAKE_PHOTO && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wasteClassifier != null) {
            wasteClassifier.close();
        }
    }
}
