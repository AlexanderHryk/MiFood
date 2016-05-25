package com.ahryk94gmail.mifood.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ahryk94gmail.mifood.Debug;
import com.ahryk94gmail.mifood.model.HrMeasurementInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HrMeasurementHistoryDbHelper extends SQLiteOpenHelper {

    private static HrMeasurementHistoryDbHelper instance;

    public synchronized static HrMeasurementHistoryDbHelper getInstance(Context context) {
        if (instance == null)
            instance = new HrMeasurementHistoryDbHelper(context);

        return instance;
    }

    private static final boolean DBG = true;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "HrMeasurementHistoryDb";

    private static final String DB_HR_MEASUREMENT_HISTORY_TABLE = "HR_MEASUREMENT_HISTORY";
    private static final String DB_ID_KEY = "ID";
    private static final String DB_HEART_RATE_KEY = "HEART_RATE";
    private static final String DB_DATE_TIME_KEY = "DATE_TIME";
    private static final String DB_HEART_RATE_STATE_KEY = "HEART_RATE_STATE";

    private static final int HR_STATE_NORMAL = 1;
    private static final int HR_STATE_ABNORMAL = 0;

    public HrMeasurementHistoryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Debug.WriteLog(DBG, "HrMeasurementHistoryDbHelper: onCreate");

        String query = "CREATE TABLE " + DB_HR_MEASUREMENT_HISTORY_TABLE + " (" +
                DB_ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DB_HEART_RATE_KEY + " INTEGER," +
                DB_DATE_TIME_KEY + " INTEGER," +
                DB_HEART_RATE_STATE_KEY + " INTEGER);";

        db.execSQL(query);
    }

    public void InsertHrMeasurementInfo(HrMeasurementInfo info) {
        ContentValues cv = new ContentValues();

        SQLiteDatabase db = this.getWritableDatabase();

        cv.put(DB_HEART_RATE_KEY, info.getHeartRate());
        cv.put(DB_DATE_TIME_KEY, info.getDateTime().getTime());
        cv.put(DB_HEART_RATE_STATE_KEY, info.isHeartRateStateNormal() ? HR_STATE_NORMAL : HR_STATE_ABNORMAL);

        db.insert(DB_HR_MEASUREMENT_HISTORY_TABLE, null, cv);

        db.close();
    }

    private List<HrMeasurementInfo> SelectHrMeasurementInfo(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<HrMeasurementInfo> list = new ArrayList<>();

        Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);

        if (c.moveToFirst()) {
            int heartRateColIndex = c.getColumnIndex(DB_HEART_RATE_KEY);
            int dateTimeColIndex = c.getColumnIndex(DB_DATE_TIME_KEY);
            int heartRateStateColIndex = c.getColumnIndex(DB_HEART_RATE_STATE_KEY);

            do {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(c.getLong(dateTimeColIndex));

                HrMeasurementInfo item = new HrMeasurementInfo(
                        c.getInt(heartRateColIndex),
                        calendar.getTime(),
                        c.getInt(heartRateStateColIndex) == HR_STATE_NORMAL);
                list.add(item);
            } while (c.moveToNext());

            c.close();
        }

        db.close();

        return list;
    }

    public List<HrMeasurementInfo> SelectHrMeasurementInfo(int limit) {
        List<HrMeasurementInfo> list =  SelectHrMeasurementInfo(DB_HR_MEASUREMENT_HISTORY_TABLE, null, null, null, null, null,
                DB_DATE_TIME_KEY + " DESC LIMIT " + String.valueOf(limit));
        Collections.sort(list, new Comparator<HrMeasurementInfo>() {
            @Override
            public int compare(HrMeasurementInfo lhs, HrMeasurementInfo rhs) {
                if (lhs.getDateTime().getTime() < rhs.getDateTime().getTime()) {
                    return -1;
                }
                return 1;
            }
        });

        return list;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
