package ph.edu.usc.ise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class DocumentDetailActivity extends AppCompatActivity {

    private HealthDocument document;
    private TextView titleView, dateView, categoryView, notesView;
    private Button deleteButton;
    private HealthLogViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        // Initialize views
        titleView = findViewById(R.id.detailTitle);
        dateView = findViewById(R.id.detailDate);
        categoryView = findViewById(R.id.detailCategory);
        notesView = findViewById(R.id.detailNotes);
        deleteButton = findViewById(R.id.deleteButton);

        viewModel = new ViewModelProvider(this).get(HealthLogViewModel.class);

        // Get passed document
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("document")) {
            document = (HealthDocument) intent.getSerializableExtra("document");

            titleView.setText(document.title);
            dateView.setText(document.date);
            categoryView.setText(document.category);
            notesView.setText(document.notes);
        }

        deleteButton.setOnClickListener(v -> {
            if (document != null) {
                HealthLogViewModel viewModel = new ViewModelProvider(this).get(HealthLogViewModel.class);
                viewModel.removeDocument(document);

                // Return to the previous activity and trigger refresh
                Intent returnIntent = new Intent();
                returnIntent.putExtra("deleted", true);
                setResult(RESULT_OK, returnIntent);
                finish(); // close this activity
            }
        });
    }
}

