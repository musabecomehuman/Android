package ml.androdevs.TagTarget.CategoryDB;

import android.provider.BaseColumns;

public class DbSchema {
    private static final String TAG = "DbSchema";

    public static final String DATABASE_NAME = "dbExpense.db";
    public static final int DATABASE_VERSION = 7;
    public static final String SORT_ASC = " ASC";
    public static final String SORT_DESC = " DESC";
    public static final String[] ORDERS = {SORT_ASC,SORT_DESC};
    public static final int OFF = 0;
    public static final int ON = 1;

    public static final class Table_Category implements BaseColumns  { 
        // Table Name
        public static final String TABLE_NAME = "Category";

        // Table Columns
        public static final String COL__ID = "_id";
        public static final String COL_CATEGORYNAME = "CategoryName";

        // Create Table Statement
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Category ( " + 
            COL__ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " + 
            COL_CATEGORYNAME + " TEXT NOT NULL );";

        // Drop table statement
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS Category;";

        // Columns list array
        public static final String[] allColumns = {
            COL__ID,
            COL_CATEGORYNAME };
    }

}
