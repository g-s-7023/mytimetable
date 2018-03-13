package com.androidapp.g_s_org.mytimetable;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

/**
 * Created by nao on 2018/03/04.
 */

public class StationAccessHelper extends SQLiteOpenHelper {
    // version of DB
    public static final int DATABASE_VERSION = 1;
    // name of DB
    public static final String TABLE_NAME = "stationDB";
    // string for outputting log
    private static final String TAG = "StationAccessHelper";

    // constructor
    public StationAccessHelper(Context con)
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
                "tabId INTEGER not null," +
                "rowId INTEGER not null," +
                "operator TEXT," +
                "line TEXT," +
                "lineForQuery TEXT," +
                "stationName TEXT," +
                "stationNameForQuery TEXT," +
                "direction TEXT," +
                "directionForQuery TEXT)";
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
