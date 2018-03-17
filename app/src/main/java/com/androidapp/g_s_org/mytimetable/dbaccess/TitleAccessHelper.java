package com.androidapp.g_s_org.mytimetable.dbaccess;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import com.androidapp.g_s_org.mytimetable.common.Common;


public class TitleAccessHelper extends SQLiteOpenHelper {
    // version of DB
    public static final int DATABASE_VERSION = 1;
    // name of DB
    public static final String TABLE_NAME = "titleDB";
    // string for outputting log
    private static final String TAG = "TitleAccessHelper";

    // constructor
    public TitleAccessHelper(Context con)
    {
        // if DB has not been created, new DB is created
        super(con, TABLE_NAME, null, 1);
    }

    // executed when DB is created
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //===
        //=== create table
        //===
        // SQL for creating DB
        String sqlToCreate = "create table " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tabId INTEGER not null," +
                "title TEXT)";
        // execute SQL
        db.execSQL(sqlToCreate);
        //===
        //=== put initial value
        //===
        // SQL for input initial value
        String sqlToInput = "insert into " + TABLE_NAME + "(tabId, title) values(0, '画面1')";
        db.execSQL(sqlToInput);
        sqlToInput = "insert into " + TABLE_NAME + "(tabId, title) values(1, '画面2')";
        db.execSQL(sqlToInput);
        sqlToInput = "insert into " + TABLE_NAME + "(tabId, title) values(2, '画面3')";
        db.execSQL(sqlToInput);

        /*
        // ContentValues to put initial value of title
        ContentValues[] cvs = new ContentValues[NUMOFTABS];
        for (int i = 0; i < NUMOFTABS; i++){
            cvs[i] = new ContentValues();
            cvs[i].put("tabId", i);
            cvs[i].put("title", "画面" + Integer.toString(i));
        }
        try{
            // get DB
            db = getWritableDatabase();
            // begin transaction
            db.beginTransaction();
            // insert values
            for (int i = 0; i < NUMOFTABS; i++){
                db.insert(TABLE_NAME, null, cvs[i]);
            }
            // commit
            db.setTransactionSuccessful();
        } catch (Exception e) {
            // output log
            Log.e("init_titleDB", e.toString());
        } finally {
            // end transaction
            db.endTransaction();
            // close DB
            db.close();
        }
        */
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
