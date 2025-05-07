package ph.edu.usc.ise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

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

        titleView = findViewById(R.id.detailTitle);
        dateView = findViewById(R.id.detailDate);
        categoryView = findViewById(R.id.detailCategory);
        notesView = findViewById(R.id.detailNotes);
        deleteButton = findViewById(R.id.deleteButton);
        documentPreview = findViewById(R.id.documentPreview);
        openFileButton = findViewById(R.id.openFileButton);

        viewModel = new ViewModelProvider(this).get(HealthLogViewModel.class);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("document")) {
            document = (HealthDocument) intent.getSerializableExtra("document");

            titleView.setText(document.title);
            dateView.setText(document.date);
            categoryView.setText(document.category);
            notesView.setText(document.notes);

            Uri fileUri = Uri.parse(document.filePath);
            String type = getContentResolver().getType(fileUri);

            if (type != null && type.startsWith("image/")) {
                // Show image directly
                documentPreview.setImageURI(fileUri);
                documentPreview.setVisibility(ImageView.VISIBLE);
            } else {
                // Show open file button
                openFileButton.setVisibility(Button.VISIBLE);
                openFileButton.setOnClickListener(v -> {
                    Intent openIntent = new Intent(Intent.ACTION_VIEW);
                    openIntent.setData(fileUri);
                    openIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(openIntent);
                });
            }
        }

        deleteButton.setOnClickListener(v -> {
            if (document != null) {
                viewModel.removeDocument(document);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("deleted", true);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
