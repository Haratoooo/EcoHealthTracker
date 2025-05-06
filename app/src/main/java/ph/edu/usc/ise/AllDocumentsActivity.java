package ph.edu.usc.ise;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllDocumentsActivity extends AppCompatActivity {

    private RecyclerView recyclerAllDocuments;
    private HealthLogViewModel viewModel;
    private DocumentAdapter adapter;
    private Spinner spinnerSortCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_documents);

        // Bind views
        recyclerAllDocuments = findViewById(R.id.recyclerAllDocuments);
        spinnerSortCategory = findViewById(R.id.spinnerSortCategory);
        recyclerAllDocuments.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HealthLogViewModel.class);

        // Setup RecyclerView adapter
        adapter = new DocumentAdapter(new ArrayList<>(), this, document -> {
            // Handle document click, open it or show details
        });

        recyclerAllDocuments.setAdapter(adapter);

        // Observe changes to the document list
        viewModel.getDocuments().observe(this, new Observer<List<HealthDocument>>() {
            @Override
            public void onChanged(List<HealthDocument> documents) {
                updateCategoryFilter(documents);  // Update spinner based on documents
                updateRecyclerView(documents);    // Initial population of the list
            }
        });

        // Handle Spinner selection for filtering by category
        spinnerSortCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = spinnerSortCategory.getSelectedItem().toString();
                filterDocumentsByCategory(selectedCategory);  // Filter documents based on selected category
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                filterDocumentsByCategory("");  // If nothing selected, show all documents
            }
        });
    }

    // Update the spinner with unique categories from documents
    private void updateCategoryFilter(List<HealthDocument> documents) {
        Set<String> categories = new HashSet<>();
        for (HealthDocument doc : documents) {
            categories.add(doc.category);
        }
        categories.add(""); // To allow showing all documents

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(categories));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortCategory.setAdapter(categoryAdapter);
    }

    // Filter documents by selected category
    private void filterDocumentsByCategory(String category) {
        List<HealthDocument> allDocuments = viewModel.getDocuments().getValue();
        List<HealthDocument> filteredDocuments = new ArrayList<>();

        if (allDocuments != null) {
            for (HealthDocument doc : allDocuments) {
                if (category.isEmpty() || doc.category.equals(category)) {
                    filteredDocuments.add(doc);
                }
            }
        }
        updateRecyclerView(filteredDocuments);  // Update RecyclerView with filtered documents
    }

    // Update the RecyclerView with new list of documents
    private void updateRecyclerView(List<HealthDocument> documents) {
        adapter.updateList(documents);
    }
}
