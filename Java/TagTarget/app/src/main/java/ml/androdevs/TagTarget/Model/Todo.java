package ml.androdevs.TagTarget.Model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import ml.androdevs.TagTarget.TodoDB.MyDatabase;

@Entity(tableName = MyDatabase.TABLE_NAME_TODO)
public class Todo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int todo_id;

    public String time;

    public String name;

    public String description;

    public String category;

    public String day;

    public String month;

    public String dayOfWeek;

    @Ignore
    public String priority;

    public String getName() {
        return name;
    }
}
