package com.ahryk94gmail.mifood.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ahryk94gmail.mifood.Debug;
import com.ahryk94gmail.mifood.model.ActivityData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityDbHelper extends SQLiteOpenHelper {

    private static ActivityDbHelper instance;

    public synchronized static ActivityDbHelper getInstance(Context context) {
        if (instance == null)
            instance = new ActivityDbHelper(context);

        return instance;
    }

    private static final boolean DBG = true;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "ActivityDb";

    private static final String DB_ACTIVITY_TABLE = "ACTIVITY";
    private static final String DB_ID_KEY = "ID";
    private static final String DB_TIMESTAMP_KEY = "TIMESTAMP";
    private static final String DB_PROVIDER_KEY = "PROVIDER";
    private static final String DB_INTENSITY_KEY = "INTENSITY";
    private static final String DB_STEPS_KEY = "STEPS";
    private static final String DB_TYPE_KEY = "TYPE";

    public ActivityDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Debug.WriteLog(DBG, "ActivityDbHelper: onCreate");

        String query = "CREATE TABLE " + DB_ACTIVITY_TABLE + " (" +
                DB_ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DB_TIMESTAMP_KEY + " INTEGER," +
                DB_PROVIDER_KEY + " INTEGER," +
                DB_INTENSITY_KEY + " INTEGER," +
                DB_STEPS_KEY + " INTEGER," +
                DB_TYPE_KEY + " INTEGER);";

        db.execSQL(query);
    }

    public void InsertActivityData(ActivityData activityData) {
        ContentValues cv = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();

        cv.put(DB_TIMESTAMP_KEY, activityData.getTimestamp());
        cv.put(DB_PROVIDER_KEY, activityData.getProvider());
        cv.put(DB_INTENSITY_KEY, activityData.getIntensity());
        cv.put(DB_STEPS_KEY, activityData.getSteps());
        cv.put(DB_TYPE_KEY, activityData.getType());

        db.insert(DB_ACTIVITY_TABLE, null, cv);

        db.close();
    }

    private List<ActivityData> SelectActivityData(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<ActivityData> list = new ArrayList<>();

        Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);

        if (c.moveToFirst()) {
            int timestampColIndex = c.getColumnIndex(DB_TIMESTAMP_KEY);
            int providerColIndex = c.getColumnIndex(DB_PROVIDER_KEY);
            int intensityColIndex = c.getColumnIndex(DB_INTENSITY_KEY);
            int stepsColIndex = c.getColumnIndex(DB_STEPS_KEY);
            int typeColIndex = c.getColumnIndex(DB_TYPE_KEY);

            do {
                ActivityData item = new ActivityData(
                        c.getInt(timestampColIndex),
                        c.getInt(providerColIndex),
                        c.getInt(intensityColIndex),
                        c.getInt(stepsColIndex),
                        c.getInt(typeColIndex));
                list.add(item);
            } while (c.moveToNext());

            c.close();
        }

        db.close();

        return list;
    }

    public List<ActivityData> SelectActivityData(int timestampFrom, int timestampTo) {
        String selectionArgs[] = {
                String.valueOf(ActivityData.TYPE_DEEP_SLEEP),
                String.valueOf(ActivityData.TYPE_LIGHT_SLEEP),
                String.valueOf(ActivityData.TYPE_ACTIVITY),
                String.valueOf(timestampFrom),
                String.valueOf(timestampTo)
        };
        String selection = DB_TYPE_KEY + " IN (?, ?, ?) AND " + DB_TIMESTAMP_KEY + " BETWEEN ? AND ?";

        return SelectActivityData(DB_ACTIVITY_TABLE, null, selection, selectionArgs, null, null, DB_TIMESTAMP_KEY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
