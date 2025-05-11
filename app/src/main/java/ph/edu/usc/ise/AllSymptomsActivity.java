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

public class AllSymptomsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_symptoms);

        // Initialize views
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.symptomsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data - replace with your actual data
        List<SymptomItem> symptoms = new ArrayList<>();
        symptoms.add(new SymptomItem("Headache", "Moderate", "2h ago", "Had persistent headache since morning"));
        symptoms.add(new SymptomItem("Fatigue", "Mild", "1d ago", "Feeling tired after work"));
        symptoms.add(new SymptomItem("Cough", "Severe", "3d ago", "Dry cough with chest pain"));

        SymptomAdapter adapter = new SymptomAdapter(symptoms);
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

    // Symptom Item class
    private static class SymptomItem {
        String name;
        String severity;
        String time;
        String notes;

        SymptomItem(String name, String severity, String time, String notes) {
            this.name = name;
            this.severity = severity;
            this.time = time;
            this.notes = notes;
        }
    }

    // Symptom Adapter
    private class SymptomAdapter extends RecyclerView.Adapter<SymptomAdapter.ViewHolder> {

        private final List<SymptomItem> symptoms;

        SymptomAdapter(List<SymptomItem> symptoms) {
            this.symptoms = symptoms;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_symptom, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SymptomItem item = symptoms.get(position);
            holder.nameText.setText(item.name);
            holder.severityText.setText(item.severity);
            holder.timeText.setText(item.time);
            holder.notesText.setText(item.notes);

            // Set color based on severity
            int color;
            switch (item.severity.toLowerCase()) {
                case "severe":
                    color = getResources().getColor(R.color.severity_high);
                    break;
                case "moderate":
                    color = getResources().getColor(R.color.severity_medium);
                    break;
                default:
                    color = getResources().getColor(R.color.severity_low);
            }
            holder.severityText.setTextColor(color);
        }

        @Override
        public int getItemCount() {
            return symptoms.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, severityText, timeText, notesText;

            ViewHolder(View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.symptomName);
                severityText = itemView.findViewById(R.id.symptomSeverity);
                timeText = itemView.findViewById(R.id.symptomTime);
                notesText = itemView.findViewById(R.id.symptomNotes);
            }
        }
    }
}