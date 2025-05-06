package ph.edu.usc.ise;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {HealthDocument.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HealthDocumentDao healthDocumentDao();
}
