package com.androidapp.g_s_org.mytimetable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.SparseIntArray;

import static com.androidapp.g_s_org.mytimetable.Common.ARG_POSITION;
import static com.androidapp.g_s_org.mytimetable.Common.ARG_SECTION_NUMBER;


// dialog asking if delete a station row or not
public class DeleteDialogFragment extends DialogFragment {
    // tabId of the entry to delete
    private int mTabIdToDelete;
    // rowId of the entry to delete
    private int mRowIdToDelete;

    public DeleteDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            mTabIdToDelete = args.getInt(ARG_SECTION_NUMBER);
            mRowIdToDelete = args.getInt(ARG_POSITION);
        }
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.title_delete)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // delete the row and update the following rows' rowId
                        // activity which call this
                        Activity caller = getActivity();
                        // create AccessHelper
                        StationAccessHelper helper = new StationAccessHelper(caller);
                        // SQLiteDatabase
                        SQLiteDatabase db = null;
                        // Cursor
                        Cursor cursor = null;
                        // value to update
                        ContentValues cv = new ContentValues();
                        // list of id and rowId pair to decrement
                        SparseIntArray idsToDecrement = new SparseIntArray();
                        // ID of a row
                        int id;
                        // register to DB
                        try {
                            //===
                            //=== read
                            //===
                            // read db and get rows whose rowId is larger than rowId
                            db = helper.getReadableDatabase();
                            // begin transaction
                            db.beginTransaction();
                            // get data from DB
                            cursor = db.query(
                                    StationAccessHelper.TABLE_NAME,
                                    new String[]{"id", "rowId"},
                                    "tabId=? AND rowId>?",
                                    new String[]{Integer.toString(mTabIdToDelete), Integer.toString(mRowIdToDelete)},
                                    null, null, "rowId ASC"
                            );
                            while (cursor.moveToNext()) {
                                idsToDecrement.append(
                                        cursor.getInt(cursor.getColumnIndex("id")),
                                        cursor.getInt(cursor.getColumnIndex("rowId"))
                                );
                            }
                            //===
                            //=== delete and update
                            //===
                            // get db to white
                            db = helper.getWritableDatabase();
                            // delete the row
                            db.delete(
                                    StationAccessHelper.TABLE_NAME,
                                    "tabId=? and rowId=?",
                                    new String[]{Integer.toString(mTabIdToDelete), Integer.toString(mRowIdToDelete)}
                            );
                            // update the rowId of each row whose rowId is bigger than that of the deleted row
                            for (int index = 0; index < idsToDecrement.size(); index++) {
                                id = idsToDecrement.keyAt(index);
                                cv.put("rowId", idsToDecrement.get(id) - 1);
                                db.update(StationAccessHelper.TABLE_NAME, cv, "id = ?", new String[]{Integer.toString(id)});
                            }
                            // commit
                            db.setTransactionSuccessful();
                        } catch (Exception e) {
                            // output log
                            Log.e("AlertDialog.onClick", e.toString());
                        } finally {
                            // end transaction
                            db.endTransaction();
                            // close DB
                            db.close();
                            // callback
                            if (caller instanceof MainActivity) {
                                ((MainActivity) caller).onDeleteSucceed(mTabIdToDelete);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    public interface DeleteSucceedCallback {
        public void onDeleteSucceed(int sectionNumber);
    }
}
