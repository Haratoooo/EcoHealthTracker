package ph.edu.usc.ise;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class AllExposuresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exposures);

        // Initialize views
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.exposuresRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data - replace with your actual data
        List<ExposureItem> exposures = new ArrayList<>();
        exposures.add(new ExposureItem("Air pollution", "Near Main Street", "3h ago", "High PM2.5 levels"));
        exposures.add(new ExposureItem("Chemical smell", "Industrial area", "2d ago", "Strong ammonia odor"));
        exposures.add(new ExposureItem("Waste burning", "Barangay 123", "5d ago", "Plastic waste being burned"));

        ExposureAdapter adapter = new ExposureAdapter(exposures);
        recyclerView.setAdapter(adapter);

        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_symptoms);
        bottomNav.setOnItemSelectedListener(this::onBottomNavigationItemSelected);
    }

    private boolean onBottomNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    // Exposure Item class
    private static class ExposureItem {
        String type;
        String location;
        String time;
        String notes;

        ExposureItem(String type, String location, String time, String notes) {
            this.type = type;
            this.location = location;
            this.time = time;
            this.notes = notes;
        }
    }

    // Exposure Adapter
    private class ExposureAdapter extends RecyclerView.Adapter<ExposureAdapter.ViewHolder> {

        private final List<ExposureItem> exposures;

        ExposureAdapter(List<ExposureItem> exposures) {
            this.exposures = exposures;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_exposure, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ExposureItem item = exposures.get(position);
            holder.typeText.setText(item.type);
            holder.locationText.setText(item.location);
            holder.timeText.setText(item.time);
            holder.notesText.setText(item.notes);
        }

        @Override
        public int getItemCount() {
            return exposures.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView typeText, locationText, timeText, notesText;

            ViewHolder(View itemView) {
                super(itemView);
                typeText = itemView.findViewById(R.id.exposureType);
                locationText = itemView.findViewById(R.id.exposureLocation);
                timeText = itemView.findViewById(R.id.exposureTime);
                notesText = itemView.findViewById(R.id.exposureNotes);
            }
        }
    }
}