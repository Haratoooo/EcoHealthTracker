package ph.edu.usc.ise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

public class DocumentDetailActivity extends AppCompatActivity {

    private HealthDocument document;
    private TextView titleView, dateView, categoryView, notesView;
    private Button deleteButton, openFileButton;
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

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HealthLogViewModel.class);

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

        // Handle delete
        deleteButton.setOnClickListener(v -> {
            viewModel.removeDocument(document);  // ensure correct ViewModel method is used

            Intent returnIntent = new Intent();
            returnIntent.putExtra("deleted", true);
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }
}
