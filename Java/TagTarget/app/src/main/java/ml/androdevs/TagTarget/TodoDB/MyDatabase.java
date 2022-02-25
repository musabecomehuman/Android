package ml.androdevs.TagTarget.TodoDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ml.androdevs.TagTarget.Model.Todo;

@Database(entities = {Todo.class}, version = 4, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    public static final String DB_NAME = "app_db";
    public static final String TABLE_NAME_TODO = "todo";


    public abstract DaoAccess daoAccess();

}

