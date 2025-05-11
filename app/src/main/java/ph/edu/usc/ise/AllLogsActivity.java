package ph.edu.usc.ise;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class AllLogsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_logs);

        // Temporary sample data - replace with your actual data source
        List<String> sampleLogs = new ArrayList<>();
        sampleLogs.add("2023-11-15: Respiratory (Severity: 7) - Air pollution");
        sampleLogs.add("2023-11-14: Headache (Severity: 5) - Chemical exposure");
        sampleLogs.add("2023-11-13: Fatigue (Severity: 4) - Work stress");

        ListView logsListView = findViewById(R.id.logsListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                sampleLogs
        );
        logsListView.setAdapter(adapter);

        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_symptoms);
        bottomNav.setOnItemSelectedListener(this::onBottomNavigationItemSelected);

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

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