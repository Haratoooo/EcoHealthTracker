package ph.edu.usc.ise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private boolean isSpinnerUpdating = false;  // Flag to prevent loop during spinner updates
    private String lastSelectedCategory = "All";
    private List<HealthDocument> allDocuments = new ArrayList<>();// Track the last selected category

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
            // Start detail activity and pass the clicked document
            Intent intent = new Intent(AllDocumentsActivity.this, DocumentDetailActivity.class);
            intent.putExtra("document", document);
            startActivityForResult(intent, 200);
        });

        recyclerAllDocuments.setAdapter(adapter);

        // Observe changes to the document list
        viewModel.getDocuments().observe(this, new Observer<List<HealthDocument>>() {
            @Override
            public void onChanged(List<HealthDocument> documents) {
                Log.d("Documents", "Documents updated: " + documents.size());
                allDocuments.clear();
                allDocuments.addAll(documents); // Save the full list once
                updateCategoryFilter(documents); // Update spinner
                filterDocumentsByCategory(lastSelectedCategory);     // Initial population of the list
            }
        });

        // Handle Spinner selection for filtering by category
        spinnerSortCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Prevent triggering the listener when the spinner is updated programmatically
                if (isSpinnerUpdating) {
                    return;
                }
                String selectedCategory = spinnerSortCategory.getSelectedItem().toString();
                if (!selectedCategory.equals(lastSelectedCategory)) {
                    lastSelectedCategory = selectedCategory;
                    Log.d("CategorySelection", "Selected Category: " + selectedCategory);  // Log selected category
                    filterDocumentsByCategory(selectedCategory);  // Filter documents based on selected category
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d("CategorySelection", "No category selected, showing all documents");
                filterDocumentsByCategory("All");  // If nothing selected, show all documents
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            boolean wasDeleted = data.getBooleanExtra("deleted", false);
            if (wasDeleted) {
                // No need to re-observe LiveData — just use the current value to refresh
                List<HealthDocument> updatedDocs = viewModel.getDocuments().getValue();
                if (updatedDocs != null) {
                    allDocuments.clear();
                    allDocuments.addAll(updatedDocs);
                    updateCategoryFilter(updatedDocs);
                    filterDocumentsByCategory(lastSelectedCategory);
                }
            }
        }
    }

    // Update the spinner with unique categories from documents
    private void updateCategoryFilter(List<HealthDocument> documents) {
        Set<String> categories = new HashSet<>();
        categories.add("All");  // Ensure "All" category is always the first one
        for (HealthDocument doc : documents) {
            categories.add(doc.category);
        }

        // Sort the categories (optional)
        List<String> sortedCategories = new ArrayList<>(categories);
        sortedCategories.sort(String::compareTo);  // Optional sorting

        // Set the spinner adapter
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sortedCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortCategory.setAdapter(categoryAdapter);

        // Ensure the spinner retains the current selection or defaults to "All"
        if (!isSpinnerUpdating) {
            isSpinnerUpdating = true;  // Set flag to true to avoid triggering the spinner listener
            int position = categoryAdapter.getPosition(lastSelectedCategory); // Select the last category
            spinnerSortCategory.setSelection(position);  // Set the spinner to the last selected category
            isSpinnerUpdating = false;  // Reset the flag after updating the spinner
        }
    }

    // Filter documents by selected category
    private void filterDocumentsByCategory(String category) {
        List<HealthDocument> filteredDocuments = new ArrayList<>();

        Log.d("Filtering", "Filtering documents by category: " + category);

        if (allDocuments != null) {
            if ("All".equalsIgnoreCase(category)) {
                filteredDocuments.addAll(allDocuments);
                Log.d("Filtering", "Showing all documents");
            } else {
                for (HealthDocument doc : allDocuments) {
                    String docCategory = doc.getCategory() != null ? doc.getCategory().trim() : "";
                    if (category != null && category.trim().equalsIgnoreCase(docCategory)) {
                        filteredDocuments.add(doc);
                        Log.d("Filtering", "Document matches category: " + docCategory);
                    }
                }
            }
        }

        Log.d("Filtering", "Filtered " + filteredDocuments.size() + " documents");
        updateRecyclerView(filteredDocuments);
    }

    // Update the RecyclerView with new list of documents
    private void updateRecyclerView(List<HealthDocument> filteredDocuments) {
        if (adapter != null) {
            adapter.updateList(filteredDocuments);  // Update RecyclerView adapter with filtered documents
        }
    }
}
