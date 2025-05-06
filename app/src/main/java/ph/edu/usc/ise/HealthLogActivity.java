package ph.edu.usc.ise;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
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
            spinnerCategory.setSelection(0);
            fileUri = null; // Reset for the next doc
            Toast.makeText(this, "Document saved!", Toast.LENGTH_SHORT).show();
        });

        Button btnViewAll = findViewById(R.id.btnViewAllDocuments);
        btnViewAll.setOnClickListener(v -> {
            startActivity(new Intent(this, AllDocumentsActivity.class));
        });
    }

    // Handle the result from gallery or file picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                fileUri = uri;
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
                // You can load the image or preview it if needed
            }
        }
    }

    private void refreshList(List<HealthDocument> documents) {
        DocumentAdapter adapter = new DocumentAdapter(documents, this, document -> {
            viewModel.removeDocument(document); // Pass the actual HealthDocument object
            refreshList(viewModel.getDocuments().getValue()); // Refresh the list after removal
        });
        recyclerDocuments.setAdapter(adapter);
    }
}
