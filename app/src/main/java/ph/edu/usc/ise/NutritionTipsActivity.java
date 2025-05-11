package ph.edu.usc.ise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NutritionTipsActivity extends AppCompatActivity {

    private TextView topTitle, title, body;
    private ImageView backButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_tips);

        // Initialize views
        topTitle = findViewById(R.id.topTitle);
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        backButton = findViewById(R.id.back_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set back button functionality
        backButton.setOnClickListener(v -> finish());

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Set the current item as selected
        bottomNavigationView.setSelectedItemId(R.id.nav_nutrition);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_hygiene) {
                startActivity(new Intent(NutritionTipsActivity.this, HealthTipsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_nutrition) {
                return true;
            } else if (id == R.id.nav_mythbusters) {
                startActivity(new Intent(NutritionTipsActivity.this, MythbustersActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    // Optional: Handle back button to maintain navigation state
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}