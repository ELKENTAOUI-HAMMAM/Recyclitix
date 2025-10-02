package com.example.smartrecycle.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidationUtils {

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) return false;
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) return false;

        // Check for at least one digit, one lowercase, one uppercase
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$");
        return pattern.matcher(password).matches();
    }

    /**
     * Validate name (first name, last name)
     */
    public static boolean isValidName(String name) {
        if (TextUtils.isEmpty(name)) return false;
        if (name.length() > Constants.MAX_NAME_LENGTH) return false;

        // Only letters and spaces allowed
        Pattern pattern = Pattern.compile("^[a-zA-Z\\s]+$");
        return pattern.matcher(name.trim()).matches();
    }

    /**
     * Validate phone number
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) return false;

        // Remove all non-digit characters
        String cleanPhone = phone.replaceAll("[^\\d]", "");

        // Check if it's between 10-15 digits
        return cleanPhone.length() >= 10 && cleanPhone.length() <= 15;
    }

    /**
     * Check if string is not empty
     */
    public static boolean isNotEmpty(String text) {
        return !TextUtils.isEmpty(text) && !text.trim().isEmpty();
    }

    /**
     * Validate waste type
     */
    public static boolean isValidWasteType(String wasteType) {
        if (TextUtils.isEmpty(wasteType)) return false;

        return wasteType.equals(Constants.WASTE_TYPE_PLASTIC) ||
               wasteType.equals(Constants.WASTE_TYPE_GLASS) ||
               wasteType.equals(Constants.WASTE_TYPE_PAPER) ||
               wasteType.equals(Constants.WASTE_TYPE_METAL) ||
               wasteType.equals(Constants.WASTE_TYPE_CARDBOARD) ||
               wasteType.equals(Constants.WASTE_TYPE_ORGANIC);
    }

    /**
     * Validate image file extension
     */
    public static boolean isValidImageExtension(String fileName) {
        if (TextUtils.isEmpty(fileName)) return false;

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") ||
               extension.equals("png") || extension.equals("bmp");
    }

    /**
     * Validate coordinates
     */
    public static boolean isValidLatitude(double latitude) {
        return latitude >= -90.0 && latitude <= 90.0;
    }

    public static boolean isValidLongitude(double longitude) {
        return longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * Get password strength level
     */
    public static PasswordStrength getPasswordStrength(String password) {
        if (TextUtils.isEmpty(password)) return PasswordStrength.WEAK;

        int score = 0;

        // Length check
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // Character variety checks
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;

        if (score <= 2) return PasswordStrength.WEAK;
        if (score <= 4) return PasswordStrength.MEDIUM;
        return PasswordStrength.STRONG;
    }

    public enum PasswordStrength {
        WEAK, MEDIUM, STRONG
    }

    /**
     * Sanitize input string
     */
    public static String sanitizeInput(String input) {
        if (TextUtils.isEmpty(input)) return "";

        return input.trim()
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("/", "&#x2F;");
    }
    public static boolean isValidUrl(String url) {
        return !TextUtils.isEmpty(url) && Patterns.WEB_URL.matcher(url).matches();
    }
}