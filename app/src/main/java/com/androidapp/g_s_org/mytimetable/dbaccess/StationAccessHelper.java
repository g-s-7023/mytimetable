package com.androidapp.g_s_org.mytimetable.dbaccess;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import com.androidapp.g_s_org.mytimetable.common.Common;

/**
 * Created by nao on 2018/03/04.
 */

public class StationAccessHelper extends SQLiteOpenHelper {
    // version of DB
    public static final int DATABASE_VERSION = 3;
    // name of DB
    public static final String DB_NAME = "stationDB";
    // name of TABLE
    public static final String OLDTABLE = "stationDB";
    public static final String TABLE_STATION = "table_station";
    public static final String TABLE_STATIONOFLINE = "table_stationOfLine";
    private static final String TAG = "StationAccessHelper";

    // constructor
    public StationAccessHelper(Context con)
    {
        // if DB has not been created, new DB is created
        super(con, DB_NAME, null, DATABASE_VERSION);
    }

    // executed when DB is created
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // SQL for creating DB
        String sqlStation = "create table " + TABLE_STATION + " (" +
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
        db.execSQL(sqlStation);
        // SQL for creating DB
        String sqlStationsOfLine = "create table " + TABLE_STATIONOFLINE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "stationId INTEGER not null," +
                "stationName TEXT not null," +
                "stationNameForQuery TEXT not null)";
        db.execSQL(sqlStationsOfLine);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion)
    {
        // output log
        Log.d(TAG, Common.LOG_DBUPGRADE);
        try
        {
            // delete old DB
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATION);
            // create new DB
            onCreate(db);
        }
        catch(Exception e)
        {
            Log.e("ERROR", e.toString());
        }
    }
}
