package ph.edu.usc.ise;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HealthLogActivity extends AppCompatActivity {

    Button btnChooseImage, btnUploadFile, btnSaveDocument;
    EditText editTitle, editDate, editNotes;
    Spinner spinnerCategory;
    RecyclerView recyclerDocuments;
    private static final int REQ_GALLERY = 2, REQ_FILE = 3;
    private Uri fileUri;
    private HealthLogViewModel viewModel;

    // ImageView to preview uploaded files
    private ImageView imagePreview;
    private TextView fileNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Use your XML layout file name

        // Bottom Navigation Bar Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_health_log); // Set initial selected item
        bottomNavigationView.setOnItemSelectedListener(this::onBottomNavigationItemSelected);

        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUploadFile = findViewById(R.id.btnUploadFile);
        btnSaveDocument = findViewById(R.id.btnSaveDocument);


        editTitle = findViewById(R.id.editTitle);
        editDate = findViewById(R.id.editDate);
        editNotes = findViewById(R.id.editNotes);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        recyclerDocuments = findViewById(R.id.recyclerDocuments);
        recyclerDocuments.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HealthLogViewModel.class);

        // ImageView and TextView for file preview
        imagePreview = findViewById(R.id.imagePreview);
        fileNameText = findViewById(R.id.fileNameText);

        // Observe LiveData to update RecyclerView when documents change
        viewModel.getDocuments().observe(this, documents -> {
            refreshList(documents); // Update RecyclerView when data changes
        });

        // Setup category spinner
        setupCategorySpinner();

        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQ_GALLERY);
        });

        btnUploadFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQ_FILE);
        });

        btnSaveDocument.setOnClickListener(v -> {
            if (fileUri == null) {
                return; // If no file is selected, return
            }

            // Create new document
            HealthDocument doc = new HealthDocument();
            doc.title = editTitle.getText().toString();
            doc.date = editDate.getText().toString();
            doc.category = spinnerCategory.getSelectedItem().toString();
            doc.notes = editNotes.getText().toString();
            doc.filePath = fileUri.toString();

            // Add document to ViewModel and refresh list
            viewModel.addDocument(doc);
            refreshList(viewModel.getDocuments().getValue()); // Update the list after adding document

            // Clear inputs
            editTitle.setText("");
            editDate.setText("");
            editNotes.setText("");
            spinnerCategory.setSelection(0); // Reset to default selection after saving
            fileUri = null; // Reset for the next doc
            imagePreview.setVisibility(View.GONE);  // Hide image preview
            fileNameText.setVisibility(View.GONE); // Hide file name text
            Toast.makeText(this, "Document saved!", Toast.LENGTH_SHORT).show();
        });

        Button btnViewAll = findViewById(R.id.btnViewAllDocuments);
        btnViewAll.setOnClickListener(v -> {
            startActivity(new Intent(this, AllDocumentsActivity.class));
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


    // Setup spinner and load the selected category
    private void setupCategorySpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.document_categories));

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    // Handle the result from gallery or file picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQ_GALLERY || requestCode == REQ_FILE) {
                Uri uri = data.getData();
                if (uri != null) {
                    fileUri = uri;
                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );

                    if (requestCode == REQ_GALLERY) {
                        imagePreview.setVisibility(View.VISIBLE);
                        Glide.with(this).load(uri).into(imagePreview);
                        fileNameText.setVisibility(View.GONE);
                    } else {
                        imagePreview.setVisibility(View.GONE);
                        fileNameText.setVisibility(View.VISIBLE);
                        fileNameText.setText(uri.getLastPathSegment());
                    }
                }
            } else if (requestCode == 100) {
                boolean wasDeleted = data.getBooleanExtra("deleted", false);
                if (wasDeleted) {
                    // Just refresh using current LiveData value
                    List<HealthDocument> updatedDocs = viewModel.getDocuments().getValue();
                    if (updatedDocs != null) {
                        refreshList(updatedDocs);
                    }
                }
            }
        }
    }

    // Refresh RecyclerView with the updated list
    private void refreshList(List<HealthDocument> documents) {
        DocumentAdapter adapter = new DocumentAdapter(documents, this, document -> {
            Intent intent = new Intent(this, DocumentDetailActivity.class);
            intent.putExtra("document", document);
            startActivityForResult(intent, 100); // use a request code
        });
        recyclerDocuments.setAdapter(adapter);
    }
}


