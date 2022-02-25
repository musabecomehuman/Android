package ml.androdevs.TagTarget.Activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;

import ml.androdevs.TagTarget.Model.AlarmReciever;
import ml.androdevs.TagTarget.TodoDB.MyDatabase;
import ml.androdevs.TagTarget.R;
import ml.androdevs.TagTarget.Model.Todo;
import ml.androdevs.TagTarget.databinding.ActivityTodoNoteBinding;
import ml.androdevs.TagTarget.CategoryDB.Category;
import ml.androdevs.TagTarget.CategoryDB.CategoryDao;

import static ml.androdevs.TagTarget.Activities.MainActivity.spinnerList;

public class TodoNoteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private ActivityTodoNoteBinding binding;
    private MaterialStyledDatePickerDialog datePicker;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    Spinner spinner;
    EditText inTitle, inTime, inDate;
    Button btnDone, btnDelete;
    ImageButton deleteCat;
    Dialog dialog;
    TextView btnAdd;
    int day;
    int dayOfWeek1;
    int monthInt;
    String month;
    String dayOfWeek;
    String day1;
    boolean isNewTodo = false;

    MyDatabase myDatabase;

    Todo updateTodo;

    public BaseAdapter categoryAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return spinnerList.size();
        }

        @Override
        public Object getItem(int i /*position*/) {
            return spinnerList.get(i);
        }

        @Override
        public long getItemId(int i /*position*/) {
            return i;
        }

        @Override
        public View getView(int i /*position*/, View view, ViewGroup viewGroup) {

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

            Category category = spinnerList.get(i);
            holder.tvCategoryName.setText(category.getCategoryName());

            return categoryView;
        }

        class categoryHolder {
            private TextView tvCategoryName;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityTodoNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        binding.inTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTimePicker();

            }
        });

        binding.inDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDatePicker();

            }
        });

        inDate = findViewById(R.id.inDate);
        inTime = findViewById(R.id.inTime);
        spinner = findViewById(R.id.spinner);
        inTitle = findViewById(R.id.inTitle);
        btnDone = findViewById(R.id.btnDone);
        deleteCat = findViewById(R.id.deleteCat1);
        btnAdd = findViewById(R.id.Add);
        dialog = new Dialog(this);
        spinner.setAdapter(categoryAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(1);

        myDatabase = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, MyDatabase.DB_NAME).build();

        int todo_id = getIntent().getIntExtra("id", -100);

        if (todo_id == -100)
            isNewTodo = true;

        if (!isNewTodo) {
            fetchTodoById(todo_id);
        }

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

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewTodo) {
                    Todo todo = new Todo();
                    todo.name = inTitle.getText().toString();
                    todo.category = spinner.getSelectedItem().toString();
                    todo.time = inTime.getText().toString();
                    todo.day = day1;
                    todo.month = month;
                    todo.dayOfWeek = dayOfWeek;

                    insertRow(todo);

                    if(todo.time.isEmpty() == true) {

                    }

                    if (todo.time.isEmpty() == false) {
                        setAlarm();
                    }

                    if (todo.dayOfWeek == null) {
                        dayOfWeek1 = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        if (dayOfWeek1 == 1) {todo.dayOfWeek = "Sun";}
                        if (dayOfWeek1 == 2) {todo.dayOfWeek = "Mon";}
                        if (dayOfWeek1 == 3) {todo.dayOfWeek = "Tue";}
                        if (dayOfWeek1 == 4) {todo.dayOfWeek = "Wed";}
                        if (dayOfWeek1 == 5) {todo.dayOfWeek = "Thu";}
                        if (dayOfWeek1 == 6) {todo.dayOfWeek = "Fri";}
                        if (dayOfWeek1 == 7) {todo.dayOfWeek = "Sat";}
                    }

                    if (todo.month == null) {
                        monthInt = Calendar.getInstance().get(Calendar.MONTH);
                        if (monthInt == 0) {todo.month = "Jan";}
                        if (monthInt == 1) {todo.month = "Feb";}
                        if (monthInt == 2) {todo.month = "Mar";}
                        if (monthInt == 3) {todo.month = "Apr";}
                        if (monthInt == 4) {todo.month = "May";}
                        if (monthInt == 5) {todo.month = "Jun";}
                        if (monthInt == 6) {todo.month = "Jul";}
                        if (monthInt == 7) {todo.month = "Aug";}
                        if (monthInt == 8) {todo.month = "Sep";}
                        if (monthInt == 9) {todo.month = "Oct";}
                        if (monthInt == 10) {todo.month = "Nov";}
                        if (monthInt == 11) {todo.month = "Dec";}
                    }

                    if (todo.day == null) {
                        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                        todo.day = String.valueOf(day);
                    }

                } else {

                    updateTodo.name = inTitle.getText().toString();
                    updateTodo.category = spinner.getSelectedItem().toString();
                    updateTodo.time = inTime.getText().toString();
                    updateTodo.day = day1;
                    updateTodo.month = month;
                    updateTodo.dayOfWeek = dayOfWeek;

                    updateRow(updateTodo);

                    if (updateTodo.dayOfWeek == null) {
                        dayOfWeek1 = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        if (dayOfWeek1 == 1) {updateTodo.dayOfWeek = "Sun";}
                        if (dayOfWeek1 == 2) {updateTodo.dayOfWeek = "Mon";}
                        if (dayOfWeek1 == 3) {updateTodo.dayOfWeek = "Tue";}
                        if (dayOfWeek1 == 4) {updateTodo.dayOfWeek = "Wed";}
                        if (dayOfWeek1 == 5) {updateTodo.dayOfWeek = "Thu";}
                        if (dayOfWeek1 == 6) {updateTodo.dayOfWeek = "Fri";}
                        if (dayOfWeek1 == 7) {updateTodo.dayOfWeek = "Sat";}
                    }

                    if (updateTodo.month == null) {
                        monthInt = Calendar.getInstance().get(Calendar.MONTH);
                        if (monthInt == 0) {updateTodo.month = "Jan";}
                        if (monthInt == 1) {updateTodo.month = "Feb";}
                        if (monthInt == 2) {updateTodo.month = "Mar";}
                        if (monthInt == 3) {updateTodo.month = "Apr";}
                        if (monthInt == 4) {updateTodo.month = "May";}
                        if (monthInt == 5) {updateTodo.month = "Jun";}
                        if (monthInt == 6) {updateTodo.month = "Jul";}
                        if (monthInt == 7) {updateTodo.month = "Aug";}
                        if (monthInt == 8) {updateTodo.month = "Sep";}
                        if (monthInt == 9) {updateTodo.month = "Oct";}
                        if (monthInt == 10) {updateTodo.month = "Nov";}
                        if (monthInt == 11) {updateTodo.month = "Dec";}
                    }

                    if (updateTodo.day == null) {
                        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                        updateTodo.day = String.valueOf(day);
                    }

                    if (updateTodo.time.isEmpty() == false) {
                        setAlarm();
                    }

                }
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    private void deleteRow(Todo todo) {
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

                    // Load spinnerList again
                    populateSpinnerList();

                }

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void populateSpinnerList() {

        spinnerList = new ArrayList<>();
        spinnerList = CategoryDao.loadAllRecords();

    }

    private void showTimePicker() {

        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(00)
                .setMinute(00)
                .setTitleText("Select Alarm Time")
                .build();

        setTheme(R.style.Dialog_Theme);

        picker.show(getSupportFragmentManager(), "ToDoReminder");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.inTime.setText(
                        String.format(picker.getHour() + " : " + picker.getMinute())
                );

                calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                calendar.set(Calendar.MONTH, monthInt);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfWeek1);
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

            }

        });

    }

    private void showDatePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        setTheme(R.style.Dialog_Theme);

        dayOfWeek1 = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        datePickerDialog.show();

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if (i1 == 0) {i1 = 1;}
        else if (i1 == 1) {i1 = 2;}
        else if (i1 == 2) {i1 = 3;}
        else if (i1 == 3) {i1 = 4;}
        else if (i1 == 4) {i1 = 5;}
        else if (i1 == 5) {i1 = 6;}
        else if (i1 == 6) {i1 = 7;}
        else if (i1 == 7) {i1 = 8;}
        else if (i1 == 8) {i1 = 9;}
        else if (i1 == 9) {i1 = 10;}
        else if (i1 == 10) {i1 = 11;}
        else if (i1 == 11) {i1 = 12;}
        String date = i2 + "-" + i1 + "-" + i;
        day = i2;
        binding.inDate.setText(date);
        monthInt = i1;
        if (i1 == 1) {month = "Jan";}
        if (i1 == 2) {month = "Feb";}
        if (i1 == 3) {month = "Mar";}
        if (i1 == 4) {month = "Apr";}
        if (i1 == 5) {month = "May";}
        if (i1 == 6) {month = "Jun";}
        if (i1 == 7) {month = "Jul";}
        if (i1 == 8) {month = "Aug";}
        if (i1 == 9) {month = "Sep";}
        if (i1 == 10) {month = "Oct";}
        if (i1 == 11) {month = "Nov";}
        if (i1 == 12) {month = "Dec";}

        if (dayOfWeek1 == 1) {dayOfWeek = "Sun";}
        if (dayOfWeek1 == 2) {dayOfWeek = "Mon";}
        if (dayOfWeek1 == 3) {dayOfWeek = "Tue";}
        if (dayOfWeek1 == 4) {dayOfWeek = "Wed";}
        if (dayOfWeek1 == 5) {dayOfWeek = "Thu";}
        if (dayOfWeek1 == 6) {dayOfWeek = "Fri";}
        if (dayOfWeek1 == 7) {dayOfWeek = "Sat";}

        if (i2 == 0) {day1 = "";}
        else {day1 = String.valueOf(day);}

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ToDoReminder";
            String description = "Notification channel for ToDoList";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ToDoReminer", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReciever.class);
        intent.putExtra("title", inTitle.getText().toString());

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }

    @SuppressLint("StaticFieldLeak")
    private void fetchTodoById(final int todo_id) {
        new AsyncTask<Integer, Void, Todo>() {
            @Override
            protected Todo doInBackground(Integer... params) {

                return myDatabase.daoAccess().fetchTodoListById(params[0]);

            }

            @Override
            protected void onPostExecute(Todo todo) {
                super.onPostExecute(todo);
                inTitle.setText(todo.name);
                spinner.setSelection(spinnerList.indexOf(todo.category));

                updateTodo = todo;
            }
        }.execute(todo_id);

    }

    @SuppressLint("StaticFieldLeak")
    private void insertRow(Todo todo) {
        new AsyncTask<Todo, Void, Long>() {
            @Override
            protected Long doInBackground(Todo... params) {
                return myDatabase.daoAccess().insertTodo(params[0]);
            }

            @Override
            protected void onPostExecute(Long id) {
                super.onPostExecute(id);

                Intent intent = getIntent();
                intent.putExtra("isNew", true).putExtra("id", id);
                setResult(RESULT_OK, intent);
                finish();
            }
        }.execute(todo);

    }


    @SuppressLint("StaticFieldLeak")
    private void updateRow(Todo todo) {
        new AsyncTask<Todo, Void, Integer>() {
            @Override
            protected Integer doInBackground(Todo... params) {
                return myDatabase.daoAccess().updateTodo(params[0]);
            }

            @Override
            protected void onPostExecute(Integer number) {
                super.onPostExecute(number);

                Intent intent = getIntent();
                intent.putExtra("isNew", false).putExtra("number", number);
                setResult(RESULT_OK, intent);
                finish();
            }
        }.execute(todo);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position == 0) {
            openDialog();
            spinner.setSelection(1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
