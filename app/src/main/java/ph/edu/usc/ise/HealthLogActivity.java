package ph.edu.usc.ise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUploadFile = findViewById(R.id.btnUploadFile);
        btnSaveDocument = findViewById(R.id.btnSaveDocument);

        editTitle = findViewById(R.id.editTitle);
        editDate = findViewById(R.id.editDate);
        editNotes = findViewById(R.id.editNotes);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        recyclerDocuments = findViewById(R.id.recyclerDocuments);
        recyclerDocuments.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel here so it's available throughout the Activity
        viewModel = new ViewModelProvider(this).get(HealthLogViewModel.class);

        // Observe LiveData to update the RecyclerView when documents change
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
                // You can add a Toast here if you'd like
                return;
            }

            HealthDocument doc = new HealthDocument();
            doc.title = editTitle.getText().toString();
            doc.date = editDate.getText().toString();
            doc.category = spinnerCategory.getSelectedItem().toString();
            doc.notes = editNotes.getText().toString();
            doc.filePath = fileUri.toString();

            viewModel.addDocument(doc);
            refreshList(viewModel.getDocuments().getValue()); // Update the list after adding document

            // Clear inputs
            editTitle.setText("");
            editDate.setText("");
            editNotes.setText("");
            spinnerCategory.setSelection(0); // Reset to default selection after saving
            fileUri = null; // Reset for the next doc
            Toast.makeText(this, "Document saved!", Toast.LENGTH_SHORT).show();
        });

        Button btnViewAll = findViewById(R.id.btnViewAllDocuments);
        btnViewAll.setOnClickListener(v -> {
            startActivity(new Intent(this, AllDocumentsActivity.class));
        });
    }

    // Setup spinner and load the selected category
    // Setup spinner and load the selected category
    private void setupCategorySpinner() {
        // Create adapter using custom layout
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.document_categories));

        // Use custom layout for drop-down items
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Get selected category from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String selectedCategory = preferences.getString("selected_category", "All");

        // Set the selected category in the spinner
        int position = categoryAdapter.getPosition(selectedCategory);

        // If the saved category isn't valid (e.g., "All" wasn't selected or is missing), default to "All"
        if (position == -1) {
            position = categoryAdapter.getPosition("All");
        }

        // Set the spinner selection (this will not trigger onItemSelected)
        spinnerCategory.setSelection(position, false);

        // Listener for when the spinner item is selected
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = parentView.getItemAtPosition(position).toString();
                filterByCategory(selectedCategory); // Apply filter when category is changed

                // Save the selected category to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selected_category", selectedCategory);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optionally handle case where nothing is selected
            }
        });
    }


    // Handle the result from gallery or file picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            boolean wasDeleted = data.getBooleanExtra("deleted", false);
            if (wasDeleted) {
                // Simply observe LiveData — it should auto-update the list
                viewModel.getDocuments().observe(this, this::refreshList);
            }
        }

        // For file pickers
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                fileUri = uri;
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
            }
        }
    }

    // Filter documents based on the selected category
    private void filterByCategory(String selectedCategory) {
        List<HealthDocument> allDocs = viewModel.getDocuments().getValue();
        if (allDocs == null) return;

        if ("All".equals(selectedCategory)) {
            refreshList(allDocs); // Show all documents
            return;
        }

        List<HealthDocument> filteredDocs = new ArrayList<>();
        for (HealthDocument doc : allDocs) {
            if (doc.category.equals(selectedCategory)) {
                filteredDocs.add(doc);
            }
        }

        refreshList(filteredDocs); // Show only filtered documents
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
