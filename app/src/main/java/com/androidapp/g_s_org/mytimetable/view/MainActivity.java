package com.androidapp.g_s_org.mytimetable.view;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;

import com.androidapp.g_s_org.mytimetable.R;
import com.androidapp.g_s_org.mytimetable.container.StationItem;
import com.androidapp.g_s_org.mytimetable.dbaccess.TitleAccessHelper;

import java.util.ArrayList;

import static com.androidapp.g_s_org.mytimetable.common.Common.ARG_ORIGINAL_NAME;

public class MainActivity extends AppCompatActivity implements
        StationsFragment.StationRefreshCallback,
        DeleteDialogFragment.DeleteSucceedCallback,
        EditTabnameDialogFragment.EditTabNameCallback
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_ROW_NUMBER = "row_number";
    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //===
        //=== Setup spinner
        //===
        // read title from DB
        titles = new String[]{"", "", ""};
        // create AccessHelper
        TitleAccessHelper helper = new TitleAccessHelper(this);
        // SQLiteDatabase
        SQLiteDatabase db = null;
        // Cursor
        Cursor cursor = null;
        //===
        //=== read DB and get titles of tabs
        //===
        try {
            // read db and get rows whose rowId is larger than rowId
            db = helper.getReadableDatabase();
            // get data from DB
            cursor = db.query(
                    TitleAccessHelper.TABLE_NAME,
                    new String[]{"id", "tabId", "title"},
                    null,
                    null,
                    null, null, null
            );
            int index;
            String title;
            while (cursor.moveToNext()){
                index = cursor.getInt(cursor.getColumnIndex("tabId"));
                if (index < titles.length) {
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    titles[index] = title;
                }
            }
        } catch (Exception e) {
            // output log
            Log.e("AlertDialog.onClick", e.toString());
        } finally {
            // close DB
            db.close();
        }
        //===
        //=== set spinner
        //===
        // set tab name
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                titles ));
        // set listener
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // get fragment and stop task
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if (fragment != null && fragment instanceof StationsFragment) {
                    ((StationsFragment) fragment).cancelTask();
                } else {
                    Log.d("getFragment", "null or not instanceof StationFragment");
                }
                // When the given dropdown item is selected, show its contents in the
                // container view.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, StationsFragment.newInstance(position))
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // select spinner if designated
        int sectionNumber = getIntent().getIntExtra(ARG_SECTION_NUMBER, 0);
        spinner.setSelection(sectionNumber);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            //===
            //=== update information of the station
            //===
            @Override
            public void onClick(View view) {
                // get fragment
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if (fragment != null && fragment instanceof StationsFragment) {
                    ((StationsFragment) fragment).refreshList();
                } else {
                    Log.d("getFragment", "null or not instanceof StationFragment");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_rename_tabs:
                //===
                //=== show dialog to edit the name of tab
                //===
                // get fragment to get the sectionNumber
                Fragment tFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if (tFragment != null && tFragment instanceof StationsFragment) {
                    // get section number and row number from Station Fragment
                    int sectionNumber = ((StationsFragment) tFragment).getSectionNumber();
                    // create dialogfragment
                    EditTabnameDialogFragment editFragment = new EditTabnameDialogFragment();
                    // value to pass fragment
                    Bundle args = new Bundle();
                    // set value
                    args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                    args.putString(ARG_ORIGINAL_NAME, titles[sectionNumber]);
                    // set the value to fragment
                    editFragment.setArguments(args);
                    // make impossible to return with "cancel" button
                    editFragment.setCancelable(false);
                    // show fragment
                    editFragment.show(getSupportFragmentManager(), "dialog");
                }
                break;
            case R.id.action_addstation:
                //===
                //=== move activity to add station
                //===
                // get fragment to get the sectionNumber
                Fragment aFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if (aFragment != null && aFragment instanceof StationsFragment) {
                    // get section number and row number from Station Fragment
                    int sectionNumber = ((StationsFragment) aFragment).getSectionNumber();
                    int rowNumber = ((StationsFragment) aFragment).getRowNumber();
                    // create intent
                    Intent intent = new Intent(MainActivity.this, AddStationActivity.class);
                    // set intent
                    intent.putExtra(ARG_SECTION_NUMBER, sectionNumber);
                    intent.putExtra(ARG_ROW_NUMBER, rowNumber);
                    // start activity(AddStationActivity)
                    startActivity(intent);
                    MainActivity.this.finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnRefreshStations(){

    }

    @Override
    public void onDeleteSucceed(int sectionNumber){
        // get fragment to refresh
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment != null && fragment instanceof StationsFragment) {
            ((StationsFragment) fragment).cancelTask();
            ArrayList<StationItem> stations = ((StationsFragment) fragment).createStationItem(sectionNumber);
            ((StationsFragment) fragment).setStationsToAdapter(stations);
            ((StationsFragment) fragment).refreshStations();
        }
    }

    @Override
    public void onEditTabName(int sectionNumber, String newTitle){
        // set new title to spinner
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        titles[sectionNumber] = newTitle;
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                titles ));
        spinner.setSelection(sectionNumber);
    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }
}
