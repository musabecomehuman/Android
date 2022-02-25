    package ml.androdevs.TagTarget.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ml.androdevs.TagTarget.TodoDB.MyDatabase;
import ml.androdevs.TagTarget.R;
import ml.androdevs.TagTarget.Model.RecyclerViewAdapter;
import ml.androdevs.TagTarget.Model.Todo;
import ml.androdevs.TagTarget.CategoryDB.Category;
import ml.androdevs.TagTarget.CategoryDB.CategoryDao;
import ml.androdevs.TagTarget.CategoryDB.DbManager;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class MainActivity<x> extends AppCompatActivity implements RecyclerViewAdapter.ClickListener, AdapterView.OnItemSelectedListener {

    MyDatabase myDatabase;
    RecyclerView recyclerView;
    Spinner spinner;
    RecyclerViewAdapter recyclerViewAdapter;
    FloatingActionButton floatingActionButton;
    Dialog dialog;
    SearchView searchView;
    AdView adView;
    ImageButton btnDelete, deleteCat;

    ArrayList<Todo> todoArrayList = new ArrayList<>();
    ArrayList<Todo> listFull = new ArrayList<>(todoArrayList);
    public static ArrayList<Category> spinnerList = new ArrayList<>();

    public BaseAdapter categoryAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return spinnerList.size();
        }

        @Override
        public Object getItem(int position) {
            return spinnerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            categoryHolder holder;
            View categoryView = view;

            if (categoryView == null) {
                categoryView = getLayoutInflater().inflate(R.layout.row_category_spinner, viewGroup, false);

                holder = new categoryHolder();
                holder.tvCategoryName = categoryView.findViewById(R.id.tv_cat_name);
                categoryView.setTag(holder);
            } else {
                holder = (categoryHolder) categoryView.getTag();
            }

            Category category = spinnerList.get(position);
            holder.tvCategoryName.setText(category.getCategoryName());

            return categoryView;
        }

        class categoryHolder {
            private TextView tvCategoryName;
        }

    };

    public static final int NEW_TODO_REQUEST_CODE = 200;
    public static final int UPDATE_TODO_REQUEST_CODE = 300;
    private ImageView imgAnim1;
    private ImageView imgAnim2;

    public void openDialog() {
        dialog.setContentView(R.layout.add_category_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView add = dialog.findViewById(R.id.Add);
        EditText inCategory = dialog.findViewById(R.id.inCategory);
        Intent intent = new Intent(this, TodoNoteActivity.class);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String categoryNameInput = inCategory.getText().toString();

                if(categoryNameInput.length() > 24) {
                    Toast toast = Toast.makeText(getApplicationContext(), "too long title", Toast.LENGTH_SHORT);
                    toast.show();
                }

                else if (!TextUtils.isEmpty(categoryNameInput)) {

                    Category category = new Category();
                    category.setCategoryName(categoryNameInput);
                    category.set_id(spinner.getCount() + 1);
                    CategoryDao.insertRecord(category);

                    categoryAdapter.notifyDataSetChanged();

                    // Load spinnerList again
                    populateSpinnerList();

                }

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbManager.setConfig(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        // Add Sample Data
        populateSampleData();

        // Load all records from DB to the spinnerList
        populateSpinnerList();

        initViews();
        myDatabase = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, MyDatabase.DB_NAME).fallbackToDestructiveMigration().build();
        checkIfAppLaunchedFirstTime();
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(1);
        dialog = new Dialog(this);
        searchView = findViewById(R.id.searchView);
        adView = findViewById(R.id.adView);
        deleteCat = findViewById(R.id.deleteCat);
        btnDelete = findViewById(R.id.btnDelete);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, TodoNoteActivity.class), NEW_TODO_REQUEST_CODE);
            }
        });

        // initialise tha layout
        PulsatorLayout pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();

        pulsator.setCount(5);


        searchView.setIconifiedByDefault(true);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCat.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                searchView.setMaxWidth(1000000000);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (spinner.getSelectedItemPosition() != 1) {
                    deleteCat.setVisibility(View.VISIBLE);
                }
                spinner.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });

        deleteCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Category category = new Category();

                category.setCategoryName(spinner.getSelectedItem().toString());
                category.set_id((int) spinner.getSelectedItemId() + 1);

                CategoryDao.deleteRecord(category);

                spinner.setSelection(1);
                populateSpinnerList();
                categoryAdapter.notifyDataSetChanged();
            }
        });

    }

    public interface ClickListener {
        void launchIntent(int id);
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteRow(Todo todo) {
        new AsyncTask<Todo, Void, Integer>() {
            @Override
            protected Integer doInBackground(Todo... params) {
                return myDatabase.daoAccess().deleteTodo(params[0]);
            }

            @Override
            protected void onPostExecute(Integer number) {
                super.onPostExecute(number);

                Intent intent = getIntent();
                intent.putExtra("isDeleted", true).putExtra("number", number);
                setResult(RESULT_OK, intent);
                finish();
            }
        }.execute(todo);

    }

    private void populateSpinnerList() {

        spinnerList = new ArrayList<>();
        spinnerList = CategoryDao.loadAllRecords();

    }

    private void populateSampleData() {

        // Sample Data for Spinner

        if (CategoryDao.loadAllRecords().size() == 0) {

            Category category = new Category();

            category.setCategoryName("Add new category");
            CategoryDao.insertRecord(category);

            category.setCategoryName("All");
            CategoryDao.insertRecord(category);

            category.setCategoryName("iOS");
            CategoryDao.insertRecord(category);

            category.setCategoryName("Kotlin");
            CategoryDao.insertRecord(category);

            category.setCategoryName("Android");
            CategoryDao.insertRecord(category);

            categoryAdapter.notifyDataSetChanged();

        }
    }

    public void initViews() {
        floatingActionButton = findViewById(R.id.fab);
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(categoryAdapter);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this, this);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    protected void onResume() {
        adView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }

    @Override
    public void launchIntent(int id) {
        startActivityForResult(new Intent(MainActivity.this, TodoNoteActivity.class).putExtra("id", id), UPDATE_TODO_REQUEST_CODE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position == 0) {
            openDialog();
            spinner.setSelection(1);
            deleteCat.setVisibility(View.INVISIBLE);
        }
        else if (position == 1) {
            loadAllTodos();
            deleteCat.setVisibility(View.INVISIBLE);
        } else {
            String string = parent.getItemAtPosition(position).toString();
            loadFilteredTodos(string);
            deleteCat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @SuppressLint("StaticFieldLeak")
    private void loadFilteredTodos(String category) {
        new AsyncTask<String, Void, List<Todo>>() {
            @Override
            protected List<Todo> doInBackground(String... params) {
                return myDatabase.daoAccess().fetchTodoListByCategory(params[0]);

            }

            @Override
            protected void onPostExecute(List<Todo> todoList) {
                recyclerViewAdapter.updateTodoList(todoList);
            }
        }.execute(category);

    }


    @SuppressLint("StaticFieldLeak")
    private void fetchTodoByIdAndInsert(int id) {
        new AsyncTask<Integer, Void, Todo>() {
            @Override
            protected Todo doInBackground(Integer... params) {
                return myDatabase.daoAccess().fetchTodoListById(params[0]);

            }

            @Override
            protected void onPostExecute(Todo todoList) {
                recyclerViewAdapter.addRow(todoList);
            }
        }.execute(id);

    }

    @SuppressLint("StaticFieldLeak")
    private void loadAllTodos() {
        new AsyncTask<String, Void, List<Todo>>() {
            @Override
            protected List<Todo> doInBackground(String... params) {
                return myDatabase.daoAccess().fetchAllTodos();
            }

            @Override
            protected void onPostExecute(List<Todo> todoList) {
                recyclerViewAdapter.updateTodoList(todoList);
            }
        }.execute();
    }

    private void buildDummyTodos() {
        Todo todo = new Todo();
        todo.name = "Android Retrofit Tutorial";
        todo.category = "Android";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "iOS TableView Tutorial";
        todo.category = "iOS";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "Kotlin Arrays";
        todo.category = "Kotlin";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "Swift Arrays";
        todo.category = "Swift";

        todoArrayList.add(todo);
        insertList(todoArrayList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //reset spinners
            spinner.setSelection(1);

            if (requestCode == NEW_TODO_REQUEST_CODE) {
                long id = data.getLongExtra("id", -1);
                Toast.makeText(getApplicationContext(), "Row inserted", Toast.LENGTH_SHORT).show();
                fetchTodoByIdAndInsert((int) id);

            } else if (requestCode == UPDATE_TODO_REQUEST_CODE) {

                boolean isDeleted = data.getBooleanExtra("isDeleted", false);
                int number = data.getIntExtra("number", -1);
                if (isDeleted) {
                    Toast.makeText(getApplicationContext(), number + " rows deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), number + " rows updated", Toast.LENGTH_SHORT).show();
                }

                loadAllTodos();

            }


        } else {
            Toast.makeText(getApplicationContext(), "No action done by user", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void insertList(List<Todo> todoList) {
        new AsyncTask<List<Todo>, Void, Void>() {
            @Override
            protected Void doInBackground(List<Todo>... params) {
                myDatabase.daoAccess().insertTodoList(params[0]);

                return null;

            }

            @Override
            protected void onPostExecute(Void voids) {
                super.onPostExecute(voids);
            }
        }.execute(todoList);

    }

    private void checkIfAppLaunchedFirstTime() {
        final String PREFS_NAME = "SharedPrefs";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("firstTime", true)) {
            settings.edit().putBoolean("firstTime", false).apply();
            buildDummyTodos();

        }
    }
}