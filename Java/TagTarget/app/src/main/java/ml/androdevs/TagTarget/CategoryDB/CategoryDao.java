package ml.androdevs.TagTarget.CategoryDB;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class CategoryDao extends DbManager {
    private static final String TAG = "CategoryDao";

    protected static SQLiteDatabase database;
    protected static DbManager mDbManager;
    protected static  String[] allColumns = DbSchema.Table_Category.allColumns;


    protected CategoryDao() {
    }

    protected static void database_open() throws SQLException {
        mDbManager = DbManager.getsInstance();
        database = mDbManager.getDatabase();
    }

    protected static void database_close() {
        mDbManager = DbManager.getsInstance();
        mDbManager.close();
    }

    public static Category loadRecordById(int m_id)  { 
        database_open();
        Cursor cursor = database.query(DbSchema.Table_Category.TABLE_NAME,allColumns,  "_id = ?" , new String[] { String.valueOf(m_id) } , null, null, null,null);

        if (cursor != null)
            cursor.moveToFirst();

        Category category = new Category();
        category = cursorToCategory(cursor);

        cursor.close();
        database_close();

        return category;
    }

    public static ArrayList<Category> loadAllRecords() {
        ArrayList<Category> categoryList = new ArrayList<Category>();
        database_open();

        Cursor cursor = database.query(
                DbSchema.Table_Category.TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category category = cursorToCategory(cursor);
            categoryList.add(category);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return categoryList;
    }

    // Please always use the typed column names (Table_Category) when passing arguments.
    // Example: Table_Category.Column_Name
    public static ArrayList<Category> loadAllRecords(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        ArrayList<Category> categoryList = new ArrayList<Category>();
        database_open();

        if(TextUtils.isEmpty(selection)){
            selection = null;
            selectionArgs = null;
        }

        Cursor cursor = database.query(
                DbSchema.Table_Category.TABLE_NAME,
                allColumns,
                selection==null ? null : selection,
                selectionArgs==null ? null : selectionArgs,
                groupBy==null ? null : groupBy,
                having==null ? null : having,
                orderBy==null ? null : orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category category = cursorToCategory(cursor);
            categoryList.add(category);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return categoryList;
    }

    public static long insertRecord(Category category) {
        ContentValues values = new ContentValues();
        values = getCategoryValues(category);
        database_open();
        long insertId = database.insert(DbSchema.Table_Category.TABLE_NAME , null, values);
        database_close();
        return insertId;
    }

    public static int updateRecord(Category category) { 
        ContentValues values = new ContentValues();
        values = getCategoryValues(category);
        database_open();
        String[] where = new String[] { String.valueOf(category.get_id()) }; 
        int updatedId = database.update(DbSchema.Table_Category.TABLE_NAME , values, DbSchema.Table_Category.COL__ID + " = ? ",where );
        database_close();
        return updatedId;
    }

    public static int deleteRecord(Category category) { 
        database_open();
        String[] where = new String[] { String.valueOf(category.get_id()) }; 
        int deletedCount = database.delete(DbSchema.Table_Category.TABLE_NAME , DbSchema.Table_Category.COL__ID + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteRecord(String id) {
        database_open();
        String[] where = new String[] { id }; 
        int deletedCount = database.delete(DbSchema.Table_Category.TABLE_NAME , DbSchema.Table_Category.COL__ID + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteAllRecords() {
        database_open();
        int deletedCount = database.delete(DbSchema.Table_Category.TABLE_NAME , null, null );
        database_close();
        return deletedCount;
    }

    protected static ContentValues getCategoryValues(Category category) {
        ContentValues values = new ContentValues();

        values.put(DbSchema.Table_Category.COL__ID, category.get_id());
        values.put(DbSchema.Table_Category.COL_CATEGORYNAME, category.getCategoryName());

        return values;
    }

    protected static Category cursorToCategory(Cursor cursor)  {
        Category category = new Category();

        category.set_id(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Category.COL__ID)));
        category.setCategoryName(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Category.COL_CATEGORYNAME)));

        return category;
    }

    

}

