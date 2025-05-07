package ph.edu.usc.ise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DocumentDetailActivity extends AppCompatActivity {

    private HealthDocument document;
    private TextView titleView, dateView, categoryView, notesView;
    private Button deleteButton, openFileButton, btnback;
    private ImageView documentPreview;
    private HealthLogViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        // Bind UI components
        titleView = findViewById(R.id.detailTitle);
        dateView = findViewById(R.id.detailDate);
        categoryView = findViewById(R.id.detailCategory);
        notesView = findViewById(R.id.detailNotes);
        deleteButton = findViewById(R.id.deleteButton);
        openFileButton = findViewById(R.id.openFileButton);
        documentPreview = findViewById(R.id.documentPreview);
        btnback = findViewById(R.id.btnback);


        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HealthLogViewModel.class);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_health_log); // Set initial selected item
        bottomNavigationView.setOnItemSelectedListener(this::onBottomNavigationItemSelected);

        // Load document from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("document")) {
            document = (HealthDocument) intent.getSerializableExtra("document");
        }

        if (document == null) {
            Toast.makeText(this, "No document found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set document details
        titleView.setText(document.title);
        dateView.setText(document.date);
        categoryView.setText(document.category);
        notesView.setText(document.notes);

        Uri fileUri = Uri.parse(document.filePath);
        String mimeType = getContentResolver().getType(fileUri);

        if (mimeType != null && mimeType.startsWith("image/")) {
            documentPreview.setVisibility(ImageView.VISIBLE);
            Glide.with(this).load(fileUri).into(documentPreview);
            openFileButton.setVisibility(Button.GONE);
        } else {
            documentPreview.setVisibility(ImageView.GONE);
            openFileButton.setVisibility(Button.VISIBLE);

            openFileButton.setOnClickListener(v -> {
                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                openIntent.setDataAndType(fileUri, mimeType != null ? mimeType : "*/*");
                openIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(openIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Handle delete
        deleteButton.setOnClickListener(v -> {
            viewModel.removeDocument(document);  // ensure correct ViewModel method is used

            Intent returnIntent = new Intent();
            returnIntent.putExtra("deleted", true);
            setResult(RESULT_OK, returnIntent);
            finish();
        });


    }
    private boolean onBottomNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_health_log) {
            // Already in Health Log Activity, do nothing
            return true;
        } else if (item.getItemId() == R.id.nav_dashboard) {
            startActivity(new Intent(this, DashboardActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_tips) {
            startActivity(new Intent(this, HealthTipsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_symptoms) {
            startActivity(new Intent(this, SymptomsTrackerActivity.class));
            return true;
        } else {
            return false;
        }
    }
}
