package com.app.madym.datatracker.ugh.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.madym.datatracker.TimerItem;

public class DataTrackerDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DataTracker.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataTrackerContract.TimerEntry.TABLE_NAME + " (" +
                    DataTrackerContract.TimerEntry._ID + " INTEGER PRIMARY KEY," +
                    DataTrackerContract.TimerEntry.COLUMN_NAME_ITEM + TEXT_TYPE + COMMA_SEP +
                    DataTrackerContract.TimerEntry.COLUMN_NAME_START + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataTrackerContract.TimerEntry.TABLE_NAME;

    public DataTrackerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // fuck everything up
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addTimerEntry(TimerItem.Entry entry, TimerItem timer) {
        if (timer == null || entry == null) {
            // Can't do anything
            Log.w(TAG, "addTimerEntry passed a null entry!");
        }

        SQLiteDatabase db = getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataTrackerContract.TimerEntry.COLUMN_NAME_ITEM, timer.getItemName());
        // TODO (!!!) entry.first and .second are date and time amount, should be start and end
        // Make sure to update in TimerItem.Entry, also should probably make those names not
        // the worst
        values.put(DataTrackerContract.TimerEntry.COLUMN_NAME_START, entry.first);
        values.put(DataTrackerContract.TimerEntry.COLUMN_NAME_END, entry.second);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DataTrackerContract.TimerEntry.TABLE_NAME, null, values);

        // TODO update totals table
    }

    public TimerItem getTimerEntry(String item) {
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DataTrackerContract.TimerEntry._ID,
                DataTrackerContract.TimerEntry.COLUMN_NAME_ITEM,
                DataTrackerContract.TimerEntry.COLUMN_NAME_START,
                DataTrackerContract.TimerEntry.COLUMN_NAME_END
        };

        String selection = DataTrackerContract.TimerEntry.COLUMN_NAME_ITEM + " = ?";
        String[] selectionArgs = { item };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                DataTrackerContract.TimerEntry.COLUMN_NAME_START + " DESC";

        Cursor c = db.query(
                DataTrackerContract.TimerEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        TimerItem entry = createFromCursor(c);
        db.close(); // TODO -- I have to do this right?
        return entry;
    }

    public int updateTimerEntry(String item, String start, TimerItem.Entry entry) {
        SQLiteDatabase db = getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DataTrackerContract.TimerEntry.COLUMN_NAME_ITEM, item);
        values.put(DataTrackerContract.TimerEntry.COLUMN_NAME_START, entry.first);
        values.put(DataTrackerContract.TimerEntry.COLUMN_NAME_START, entry.second);

        // Which row to update, based on the title
        String selection = DataTrackerContract.TimerEntry.COLUMN_NAME_ITEM + " LIKE ? AND "
                + DataTrackerContract.TimerEntry.COLUMN_NAME_START + " LIKE ?";
        String[] selectionArgs = { item, start };

        int count = db.update(
                DataTrackerContract.TimerEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        return count;
    }

    public void deleteTimerItem(String item) {
        // Define 'where' part of query.
        String selection = DataTrackerContract.TimerEntry.COLUMN_NAME_ITEM + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { item };
        // Issue SQL statement.
        getWritableDatabase().delete(DataTrackerContract.TimerEntry.TABLE_NAME, selection,
                selectionArgs);
    }

    public void deleteTimerEntry(String item, String start) {
        // Define 'where' part of query.
        String selection = DataTrackerContract.TimerEntry.COLUMN_NAME_ITEM + " LIKE ? AND "
                + DataTrackerContract.TimerEntry.COLUMN_NAME_START + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { item, start };
        // Issue SQL statement.
        getWritableDatabase().delete(DataTrackerContract.TimerEntry.TABLE_NAME, selection,
                selectionArgs);

    }

    private TimerItem createFromCursor(Cursor c) {
        // TODO ????????
        c.moveToFirst();
        long itemId = c.getLong(
                c.getColumnIndexOrThrow(DataTrackerContract.TimerEntry._ID)
        );
        c.close();
        return null;
    }
}