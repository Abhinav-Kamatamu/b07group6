package com.example.b07group6;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        bottomNav = findViewById(R.id.bottom_navigation);
        View root = findViewById(R.id.main);


        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime  = windowInsets.getInsets(WindowInsetsCompat.Type.ime());

            // Status bar (top) + side gesture areas go on the root.
            // Do NOT apply bars.bottom here — that's what lifts the nav bar and
            // creates the gap. Only pad the bottom while the keyboard is open.
            v.setPadding(bars.left, bars.top, bars.right, ime.bottom);

            // The nav bar reaches the true bottom edge (drawing behind the system
            // navigation bar) and lifts its icons/labels above it with padding.
            bottomNav.setPadding(0, 0, 0, bars.bottom);

            return windowInsets;
        });






//        // VERY IMPORTANT LINE!!!! Removes excess padding on bottomNav
//        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> insets);
//        ViewCompat.requestApplyInsets(bottomNav);

        handleBottomNavigation();

    }

    private void handleBottomNavigation(){
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.main_activity_fragment_view);
            assert navHostFragment != null;

            if (itemId == R.id.nav_saved) {
                // switch to saved artifacts fragment
                navHostFragment.getNavController().navigate(R.id.action_global_to_saved);
                return true;
            } else if (itemId == R.id.nav_home) {
                // switch to add and edit fragment
                navHostFragment.getNavController().navigate(R.id.action_global_to_home);
                return true;
            } else if (itemId == R.id.nav_add) {
                navHostFragment.getNavController().navigate(R.id.action_global_to_add_edit);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // switch to profile fragment
                navHostFragment.getNavController().navigate(R.id.action_global_to_profile);
                return true;
            }

            return false;
        });
    }
}