package ph.edu.usc.ise;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        TextView viewAllSymptoms = findViewById(R.id.viewAllSymptoms);
        TextView viewAllExposures = findViewById(R.id.viewAllExposures);
        LinearLayout environmentAlertBanner = findViewById(R.id.environmentAlertBanner);
        TextView dismissAlert = findViewById(R.id.dismissAlert);
        Button reportExposureButton = findViewById(R.id.reportExposureButton);
        Button logSymptomButton = findViewById(R.id.logSymptomButton);

        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
        bottomNav.setOnItemSelectedListener(this::onBottomNavigationItemSelected);

        // Set click listeners
        viewAllSymptoms.setOnClickListener(v -> {
            // Placeholder - replace with actual implementation
            startActivity(new Intent(this, AllSymptomsActivity.class));
            overridePendingTransition(0, 0);
        });

        viewAllExposures.setOnClickListener(v -> {
            // Placeholder - replace with actual implementation
            startActivity(new Intent(this, AllExposuresActivity.class));
            overridePendingTransition(0, 0);
        });

        dismissAlert.setOnClickListener(v -> {
            environmentAlertBanner.setVisibility(View.GONE);
        });

        reportExposureButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SymptomsTrackerActivity.class));
            overridePendingTransition(0, 0);
        });

        logSymptomButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SymptomsTrackerActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private boolean onBottomNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_health_log) {
            startActivity(new Intent(this, HealthLogActivity.class));
            overridePendingTransition(0, 0);
            finish();
            return true;
        } else if (id == R.id.nav_dashboard) {
            return true; // Already on dashboard
        } else if (id == R.id.nav_tips) {
            startActivity(new Intent(this, HealthTipsActivity.class));
            overridePendingTransition(0, 0);
            finish();
            return true;
        } else if (id == R.id.nav_symptoms) {
            startActivity(new Intent(this, SymptomsTrackerActivity.class));
            overridePendingTransition(0, 0);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}