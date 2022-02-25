package ml.androdevs.TagTarget.TodoDB;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import ml.androdevs.TagTarget.Model.Todo;

@SuppressWarnings({"unchecked", "deprecation"})
public final class DaoAccess_Impl implements DaoAccess {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Todo> __insertionAdapterOfTodo;

  private final EntityDeletionOrUpdateAdapter<Todo> __deletionAdapterOfTodo;

  private final EntityDeletionOrUpdateAdapter<Todo> __updateAdapterOfTodo;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTaskFromId;

  public DaoAccess_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTodo = new EntityInsertionAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `todo` (`todo_id`,`time`,`name`,`description`,`category`,`day`,`month`,`dayOfWeek`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.todo_id);
        if (value.time == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.time);
        }
        if (value.name == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.name);
        }
        if (value.description == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.description);
        }
        if (value.category == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.category);
        }
        if (value.day == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.day);
        }
        if (value.month == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.month);
        }
        if (value.dayOfWeek == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.dayOfWeek);
        }
      }
    };
    this.__deletionAdapterOfTodo = new EntityDeletionOrUpdateAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `todo` WHERE `todo_id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.todo_id);
      }
    };
    this.__updateAdapterOfTodo = new EntityDeletionOrUpdateAdapter<Todo>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `todo` SET `todo_id` = ?,`time` = ?,`name` = ?,`description` = ?,`category` = ?,`day` = ?,`month` = ?,`dayOfWeek` = ? WHERE `todo_id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Todo value) {
        stmt.bindLong(1, value.todo_id);
        if (value.time == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.time);
        }
        if (value.name == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.name);
        }
        if (value.description == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.description);
        }
        if (value.category == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.category);
        }
        if (value.day == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.day);
        }
        if (value.month == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.month);
        }
        if (value.dayOfWeek == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.dayOfWeek);
        }
        stmt.bindLong(9, value.todo_id);
      }
    };
    this.__preparedStmtOfDeleteTaskFromId = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM todo WHERE todo_id = ?";
        return _query;
      }
    };
  }

  @Override
  public long insertTodo(final Todo todo) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfTodo.insertAndReturnId(todo);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertTodoList(final List<Todo> todoList) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTodo.insert(todoList);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int deleteTodo(final Todo todo) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total +=__deletionAdapterOfTodo.handle(todo);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int updateTodo(final Todo todo) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total +=__updateAdapterOfTodo.handle(todo);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteTaskFromId(final int todoId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTaskFromId.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, todoId);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteTaskFromId.release(_stmt);
    }
  }

  @Override
  public List<Todo> fetchAllTodos() {
    final String _sql = "SELECT * FROM todo";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfTodoId = CursorUtil.getColumnIndexOrThrow(_cursor, "todo_id");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
      final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
      final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
      final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Todo _item;
        _item = new Todo();
        _item.todo_id = _cursor.getInt(_cursorIndexOfTodoId);
        _item.time = _cursor.getString(_cursorIndexOfTime);
        _item.name = _cursor.getString(_cursorIndexOfName);
        _item.description = _cursor.getString(_cursorIndexOfDescription);
        _item.category = _cursor.getString(_cursorIndexOfCategory);
        _item.day = _cursor.getString(_cursorIndexOfDay);
        _item.month = _cursor.getString(_cursorIndexOfMonth);
        _item.dayOfWeek = _cursor.getString(_cursorIndexOfDayOfWeek);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Todo> fetchTodoListByCategory(final String category) {
    final String _sql = "SELECT * FROM todo WHERE category = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (category == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, category);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfTodoId = CursorUtil.getColumnIndexOrThrow(_cursor, "todo_id");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
      final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
      final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
      final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Todo _item;
        _item = new Todo();
        _item.todo_id = _cursor.getInt(_cursorIndexOfTodoId);
        _item.time = _cursor.getString(_cursorIndexOfTime);
        _item.name = _cursor.getString(_cursorIndexOfName);
        _item.description = _cursor.getString(_cursorIndexOfDescription);
        _item.category = _cursor.getString(_cursorIndexOfCategory);
        _item.day = _cursor.getString(_cursorIndexOfDay);
        _item.month = _cursor.getString(_cursorIndexOfMonth);
        _item.dayOfWeek = _cursor.getString(_cursorIndexOfDayOfWeek);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Todo fetchTodoListById(final int todoId) {
    final String _sql = "SELECT * FROM todo WHERE todo_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, todoId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfTodoId = CursorUtil.getColumnIndexOrThrow(_cursor, "todo_id");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
      final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
      final int _cursorIndexOfDayOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "dayOfWeek");
      final Todo _result;
      if(_cursor.moveToFirst()) {
        _result = new Todo();
        _result.todo_id = _cursor.getInt(_cursorIndexOfTodoId);
        _result.time = _cursor.getString(_cursorIndexOfTime);
        _result.name = _cursor.getString(_cursorIndexOfName);
        _result.description = _cursor.getString(_cursorIndexOfDescription);
        _result.category = _cursor.getString(_cursorIndexOfCategory);
        _result.day = _cursor.getString(_cursorIndexOfDay);
        _result.month = _cursor.getString(_cursorIndexOfMonth);
        _result.dayOfWeek = _cursor.getString(_cursorIndexOfDayOfWeek);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
