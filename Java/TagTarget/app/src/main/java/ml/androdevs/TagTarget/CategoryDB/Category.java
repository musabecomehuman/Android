package ml.androdevs.TagTarget.CategoryDB;

import android.os.Bundle;

public class Category {

    public static final String COL__ID = "_id";
    public static final String COL_CATEGORYNAME = "CategoryName";

    private Integer m_id;
    private String mCategoryName;

    public Category() {
    }

    public Category(Integer _id, String CategoryName) {
        this.m_id = _id;
        this.mCategoryName = CategoryName;
    }

    public Integer get_id() {
        return m_id;
    }

    public void set_id(Integer _id) {
        this.m_id = _id;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String CategoryName) {
        this.mCategoryName = CategoryName;
    }


    public Bundle toBundle() { 
        Bundle b = new Bundle();
        b.putInt(COL__ID, this.m_id);
        b.putString(COL_CATEGORYNAME, this.mCategoryName);
        return b;
    }

    public Category(Bundle b) {
        if (b != null) {
            this.m_id = b.getInt(COL__ID);
            this.mCategoryName = b.getString(COL_CATEGORYNAME);
        }
    }

    @Override
    public String toString() {
        return mCategoryName;
    }


}
