package ph.edu.usc.ise;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class SymptomsTrackerActivity extends AppCompatActivity {

    private AutoCompleteTextView symptomTypeInput, hazardTypeInput;
    private TextInputEditText notesInput, locationInput; // Only notes and location use regular EditText
    private SeekBar severitySeekBar;
    private Button recordVoiceButton, getLocationButton, takePhotoButton, recordAudioButton, submitButton, viewLogsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_tracker);

        // Initialize views
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        // Symptom Log Views - Using AutoCompleteTextView
        symptomTypeInput = findViewById(R.id.symptomTypeInput);
        severitySeekBar = findViewById(R.id.severitySeekBar);
        notesInput = findViewById(R.id.notesInput); // This is TextInputEditText in XML
        recordVoiceButton = findViewById(R.id.recordVoiceButton);
        viewLogsButton = findViewById(R.id.viewLogsButton);

        // Exposure Reporting Views
        hazardTypeInput = findViewById(R.id.hazardTypeInput); // AutoCompleteTextView
        locationInput = findViewById(R.id.locationInput); // TextInputEditText
        getLocationButton = findViewById(R.id.getLocationButton);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        recordAudioButton = findViewById(R.id.recordAudioButton);
        submitButton = findViewById(R.id.submitButton);

        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_symptoms);
        bottomNav.setOnItemSelectedListener(this::onBottomNavigationItemSelected);

        // Setup button listeners
        setupButtonListeners();

        // Setup autocomplete suggestions (example for symptom types)
        setupAutocompleteAdapters();
    }

    private void setupAutocompleteAdapters() {
        // Example for symptom types
        String[] symptomTypes = new String[]{
                "Respiratory", "Skin irritation", "Headache",
                "Fatigue", "Nausea", "Dizziness"
        };

        ArrayAdapter<String> symptomAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                symptomTypes
        );
        symptomTypeInput.setAdapter(symptomAdapter);

        // Example for hazard types
        String[] hazardTypes = new String[]{
                "Air pollution", "Water contamination",
                "Chemical exposure", "Waste exposure",
                "Noise pollution", "Biological hazard"
        };

        ArrayAdapter<String> hazardAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                hazardTypes
        );
        hazardTypeInput.setAdapter(hazardAdapter);
    }

    // ... rest of your code remains the same ...
    private boolean onBottomNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_health_log) {
            startActivity(new Intent(this, HealthLogActivity.class));
            overridePendingTransition(0, 0);
            finish();
            return true;
        } else if (id == R.id.nav_dashboard) {
            startActivity(new Intent(this, DashboardActivity.class));
            overridePendingTransition(0, 0);
            finish();
            return true;
        } else if (id == R.id.nav_tips) {
            startActivity(new Intent(this, HealthTipsActivity.class));
            overridePendingTransition(0, 0);
            finish();
            return true;
        } else if (id == R.id.nav_symptoms) {
            return true;
        }
        return false;
    }

    private void setupButtonListeners() {
        recordVoiceButton.setOnClickListener(v -> {
            Toast.makeText(this, "Voice recording feature will be implemented", Toast.LENGTH_SHORT).show();
        });

        getLocationButton.setOnClickListener(v -> {
            locationInput.setText("Getting location...");
            Toast.makeText(this, "Location feature will be implemented", Toast.LENGTH_SHORT).show();
        });

        takePhotoButton.setOnClickListener(v -> {
            Toast.makeText(this, "Camera feature will be implemented", Toast.LENGTH_SHORT).show();
        });

        recordAudioButton.setOnClickListener(v -> {
            Toast.makeText(this, "Audio recording feature will be implemented", Toast.LENGTH_SHORT).show();
        });

        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                saveReport();
                Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
                clearForm();
            }
        });
        viewLogsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AllLogsActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private boolean validateForm() {
        if (symptomTypeInput.getText().toString().isEmpty() &&
                hazardTypeInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill at least symptom or exposure details", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveReport() {
        String report = "Symptom: " + symptomTypeInput.getText().toString() +
                "\nSeverity: " + severitySeekBar.getProgress() +
                "\nNotes: " + notesInput.getText().toString() +
                "\nHazard: " + hazardTypeInput.getText().toString() +
                "\nLocation: " + locationInput.getText().toString();
        // Save implementation
    }

    private void clearForm() {
        symptomTypeInput.setText("");
        severitySeekBar.setProgress(5);
        notesInput.setText("");
        hazardTypeInput.setText("");
        locationInput.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}