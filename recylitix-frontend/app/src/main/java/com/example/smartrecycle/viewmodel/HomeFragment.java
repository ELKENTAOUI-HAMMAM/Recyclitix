package com.example.smartrecycle.viewmodel;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartrecycle.R;
import com.example.smartrecycle.api.RetrofitClient;
import com.example.smartrecycle.model.WasteResult;
import com.example.smartrecycle.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private static final int PICK_IMAGE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationText;
    private TextView temperatureText;
    private TextView totalScansText;
    private TextView totalPointsText;
    private TextView thisWeekText;
    private TextView humidityText;
    private TextView windText;
    private TextView dateText;
    private android.widget.ImageView weatherIcon;
    private android.widget.ImageView btnRefreshWeather;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String OPENWEATHER_API_KEY = "d33816ed752774c721d5421cbada617e";
    private static final String OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private com.google.android.material.button.MaterialButton btnChatBot;
    private androidx.cardview.widget.CardView cardChatBot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupClickListeners(view);
        setupLocationServices();
        loadUserStats();

        return view;
    }

    private void initializeViews(View view) {
        locationText = view.findViewById(R.id.locationText);
        temperatureText = view.findViewById(R.id.temperatureText);
        totalScansText = view.findViewById(R.id.totalScansText);
        totalPointsText = view.findViewById(R.id.totalPointsText);
        thisWeekText = view.findViewById(R.id.thisWeekText);
        humidityText = view.findViewById(R.id.humidityText);
        windText = view.findViewById(R.id.windText);
        dateText = view.findViewById(R.id.dateText);
        weatherIcon = view.findViewById(R.id.weatherIcon);
        btnRefreshWeather = view.findViewById(R.id.btnRefreshWeather);
        cardChatBot = view.findViewById(R.id.cardChatBot);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    private void setupClickListeners(View view) {
        LinearLayout pickImage = view.findViewById(R.id.pickImage);
        LinearLayout scan = view.findViewById(R.id.Scan);
        Button btnHistory = view.findViewById(R.id.btnHistory);
        Button btnMap = view.findViewById(R.id.btnMap);

        scan.setOnClickListener(v -> launchScanFragment(null));
        pickImage.setOnClickListener(v -> openGallery());

        btnHistory.setOnClickListener(v -> {
            HistoryFragment historyFragment = new HistoryFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, historyFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnMap.setOnClickListener(v -> {
            Fragment mapsFragment = new MapsFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mapsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnRefreshWeather.setOnClickListener(v -> getLastLocation());

        cardChatBot.setOnClickListener(v -> {
            ChatBotFragment chatBotFragment = new ChatBotFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, chatBotFragment)
                    .addToBackStack("chatbot_fragment")
                    .commit();
        });
    }

    private void loadUserStats() {
        // Vérifier si l'utilisateur est connecté
        SessionManager sessionManager = new SessionManager(requireContext());
        String token = sessionManager.getToken();
        
        if (token == null || token.isEmpty()) {
            // Utilisateur non connecté - afficher des statistiques par défaut
            showDefaultStats();
            return;
        }
        
        // Charger les statistiques depuis le backend
        RetrofitClient.getAuthenticatedApiService(requireContext())
                .getWasteHistory()
                .enqueue(new Callback<List<WasteResult>>() {
                    @Override
                    public void onResponse(Call<List<WasteResult>> call, Response<List<WasteResult>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<WasteResult> wasteResults = response.body();
                            calculateAndDisplayStats(wasteResults);
                        } else {
                            Log.e(TAG, "Error loading user stats: " + response.code());
                            showDefaultStats();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<WasteResult>> call, Throwable t) {
                        Log.e(TAG, "Network error loading user stats", t);
                        showDefaultStats();
                    }
                });
    }

    private void calculateAndDisplayStats(List<WasteResult> wasteResults) {
        int totalScans = wasteResults.size();
            int totalPoints = 0;
            int thisWeekScans = 0;

            // Get current week start date
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date weekStart = calendar.getTime();

        for (WasteResult wasteResult : wasteResults) {
            // Calculer les points
            if (wasteResult.getWastePoints() != null) {
                totalPoints += wasteResult.getWastePoints();
            } else {
                // Points par défaut selon le type de déchet
                totalPoints += getDefaultPoints(wasteResult.getWasteType());
            }

            // Vérifier si l'analyse est de cette semaine
            if (wasteResult.getWasteDate() != null) {
                try {
                    // Convertir la date string en Date object
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date scanDate = sdf.parse(wasteResult.getWasteDate());
                    if (scanDate != null && scanDate.after(weekStart)) {
                        thisWeekScans++;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + wasteResult.getWasteDate(), e);
                }
            }
        }

        // Mettre à jour l'UI
        updateStatsUI(totalScans, totalPoints, thisWeekScans);
    }

    private int getDefaultPoints(String wasteType) {
        if (wasteType == null) return 5;
        
        switch (wasteType.toLowerCase()) {
            case "plastic":
                return 10;
            case "paper":
                return 8;
            case "glass":
                return 12;
            case "metal":
                return 15;
            case "organic":
                return 6;
            case "non-recyclable":
                return 2;
            default:
                return 5;
        }
    }

    private void updateStatsUI(int totalScans, int totalPoints, int thisWeekScans) {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
            totalScansText.setText(String.valueOf(totalScans));
            totalPointsText.setText(String.valueOf(totalPoints));
            thisWeekText.setText(String.valueOf(thisWeekScans));
            });
        }
    }

    private void showDefaultStats() {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
            totalScansText.setText("0");
            totalPointsText.setText("0");
            thisWeekText.setText("0");
                
                // Optionnel : afficher un message pour encourager la connexion
                Toast.makeText(requireContext(), "Connectez-vous pour voir vos statistiques", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void launchScanFragment(Uri imageUri) {
        ScanFragment scanFragment = ScanFragment.newInstance(imageUri);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, scanFragment)
                .addToBackStack("scan_fragment")
                .commit();
    }

    private void setupLocationServices() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            launchScanFragment(selectedImageUri);
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        updateLocationUI(location);
                    } else {
                        requestNewLocation();
                    }
                });
    }

    private void requestNewLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) return;
            for (android.location.Location location : locationResult.getLocations()) {
                updateLocationUI(location);
            }
            fusedLocationClient.removeLocationUpdates(this);
        }
    };

    private void updateLocationUI(android.location.Location location) {
        new Thread(() -> {
            android.location.Geocoder geocoder = new android.location.Geocoder(requireContext(), Locale.getDefault());
            try {
                List<android.location.Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);

                if (addresses != null && !addresses.isEmpty()) {
                    android.location.Address address = addresses.get(0);
                    String locality = address.getLocality();
                    String adminArea = address.getAdminArea();

                    requireActivity().runOnUiThread(() -> {
                        if (locality != null && adminArea != null) {
                            locationText.setText(locality + ", " + adminArea);
                        } else {
                            locationText.setText(address.getCountryName());
                        }
                        // Afficher la date et l'heure actuelles
                        String currentDateTime = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new java.util.Date());
                        dateText.setText(currentDateTime);
                    });

                    fetchWeatherData(location.getLatitude(), location.getLongitude());
                }
            } catch (IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Location error", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void fetchWeatherData(double lat, double lon) {
        String url = OPENWEATHER_BASE_URL +
                "?lat=" + lat +
                "&lon=" + lon +
                "&units=metric" +
                "&appid=" + OPENWEATHER_API_KEY;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject main = jsonResponse.getJSONObject("main");
                        double temp = main.getDouble("temp");
                        int humidity = main.getInt("humidity");
                        JSONObject wind = jsonResponse.getJSONObject("wind");
                        double windSpeed = wind.getDouble("speed");
                        JSONArray weatherArray = jsonResponse.getJSONArray("weather");
                        String icon;
                        if (weatherArray.length() > 0) {
                            icon = weatherArray.getJSONObject(0).getString("icon");
                        } else {
                            icon = "01d";
                        }

                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                temperatureText.setText(String.format(Locale.getDefault(), "%.1f°C", temp));
                                humidityText.setText("Hum: " + humidity + "%");
                                windText.setText("Vent: " + String.format(Locale.getDefault(), "%.0f", windSpeed * 3.6) + " km/h");
                                // Changer l'icône météo selon le code
                                int resId = getWeatherIconResId(icon);
                                if (resId != 0) weatherIcon.setImageResource(resId);
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Weather data error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Connection error", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    // Retourne la ressource drawable selon le code d'icône OpenWeatherMap
    private int getWeatherIconResId(String iconCode) {
        switch (iconCode) {
            case "01d": return R.drawable.ic_weather_sunny;
            case "01n": return R.drawable.ic_weather_sunny;
            case "02d": return R.drawable.ic_weather_sunny;
            case "02n": return R.drawable.ic_weather_sunny;
            case "03d":
            case "03n":
            case "04d":
            case "04n": return R.drawable.ic_cloud;
            case "09d":
            case "09n":
            case "10d":
            case "10n": return R.drawable.ic_rain;
            case "11d":
            case "11n": return R.drawable.ic_storm;
            case "13d":
            case "13n": return R.drawable.ic_snow;
            case "50d":
            case "50n": return R.drawable.ic_fog;
            default: return R.drawable.ic_weather_sunny;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh stats when returning to home
        loadUserStats();
    }
}

