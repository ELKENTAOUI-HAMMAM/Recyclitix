package com.example.smartrecycle.utils;

public class Constants {

    // API Keys
    public static final String OPENWEATHER_API_KEY = "d33816ed752774c721d5421cbada617e";
    public static final String OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    // SharedPreferences Keys
    public static final String PREFS_HISTORY = "history";
    public static final String PREFS_USER = "user_prefs";
    public static final String KEY_HISTORY_ITEMS = "history_items";
    public static final String KEY_TOTAL_SCANS = "total_scans";
    public static final String KEY_TOTAL_POINTS = "total_points";

    // Request Codes
    public static final int REQUEST_CAMERA_PERMISSION = 100;
    public static final int REQUEST_LOCATION_PERMISSION = 101;
    public static final int REQUEST_PICK_IMAGE = 102;
    public static final int REQUEST_TAKE_PHOTO = 103;

    // Waste Types
    public static final String WASTE_TYPE_PLASTIC = "plastic";
    public static final String WASTE_TYPE_GLASS = "glass";
    public static final String WASTE_TYPE_PAPER = "paper";
    public static final String WASTE_TYPE_METAL = "metal";
    public static final String WASTE_TYPE_CARDBOARD = "cardboard";
    public static final String WASTE_TYPE_ORGANIC = "organic";

    // Points System
    public static final int MIN_POINTS_PER_SCAN = 5;
    public static final int MAX_POINTS_PER_SCAN = 20;
    public static final int BONUS_POINTS_RECYCLABLE = 5;
    public static final int BONUS_POINTS_WEEKLY_GOAL = 50;

    // Image Processing
    public static final int IMAGE_SIZE = 224;
    public static final int IMAGE_QUALITY = 90;
    public static final String IMAGE_FORMAT = "JPEG";
    public static final String IMAGE_PREFIX = "WASTE_";

    // Map Settings
    public static final float DEFAULT_ZOOM_LEVEL = 12f;
    public static final double DEFAULT_LATITUDE = 48.8566; // Paris
    public static final double DEFAULT_LONGITUDE = 2.3522; // Paris

    // Animation Durations
    public static final int ANIMATION_DURATION_SHORT = 300;
    public static final int ANIMATION_DURATION_MEDIUM = 500;
    public static final int ANIMATION_DURATION_LONG = 1000;

    // File Paths
    public static final String IMAGES_DIRECTORY = "waste_images";
    public static final String CACHE_DIRECTORY = "images";
    public static final String SHARED_IMAGE_NAME = "shared_image.png";

    // Network Settings
    public static final int NETWORK_TIMEOUT = 10000; // 10 seconds
    public static final int LOCATION_UPDATE_INTERVAL = 10000; // 10 seconds
    public static final int LOCATION_FASTEST_INTERVAL = 5000; // 5 seconds

    // UI Constants
    public static final int GRID_SPAN_COUNT = 2;
    public static final int LIST_ITEM_ANIMATION_DELAY = 100;
    public static final float CARD_ELEVATION = 4f;
    public static final float CARD_CORNER_RADIUS = 12f;

    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_NAME_LENGTH = 50;
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    // Error Messages
    public static final String ERROR_NETWORK = "Network connection error";
    public static final String ERROR_LOCATION = "Location access error";
    public static final String ERROR_CAMERA = "Camera access error";
    public static final String ERROR_IMAGE_LOAD = "Failed to load image";
    public static final String ERROR_CLASSIFICATION = "Failed to classify waste";
    public static final String ERROR_SAVE_HISTORY = "Failed to save to history";

    // Success Messages
    public static final String SUCCESS_SAVE_HISTORY = "Added to history successfully";
    public static final String SUCCESS_SHARE = "Image shared successfully";
    public static final String SUCCESS_DELETE = "Item deleted successfully";

    // Date Formats
    public static final String DATE_FORMAT_DISPLAY = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_FILE = "yyyyMMdd_HHmmss";
    public static final String DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    // Recycling Tips
    public static final String[] RECYCLING_TIPS = {
        "Always clean containers before recycling",
        "Remove caps and lids from bottles",
        "Separate different materials",
        "Check local recycling guidelines",
        "Reduce, reuse, then recycle",
        "Compost organic waste when possible"
    };

    // Achievement Levels
    public static final int BEGINNER_SCANS = 10;
    public static final int INTERMEDIATE_SCANS = 50;
    public static final int ADVANCED_SCANS = 100;
    public static final int EXPERT_SCANS = 500;

    // Private constructor to prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}