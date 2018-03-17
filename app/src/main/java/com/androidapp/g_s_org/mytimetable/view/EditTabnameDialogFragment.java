package com.androidapp.g_s_org.mytimetable.view;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.ContentValues;
        import android.content.DialogInterface;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.support.v4.app.DialogFragment;
        import android.util.Log;
        import android.widget.EditText;

        import com.androidapp.g_s_org.mytimetable.R;
        import com.androidapp.g_s_org.mytimetable.dbaccess.TitleAccessHelper;

        import static com.androidapp.g_s_org.mytimetable.common.Common.ARG_ORIGINAL_NAME;
        import static com.androidapp.g_s_org.mytimetable.common.Common.ARG_SECTION_NUMBER;


// dialog to edit the name of tab
public class EditTabnameDialogFragment extends DialogFragment {
    // sectionNumber of the selected tab
    private int mSectionNumber;

    public EditTabnameDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // get activity which call this fragment
        Activity caller = getActivity();
        // edittext in the dialog
        final EditText editView = new EditText(caller);
        Bundle args = getArguments();
        if (args != null){
            mSectionNumber = args.getInt(ARG_SECTION_NUMBER);
            editView.setText(args.getString(ARG_ORIGINAL_NAME));
        }
        return new AlertDialog.Builder(caller)
                .setMessage(R.string.title_edittab)
                .setView(editView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // activity which call this
                        Activity caller = getActivity();
                        // create AccessHelper
                        TitleAccessHelper helper = new TitleAccessHelper(caller);
                        // SQLiteDatabase
                        SQLiteDatabase db = null;
                        // value to update
                        ContentValues cv = new ContentValues();
                        // new title
                        String newTitle = editView.getText().toString();
                        cv.put("title", newTitle);
                        // register to DB
                        try {
                            //===
                            //=== update title
                            //===
                            // get db to write
                            db = helper.getWritableDatabase();
                            // begin transaction
                            db.beginTransaction();
                            // uodate title of the tab whose number is mSectionNumber
                            db.update(TitleAccessHelper.TABLE_NAME, cv, "tabId = ?", new String[] {Integer.toString(mSectionNumber)});
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
                            if (caller instanceof MainActivity){
                                ((MainActivity) caller).onEditTabName(mSectionNumber, newTitle);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    @Override
    public void onPause(){
        super.onPause();
        dismiss();
    }

    public interface EditTabNameCallback{
        public void onEditTabName(int sectionNumber, String newTitle);
    }
}
