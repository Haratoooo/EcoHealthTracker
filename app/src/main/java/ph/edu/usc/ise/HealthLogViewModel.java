package ph.edu.usc.ise;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import androidx.room.Room;

import java.util.List;

public class HealthLogViewModel extends AndroidViewModel {
    private final MutableLiveData<List<HealthDocument>> documentListLiveData = new MutableLiveData<>();
    private final AppDatabase db;

    public HealthLogViewModel(Application application) {
        super(application);
        db = Room.databaseBuilder(application, AppDatabase.class, "health_document_database").build();
    }

    public LiveData<List<HealthDocument>> getDocuments() {
        // Query the database on a background thread and update LiveData
        new Thread(() -> {
            List<HealthDocument> allDocs = db.healthDocumentDao().getAll();
            documentListLiveData.postValue(allDocs); // Post the result to LiveData
        }).start();
        return documentListLiveData;
    }

    public void addDocument(HealthDocument doc) {
        new Thread(() -> {
            db.healthDocumentDao().insert(doc);
            // After insertion, fetch the updated list from the database
            List<HealthDocument> updatedDocs = db.healthDocumentDao().getAll();
            documentListLiveData.postValue(updatedDocs); // Update the LiveData with the new list
        }).start();
    }

    public void removeDocument(HealthDocument doc) {
        new Thread(() -> {
            db.healthDocumentDao().delete(doc);
            // After deletion, fetch the updated list from the database
            List<HealthDocument> updatedDocs = db.healthDocumentDao().getAll();
            documentListLiveData.postValue(updatedDocs); // Update the LiveData with the new list
        }).start();
    }
}
