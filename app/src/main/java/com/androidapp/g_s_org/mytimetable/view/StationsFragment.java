package com.androidapp.g_s_org.mytimetable.view;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidapp.g_s_org.mytimetable.common.Common;
import com.androidapp.g_s_org.mytimetable.httpaccess.HttpGetTrafficAPI;
import com.androidapp.g_s_org.mytimetable.R;
import com.androidapp.g_s_org.mytimetable.dbaccess.StationAccessHelper;
import com.androidapp.g_s_org.mytimetable.container.StationItem;
import com.androidapp.g_s_org.mytimetable.adapter.StationRecyclerViewAdapter;
import com.androidapp.g_s_org.mytimetable.httpaccess.GetStationTimeTableCallback;
import com.androidapp.g_s_org.mytimetable.httpaccess.GetTrainTimeTableCallback;
import com.androidapp.g_s_org.mytimetable.httpaccess.GetTrainsPositionCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.androidapp.g_s_org.mytimetable.common.Common.ARG_POSITION;
import static com.androidapp.g_s_org.mytimetable.common.Common.ARG_SECTION_NUMBER;
import static com.androidapp.g_s_org.mytimetable.common.Common.NONE;
import static com.androidapp.g_s_org.mytimetable.common.Common.REALTIME;
import static com.androidapp.g_s_org.mytimetable.common.Common.STATIC;

public class StationsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this fragment.
     */

    private StationRecyclerViewAdapter mAdapter;
    private static int mSectionNumber;
    private static List<HttpGetTrafficAPI> mRunningTask;

    public StationsFragment() {
    }

    //===
    //=== Returns a new instance of this fragment for the given section number.
    //===
    public static StationsFragment newInstance(int sectionNumber) {
        StationsFragment fragment = new StationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        mSectionNumber = sectionNumber;
        mRunningTask = new ArrayList<>();
        // initialize view
        View listView = view.findViewById(R.id.list);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            RecyclerView recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            // create station list from reading DB
            ArrayList<StationItem> stations = createStationItem(sectionNumber);
            // create adapter
            mAdapter = new StationRecyclerViewAdapter(stations);
            // set adapter
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemLongClickListener(new StationRecyclerViewAdapter.onItemLongClickListener() {
                // show dialog for delete
                @Override
                public void onLongClick(View v, int position) {
                    // fragment
                    DeleteDialogFragment delFragment = new DeleteDialogFragment();
                    // value to pass fragment
                    Bundle args = new Bundle();
                    // set value
                    args.putInt(ARG_POSITION, position);
                    args.putInt(ARG_SECTION_NUMBER, mSectionNumber);
                    // set the value to fragment
                    delFragment.setArguments(args);
                    // make impossible to return with "cancel" button
                    delFragment.setCancelable(false);
                    // show fragment
                    delFragment.show(getActivity().getSupportFragmentManager(), "dialog");
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // get train information of each station
        this.refreshList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public StationRecyclerViewAdapter getAdapter() {
        return this.mAdapter;
    }

    public static int getSectionNumber() {
        return mSectionNumber;
    }

    public int getRowNumber() {
        return mAdapter.getItemCount();
    }

    public void setStationsToAdapter(ArrayList<StationItem> stations) {
        mAdapter.setStations(stations);
    }

    public void refreshStations() {
        mAdapter.notifyDataSetChanged();
    }

    public void cancelTask() {
        for (HttpGetTrafficAPI http : mRunningTask) {
            http.cancel(true);
        }
        mRunningTask.clear();
    }

    public static void addTask(HttpGetTrafficAPI http){
        mRunningTask.add(http);
    }

    public void refreshList() {
        // cancel running task
        cancelTask();
        // get date
        Calendar now = Calendar.getInstance();
        // set callback
        GetTrainsPositionCallback gtpCallback = GetTrainsPositionCallback.getCallback();
        gtpCallback.setCaller(this);
        gtpCallback.setAdapter(mAdapter);
        gtpCallback.setSectionNumWhenCreated(mSectionNumber);
        GetTrainTimeTableCallback gtttCallback = GetTrainTimeTableCallback.getCallback();
        gtttCallback.newTrainMapMap();
        gtttCallback.setAdapter(mAdapter);
        gtttCallback.setGotDate(now);
        GetStationTimeTableCallback gsttCallback = GetStationTimeTableCallback.getCallback();
        gsttCallback.setAdapter(mAdapter);
        gsttCallback.setGotDate(now);
        // get information of train
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            // get stationItem
            StationItem item = mAdapter.getItem(i);
            switch (item.getTimeTableType()) {
                case REALTIME:
                    // make url
                    String url = item.makeURLForTrain();
                    // http access
                    HttpGetTrafficAPI http = new HttpGetTrafficAPI(url, i, gtpCallback);
                    http.execute();
                    mRunningTask.add(http);
                    break;
                case STATIC:
                    // make url
                    String urlForStationTimetable = item.makeURLForStationTimetable(now);
                    // http access
                    HttpGetTrafficAPI httpForStationTimetable = new HttpGetTrafficAPI(urlForStationTimetable, i, gsttCallback);
                    httpForStationTimetable.execute();
                    mRunningTask.add(httpForStationTimetable);
                    break;
                case NONE:
                    // nothing
                    break;
            }

        }
        // set time of refresh to text view
        Activity caller = getActivity();
        if (caller instanceof MainActivity) {
            // get the text view to set time
            TextView time = (TextView) caller.findViewById(R.id.tv_timeOfRefresh);
            int min = now.get(Calendar.MINUTE);
            String minString = min <= 9 ? "0" + Integer.toString(min) : Integer.toString(min);
            time.setText(Integer.toString(now.get(Calendar.HOUR_OF_DAY)) + ":" + minString);
        }
    }

    //===
    //=== create stationItem from DB
    //===
    public ArrayList<StationItem> createStationItem(int sectionNumber) {
        // DB AccessHelperodpt:stationTitle
        StationAccessHelper helper = new StationAccessHelper(getActivity());
        // DB
        SQLiteDatabase db = null;
        // Cursor
        Cursor cursor = null;
        // list of StationItem
        ArrayList<StationItem> list = new ArrayList<>();
        try {
            // get DB object
            db = helper.getReadableDatabase();
            // get data from DB
            cursor = db.query(
                    StationAccessHelper.TABLE_NAME,
                    new String[]{"id", "tabId", "rowId", "operator", "line", "lineForQuery", "stationName", "stationNameForQuery", "direction", "directionForQuery"},
                    "tabId=?",
                    new String[]{Integer.toString(sectionNumber)},
                    null, null, "rowId ASC"
            );
            // read data in sequence and store them to list
            while (cursor.moveToNext()) {
                // test
                int rowId = cursor.getInt(cursor.getColumnIndex("rowId"));
                // get type of time table
                String operator = cursor.getString(cursor.getColumnIndex("operator"));
                int typeOfTimeTable = NONE;
                for (Common.Operator op : Common.Operator.values()) {
                    if (op.getNameForQuery().equals(operator)) {
                        typeOfTimeTable = op.getTypeOfTimetable();
                        break;
                    }
                }
                // create StationItem and add to list
                if (typeOfTimeTable == REALTIME || typeOfTimeTable == STATIC) {
                    list.add(new StationItem(
                            cursor.getInt(cursor.getColumnIndex("rowId")),
                            operator,
                            typeOfTimeTable,
                            cursor.getString(cursor.getColumnIndex("line")),
                            cursor.getString(cursor.getColumnIndex("stationName")),
                            cursor.getString(cursor.getColumnIndex("direction")),
                            cursor.getString(cursor.getColumnIndex("lineForQuery")),
                            cursor.getString(cursor.getColumnIndex("stationNameForQuery")),
                            cursor.getString(cursor.getColumnIndex("directionForQuery"))
                    ));
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        } finally {
            // close cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        /*
        // DB AccessHelper
        StationsOfLineAccessHelper solaHelper = new StationsOfLineAccessHelper(getActivity());
        // DB
        SQLiteDatabase db2 = null;
        // Cursor
        Cursor cursor2 = null;
        try {
            // get DB object
            db2 = helper.getReadableDatabase();
            for (StationItem si : list) {
                // get data from DB
                cursor2 = db2.query(
                        StationAccessHelper.TABLE_NAME,
                        new String[]{"id", "line", "stationName", "stationNameForQuery"},
                        "line=?",
                        new String[]{si.getLineForQuery()},
                        null, null, null
                );
                // read data in sequence and store them to list
                List<QueryItem> stations = new ArrayList<>();
                while (cursor2.moveToNext()) {
                    String sName = cursor2.getString(cursor2.getColumnIndex("stationName"));
                    String sNameForQuery = cursor2.getString(cursor2.getColumnIndex("stationNameForQuery"));
                    stations.add(new QueryItem(sName, sNameForQuery));
                }
                si.setStationsOfLine(stations);
            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        } finally {
            // close cursor
            if (cursor != null) {
                cursor.close();
            }
        }

        // lineを読み込んで登録する
        // 駅名を検索する
        */

        return list;
    }

    public interface StationRefreshCallback {
        public void OnRefreshStations();
    }

}
