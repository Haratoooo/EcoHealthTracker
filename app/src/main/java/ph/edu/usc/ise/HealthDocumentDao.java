package ph.edu.usc.ise;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HealthDocumentDao {
    @Insert
    void insert(HealthDocument doc);

    @Query("SELECT * FROM HealthDocument ORDER BY id DESC")
    List<HealthDocument> getAll();

    @Delete
    void delete(HealthDocument doc);
}
