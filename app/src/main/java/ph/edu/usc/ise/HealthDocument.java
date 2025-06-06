package ph.edu.usc.ise;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class HealthDocument implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String date;
    public String category;
    public String notes;
    public String filePath;
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }// Local path to image/PDF
}
