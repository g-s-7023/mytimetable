package com.androidapp.g_s_org.mytimetable;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

/**
 * Created by nao on 2018/03/11.
 */

public class StationsOfLineAccessHelper extends SQLiteOpenHelper {
    // version of DB
    public static final int DATABASE_VERSION = 1;
    // name of DB
    public static final String TABLE_NAME = "stationsOfLineDB";
    // string for outputting log
    private static final String TAG = "StaOfLineAccessHelper";

    // constructor
    public StationsOfLineAccessHelper(Context con)
    {
        // if DB has not been created, new DB is created
        super(con, TABLE_NAME, null, 1);
    }

    // executed when DB is created
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // SQL for creating DB
        String sql = "create table " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "line TEXT not null," +
                "stationName TEXT not null," +
                "stationNameForQuery TEXT not null";
        // execute SQL
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion)
    {
        // output log
        Log.d(TAG, Common.LOG_DBUPGRADE);
        try
        {
            // delete old DB
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            // create new DB
            onCreate(db);
        }
        catch(Exception e)
        {
            Log.e("ERROR", e.toString());
        }
    }
}
