package ml.androdevs.TagTarget.Model;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ml.androdevs.TagTarget.Activities.MainActivity;
import ml.androdevs.TagTarget.R;
import ml.androdevs.TagTarget.TodoDB.DatabaseClient;
import ml.androdevs.TagTarget.TodoDB.MyDatabase;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    private MainActivity context;
    List<Todo> todoList;
    List<Todo> listFull;
    private RecyclerViewAdapter.ClickListener clickListener;
    MyDatabase myDatabase;

    public RecyclerViewAdapter(MainActivity context, ClickListener clickListener) {
        this.clickListener = clickListener;
        todoList = new ArrayList<>();
        this.context = context;
    }

    private void deleteTaskFromId(int taskId, int position) {
        class GetSavedTasks extends AsyncTask<Void, Void, List<Todo>> {
            @Override
            protected List<Todo> doInBackground(Void... voids) {
                DatabaseClient.getInstance(context)
                        .getAppDatabase()
                        .daoAccess()
                        .deleteTaskFromId(taskId);

                return todoList;
            }

            @Override
            protected void onPostExecute(List<Todo> tasks) {
                super.onPostExecute(tasks);
                removeAtPosition(position);
            }
        }
        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    private void removeAtPosition(int position) {
        todoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, todoList.size());
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_layout, parent, false);
        RecyclerViewAdapter.ViewHolder viewHolder = new RecyclerViewAdapter.ViewHolder(view);
        listFull = new ArrayList<>(todoList);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.txtName.setText(todo.name);
        holder.txtCategory.setText(todo.category);
        holder.txtTime.setText(todo.time);
        holder.txtDay.setText(todo.day);
        holder.txtMonth.setText(todo.month);
        holder.txtDayOfWeek.setText(todo.dayOfWeek);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTaskFromId(todo.todo_id, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void updateTodoList(List<Todo> data) {
        todoList.clear();
        todoList.addAll(data);
        notifyDataSetChanged();
    }

    public void filterList(ArrayList<Todo> filteredList) {
        todoList = filteredList;
        notifyDataSetChanged();
    }

    public void addRow(Todo data) {
        todoList.add(data);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return FilterTodo;
    }

     Filter FilterTodo = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Todo> tempList = new ArrayList<>();
            if (charSequence.toString().length() == 0 || charSequence.toString().isEmpty()) {
                tempList.addAll(listFull);
            } else {
                for (Todo item:listFull) {
                    if (item.getName().toString().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        tempList.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = tempList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            todoList.clear();
            todoList.addAll((ArrayList<Todo>) filterResults.values);
            notifyDataSetChanged();


        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public TextView txtCategory;
        public TextView txtTime;
        public TextView txtDay;
        public TextView txtMonth;
        public TextView txtDayOfWeek;
        public CardView cardView;
        public ImageButton btnDelete;

        public ViewHolder(View view) {
            super(view);

            txtTime = view.findViewById(R.id.time);
            txtName = view.findViewById(R.id.txtName);
            txtCategory = view.findViewById(R.id.txtCategory);
            txtDay = view.findViewById(R.id.date);
            txtMonth = view.findViewById(R.id.month);
            txtDayOfWeek = view.findViewById(R.id.day);
            btnDelete = view.findViewById(R.id.btnDelete);
            cardView = view.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.launchIntent(todoList.get(getAdapterPosition()).todo_id);
                }
            });

        }
    }

    public interface ClickListener {
        void launchIntent(int id);
    }
}