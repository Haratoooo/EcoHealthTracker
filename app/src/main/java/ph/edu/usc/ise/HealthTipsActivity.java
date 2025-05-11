package ph.edu.usc.ise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HealthTipsActivity extends AppCompatActivity {

    private TextView topTitle, title, body;
    private ImageView backButton;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        // Initialize views
        topTitle = findViewById(R.id.topTitle);
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        backButton = findViewById(R.id.back_button);
        bottomNav = findViewById(R.id.bottom_navigation);

        // Set back button
        backButton.setOnClickListener(v -> finish());

        // Setup bottom navigation
        setupBottomNavigation();

        // Set initial content based on which item was selected
        updateContentForTab(bottomNav.getSelectedItemId());
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == bottomNav.getSelectedItemId()) {
                return true; // Already on this tab
            }

            if (id == R.id.nav_hygiene) {
                startActivity(new Intent(this, HealthTipsActivity.class));
            } else if (id == R.id.nav_nutrition) {
                startActivity(new Intent(this, NutritionTipsActivity.class));
            } else if (id == R.id.nav_mythbusters) {
                startActivity(new Intent(this, MythbustersActivity.class));
            }

            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    private void updateContentForTab(int tabId) {
        if (tabId == R.id.nav_hygiene) {
            updateContent(
                    "Hygiene and Safety Guides",
                    "Essential Hygiene Practices",
                    "Proper hygiene measures are crucial for preventing infections..."
            );
        } else if (tabId == R.id.nav_nutrition) {
            updateContent(
                    "Nutrition for Immunity",
                    "Boost Your Immune System",
                    "A balanced diet rich in vitamins and minerals..."
            );
        } else if (tabId == R.id.nav_mythbusters) {
            updateContent(
                    "Health Mythbusters",
                    "Separating Fact from Fiction",
                    "Common health myths debunked with scientific evidence..."
            );
        }
    }

    private void updateContent(String topBarTitle, String headline, String bodyText) {
        topTitle.setText(topBarTitle);
        title.setText(headline);
        body.setText(bodyText);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}