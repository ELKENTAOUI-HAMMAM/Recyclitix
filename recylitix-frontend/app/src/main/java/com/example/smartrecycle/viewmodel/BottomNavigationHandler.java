package com.example.smartrecycle.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.smartrecycle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHandler {

    public static void setupBottomNavigation(@NonNull AppCompatActivity activity,
                                             @NonNull BottomNavigationView bottomNav,
                                             int selectedItemId) {
        // Définir l'élément sélectionné
        bottomNav.setSelectedItemId(selectedItemId);

        // Configurer le listener pour la navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment targetFragment = null;

            // Déterminer le fragment cible
            if (id == R.id.navigation_home) {
                targetFragment = new HomeFragment();
            } else if (id == R.id.navigation_scan) {
                targetFragment = new ScanFragment();
            } else if (id == R.id.navigation_history) {
                targetFragment = new HistoryFragment();
            } else if (id == R.id.navigation_profile) {
                targetFragment = ProfileFragment.newInstance(new Bundle());
            }

            // Remplacer le fragment actuel
            if (targetFragment != null) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .commit();
                return true;
            }

            return false;
        });
    }

    public static void setupBottomNavigationForFragment(@NonNull Fragment currentFragment,
                                                        @NonNull BottomNavigationView bottomNav,
                                                        int selectedItemId) {
        // Définir l'élément sélectionné
        bottomNav.setSelectedItemId(selectedItemId);

        // Configurer le listener pour la navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment targetFragment = null;

            // Déterminer le fragment cible
            if (id == R.id.navigation_home) {
                targetFragment = new HomeFragment();
            } else if (id == R.id.navigation_scan) {
                targetFragment = new ScanFragment();
            } else if (id == R.id.navigation_history) {
                targetFragment = new HistoryFragment();
            } else if (id == R.id.navigation_profile) {
                targetFragment = ProfileFragment.newInstance(new Bundle());
            }

            // Remplacer le fragment actuel
            if (targetFragment != null) {
                FragmentManager fragmentManager = currentFragment.getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .commit();
                return true;
            }

            return false;
        });
    }

    /**
     * Update the selected item in bottom navigation
     */
    public static void updateSelectedItem(@NonNull BottomNavigationView bottomNav, int itemId) {
        bottomNav.setSelectedItemId(itemId);
    }

    /**
     * Get the fragment class for a navigation item
     */
    public static Class<? extends Fragment> getFragmentClass(int navigationItemId) {
        if (navigationItemId == R.id.navigation_home) {
            return HomeFragment.class;
        } else if (navigationItemId == R.id.navigation_scan) {
            return ScanFragment.class;
        } else if (navigationItemId == R.id.navigation_history) {
            return HistoryFragment.class;
        } else if (navigationItemId == R.id.navigation_profile) {
            return ProfileFragment.class;
        }
        return null;
    }

    /**
     * Check if the current fragment matches the navigation item
     */
    public static boolean isCurrentFragment(@NonNull Fragment currentFragment, int navigationItemId) {
        Class<? extends Fragment> targetClass = getFragmentClass(navigationItemId);
        return targetClass != null && targetClass.isInstance(currentFragment);
    }

    /**
     * Navigate to a specific fragment by navigation item ID
     */
    public static void navigateToFragment(@NonNull FragmentManager fragmentManager,
                                          int navigationItemId,
                                          Bundle args) {
        Fragment targetFragment = null;

        if (navigationItemId == R.id.navigation_home) {
            targetFragment = new HomeFragment();
        } else if (navigationItemId == R.id.navigation_scan) {
            targetFragment = new ScanFragment();
        } else if (navigationItemId == R.id.navigation_history) {
            targetFragment = new HistoryFragment();
        } else if (navigationItemId == R.id.navigation_profile) {
            targetFragment = ProfileFragment.newInstance(args != null ? args : new Bundle());
        }

        if (targetFragment != null) {
            if (args != null) {
                targetFragment.setArguments(args);
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .commit();
        }
    }

    /**
     * Navigate to a specific fragment with back stack
     */
    public static void navigateToFragmentWithBackStack(@NonNull FragmentManager fragmentManager,
                                                       int navigationItemId,
                                                       Bundle args,
                                                       String backStackName) {
        Fragment targetFragment = null;

        if (navigationItemId == R.id.navigation_home) {
            targetFragment = new HomeFragment();
        } else if (navigationItemId == R.id.navigation_scan) {
            targetFragment = new ScanFragment();
        } else if (navigationItemId == R.id.navigation_history) {
            targetFragment = new HistoryFragment();
        } else if (navigationItemId == R.id.navigation_profile) {
            targetFragment = ProfileFragment.newInstance(args != null ? args : new Bundle());
        }

        if (targetFragment != null) {
            if (args != null) {
                targetFragment.setArguments(args);
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .addToBackStack(backStackName)
                    .commit();
        }
    }
}
