package com.androidapp.g_s_org.mytimetable;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.androidapp.g_s_org.mytimetable.Common.ACCESSTOKEN;
import static com.androidapp.g_s_org.mytimetable.Common.ARG_POSITION;
import static com.androidapp.g_s_org.mytimetable.Common.ARG_SECTION_NUMBER;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_CALENDAR;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_DELAY;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_DEPARTURESTATION;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_DEPARTURETIME;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_DESTINATIONSTATION;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_DIRECTION;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_FROMSTATION;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_RAILWAY;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_STATIONTIMETABLE;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_TERMINALSTATION;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_TERMINALSTATIONTITLE;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_TIMETABLE;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_TOKEN;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_TOSTATION;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_TRAINNUMBER;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_TRAINTYPE;
import static com.androidapp.g_s_org.mytimetable.Common.KEY_TRAINTYPETITLE;
import static com.androidapp.g_s_org.mytimetable.Common.NONE;
import static com.androidapp.g_s_org.mytimetable.Common.PATH_API;
import static com.androidapp.g_s_org.mytimetable.Common.QUERY_TRAINTIMETABLE;
import static com.androidapp.g_s_org.mytimetable.Common.REALTIME;
import static com.androidapp.g_s_org.mytimetable.Common.STATIC;

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

    public int getSectionNumber() {
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
    }

    public void refreshList() {
        // cancel running task
        cancelTask();
        // get date
        Calendar now = Calendar.getInstance();
        // set callback
        GetTrainsPositionCallback gtpCallback = GetTrainsPositionCallback.getCallback();
        gtpCallback.setAdapter(mAdapter);
        gtpCallback.setSectionNumWhenCreated(mSectionNumber);
        GetTrainTimeTableCallback gtttCallback = GetTrainTimeTableCallback.getCallback();
        gtttCallback.newTrainMapMap();
        gtttCallback.setAdapter(mAdapter);
        gtttCallback.setSectionNumWhenCreated(mSectionNumber);
        gtttCallback.setGotDate(now);
        GetStationTimeTableCallback gsttCallback = GetStationTimeTableCallback.getCallback();
        gsttCallback.setAdapter(mAdapter);
        gsttCallback.setSectionNumWhenCreated(mSectionNumber);
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

    public static class GetTrainsPositionCallback implements HttpGetTrafficAPI.HttpGetTrafficAPICallback {
        private static GetTrainsPositionCallback mCallback = new GetTrainsPositionCallback();
        private static StationRecyclerViewAdapter mAdapter;
        private static int mSectionNumWhenCreated;

        private GetTrainsPositionCallback() {
        }

        public static GetTrainsPositionCallback getCallback() {
            return mCallback;
        }

        public static void setAdapter(StationRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }

        public static void setSectionNumWhenCreated(int sectionNum) {
            mSectionNumWhenCreated = sectionNum;
        }

        @Override
        public void callback(JSONArray result, int position) {
            if (StationsFragment.mSectionNumber == mSectionNumWhenCreated) {
                // display information of four trains nearest to this station
                //===
                //=== get trains' information
                //===
                // todo
                // list may be unnecessary
                ArrayList<TrainItem> trains = new ArrayList<>();
                HashMap<String, TrainItem> trainMap = new HashMap<>();
                // get stationItem at the "position" in the list
                StationItem stationItem = mAdapter.getItem(position);
                try {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject trainObject = result.getJSONObject(i);
                        // make list of trains whose direction matches that of the stationItem
                        if (trainObject.isNull(KEY_DIRECTION) == false) {
                            String direction = trainObject.getString(KEY_DIRECTION);
                            if (direction.equals(stationItem.getDirectionForQuery())) {
                                // create trainItem and add it to the list
                                String trainNumber = trainObject.isNull(KEY_TRAINNUMBER) ? "" : trainObject.getString(KEY_TRAINNUMBER);
                                String fromStation = trainObject.isNull(KEY_FROMSTATION) ? "" : trainObject.getString(KEY_FROMSTATION);
                                String toStation = trainObject.isNull(KEY_TOSTATION) ? "" : trainObject.getString(KEY_TOSTATION);
                                String delay = trainObject.isNull(KEY_DELAY) ? "" : trainObject.getString(KEY_DELAY);
                                String terminal = trainObject.isNull(KEY_TERMINALSTATIONTITLE) ? "" : trainObject.getString(KEY_TERMINALSTATIONTITLE);
                                String terminalForQuery = trainObject.isNull(KEY_TERMINALSTATION) ? "" : trainObject.getString(KEY_TERMINALSTATION);
                                String trainType = trainObject.isNull(KEY_TRAINTYPETITLE) ? "" : trainObject.getString(KEY_TRAINTYPETITLE);
                                String trainTypeForQuery = trainObject.isNull(KEY_TRAINTYPE) ? "" : trainObject.getString(KEY_TRAINTYPE);
                                TrainItem train = new TrainItem(trainNumber, fromStation, toStation, delay, terminal, terminalForQuery, trainType, trainTypeForQuery, direction);
                                trains.add(train);
                                if (trainNumber.equals("") == false && trainMap.containsKey(trainNumber) == false) {
                                    // if key is not duplicated, register the key to trainMap
                                    trainMap.put(trainNumber, train);
                                } else {
                                    Log.i("StationFragment", "duplicate key");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("StationsFragment", "", e);
                }
                //===
                //=== get timetable of trains
                //===
                if (trains.size() > 0) {
                    // make URL to get timetable
                    StringBuilder url = new StringBuilder();
                    url.append(PATH_API)
                            .append(QUERY_TRAINTIMETABLE)
                            .append(KEY_RAILWAY).append("=").append(stationItem.getLineForQuery())
                            .append("&")
                            .append(KEY_CALENDAR).append("=").append(TimeUtil.getTypeOfDay(Calendar.getInstance(), stationItem.getOperator(), stationItem.getLineForQuery()))
                            .append("&")
                            .append(KEY_TRAINNUMBER).append("=");
                    for (TrainItem train : trains) {
                        url.append(train.getTrainNumber()).append(",");
                    }
                    // replace the last char(',') for '&'
                    url.setCharAt(url.length() - 1, '&');
                    url.append(KEY_TOKEN).append("=").append(ACCESSTOKEN);
                    // http access
                    GetTrainTimeTableCallback gtttCallback = GetTrainTimeTableCallback.getCallback();
                    gtttCallback.putTrainMapMap(position, trainMap);
                    HttpGetTrafficAPI http = new HttpGetTrafficAPI(url.toString(), position, gtttCallback);
                    http.execute();
                    mRunningTask.add(http);
                }
            }
        }
    }

    public static class GetTrainTimeTableCallback implements HttpGetTrafficAPI.HttpGetTrafficAPICallback {
        private static GetTrainTimeTableCallback mCallback = new GetTrainTimeTableCallback();
        private static StationRecyclerViewAdapter mAdapter;
        private static HashMap<Integer, ArrayList<TrainItem>> mTrainsMap;
        // map to store the list of trains got by calling API from each station
        private static HashMap<Integer, HashMap<String, TrainItem>> mTrainMapMap;
        private static int mSectionNumWhenCreated;
        private Calendar mGotDate;


        private GetTrainTimeTableCallback() {
        }

        public static GetTrainTimeTableCallback getCallback() {
            return mCallback;
        }

        public static void setAdapter(StationRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }

        public static void setSectionNumWhenCreated(int sectionNum) {
            mSectionNumWhenCreated = sectionNum;
        }

        public void setGotDate(Calendar date) {
            mGotDate = date;
        }

        public static void newTrainsMap() {
            mTrainsMap = new HashMap<>();
        }

        public static void newTrainMapMap() {
            mTrainMapMap = new HashMap<>();
        }

        public static void putTrainsMap(Integer key, ArrayList<TrainItem> list) {
            mTrainsMap.put(key, list);
        }

        public static void putTrainMapMap(Integer key, HashMap<String, TrainItem> map) {
            mTrainMapMap.put(key, map);
        }

        public static void clearData() {
            mTrainsMap.clear();
            mTrainMapMap.clear();
        }

        @Override
        public void callback(JSONArray result, int position) {
            if (StationsFragment.mSectionNumber == mSectionNumWhenCreated) {
                // get stationItem at the "position" in the list
                StationItem stationItem = mAdapter.getItem(position);
                // store a map temporally
                Map<String, TrainItem> trainsMap = mTrainMapMap.get(position);
                //===
                //=== Parse JSON array
                //===
                try {
                    // check each train
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject trainObject = result.getJSONObject(i);
                        JSONArray timetableArray = trainObject.getJSONArray(KEY_TIMETABLE);
                        // check departure time of each station
                        for (int j = 0; j < timetableArray.length(); j++) {
                            JSONObject timetableEntryObject = timetableArray.getJSONObject(j);
                            // check if the train will stop at the station
                            if (timetableEntryObject.isNull(KEY_DEPARTURESTATION) == false) {
                                if (timetableEntryObject.getString(KEY_DEPARTURESTATION).equals(stationItem.getStationForQuery())) {
                                    if (trainsMap != null) {
                                        // get trainNumber to use for the key of trainsMap
                                        String trainNumber = trainObject.isNull(KEY_TRAINNUMBER) ? "" : trainObject.getString(KEY_TRAINNUMBER);
                                        // search train whose ID matches that got from API
                                        if (trainNumber.equals("") == false && trainsMap.containsKey(trainNumber) == true) {
                                            // register departure time to the trainItem
                                            trainsMap.get(trainNumber).setTimeToDepart(timetableEntryObject.getString(KEY_DEPARTURETIME));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Station Fragment", "", e);
                }
                //===
                //=== filter trainMap by whether each train is yet to depart
                //===
                // get hour of now
                // 0:00-2:59 is represented as 24:00-26:59
                int nowHour = mGotDate.get(Calendar.HOUR);
                if (mGotDate.get(Calendar.AM_PM) == Calendar.AM && nowHour < 3) {
                    nowHour += 24;
                } else if (mGotDate.get(Calendar.AM_PM) == Calendar.PM) {
                    nowHour += 12;
                }
                int nowMinute = mGotDate.get(Calendar.MINUTE);


                // declare new list for sorting
                // (mTrainMapMap cannot be added or removed from multiple threads at the same time)
                List<TrainItem> trainsForSort = new ArrayList<>();
                try {
                    for (String key : trainsMap.keySet()) {
                        String timeToDepart = trainsMap.get(key).getTimeToDepart();
                        String delayString = trainsMap.get(key).getDelay();
                        int delay = 0;
                        int delayMin = 0;
                        if (delayString != null) {
                            delay = delayString.equals("") ? 0 : Integer.parseInt(delayString);
                            delayMin = delay < 60 ? delay : delay / 60;
                        }
                        if (timeToDepart != null) {
                            String[] time = timeToDepart.split(":", 0);
                            // time should contain only two elements(hour, minute)
                            if (time.length == 2) {
                                int hourToDepart = Integer.parseInt(time[0]);
                                // add delay
                                hourToDepart += delayMin / 60;
                                // 0:00-2:59 is represented as 24:00-26:59
                                hourToDepart += (hourToDepart < 3 ? 24 : 0);
                                int minuteToDepart = Integer.parseInt(time[1]);
                                // add delay
                                minuteToDepart += delay % 60;
                                // compare timeToDepart and now
                                if (hourToDepart > nowHour || (hourToDepart == nowHour && minuteToDepart >= nowMinute)) {
                                    // if timeToDepart is later than or equal to now, add the train to ArrayList for sort
                                    trainsForSort.add(trainsMap.get(key));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Station Fragment", "", e);
                }
                //===
                //=== sort trainsMap according to departure time
                //===
                // sort trainList in ascending order
                // implement compare method to decide how to compare the trainItems
                Collections.sort(trainsForSort, new Comparator<TrainItem>() {
                    // compare "timeToDepart" of two trainItems
                    // return positive number if the former trainItem depart earlier than the latter
                    // return 0 if the two trainItem depart at the same time(hardly to happen)
                    // return negative number otherwise
                    public int compare(TrainItem train1, TrainItem train2) {
                        String train1DepartureTime = train1.getTimeToDepart();
                        String train2DepartureTime = train2.getTimeToDepart();
                        if (train1DepartureTime != null && train1DepartureTime.length() != 0 && train2DepartureTime != null && train2DepartureTime.length() != 0) {
                            int time1 = Integer.parseInt(train1DepartureTime.replace(":", ""));
                            int time2 = Integer.parseInt(train2DepartureTime.replace(":", ""));
                            // 0:00-3:59 is represented as 24:00-27:59
                            return (time1 < 400 ? time1 + 2400 : time1) - (time2 < 400 ? time2 + 2400 : time2);
                        }
                        return 0;
                    }
                });
                stationItem.resetTrains(trainsForSort);
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    public static class GetStationTimeTableCallback implements HttpGetTrafficAPI.HttpGetTrafficAPICallback {
        private static GetStationTimeTableCallback mCallback = new GetStationTimeTableCallback();
        private static StationRecyclerViewAdapter mAdapter;
        private static int mSectionNumWhenCreated;
        private Calendar mGotDate;


        private GetStationTimeTableCallback() {
        }

        public static GetStationTimeTableCallback getCallback() {
            return mCallback;
        }

        public static void setAdapter(StationRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }

        public static void setSectionNumWhenCreated(int sectionNum) {
            mSectionNumWhenCreated = sectionNum;
        }

        public void setGotDate(Calendar date) {
            mGotDate = date;
        }

        @Override
        public void callback(JSONArray result, int position) {
            if (StationsFragment.mSectionNumber == mSectionNumWhenCreated) {
                // display information of for trains nearest to this station
                List<TrainItem> trains = new ArrayList<>();
                HashMap<String, TrainItem> trainMap = new HashMap<>();
                // get stationItem at the "position" in the list
                StationItem stationItem = mAdapter.getItem(position);
                //===
                //=== Parse JSON array
                //===
                try {
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject timetableObject = result.getJSONObject(i);
                        // make list of trains whose direction matches that of the stationItem
                        if (timetableObject.isNull(KEY_STATIONTIMETABLE) == false) {
                            JSONArray trainsArray = timetableObject.getJSONArray(KEY_STATIONTIMETABLE);
                            JSONObject trainObject;
                            for (int j = 0; j < trainsArray.length(); j++) {
                                trainObject = trainsArray.getJSONObject(j);
                                String timeToDepart = trainObject.isNull(KEY_DEPARTURETIME) ? "" : trainObject.getString(KEY_DEPARTURETIME);
                                String terminalForQuery = trainObject.isNull(KEY_DESTINATIONSTATION) ? "" : trainObject.getString(KEY_DESTINATIONSTATION);
                                String trainType = trainObject.isNull(KEY_TRAINTYPETITLE) ? "" : trainObject.getString(KEY_TRAINTYPETITLE);
                                String trainTypeForQuery = trainObject.isNull(KEY_TRAINTYPE) ? "" : trainObject.getString(KEY_TRAINTYPE);
                                //===
                                //=== filter trainMap by whether each train is yet to depart
                                //===
                                // get hour of now
                                // 0:00-2:59 is represented as 24:00-26:59
                                int nowHour = mGotDate.get(Calendar.HOUR);
                                if (mGotDate.get(Calendar.AM_PM) == Calendar.AM && nowHour < 3) {
                                    nowHour += 24;
                                } else if (mGotDate.get(Calendar.AM_PM) == Calendar.PM) {
                                    nowHour += 12;
                                }
                                int nowMinute = mGotDate.get(Calendar.MINUTE);
                                // compare now and timeToDepart (whether the train is yet to depart from the station)
                                if (timeToDepart.equals("") == false) {
                                    String[] time = timeToDepart.split(":", 0);
                                    // time should contain only two elements(hour, minute)
                                    if (time.length == 2) {
                                        int hourToDepart = Integer.parseInt(time[0]);
                                        // 0:00-2:59 is represented as 24:00-26:59
                                        hourToDepart += (hourToDepart < 3 ? 24 : 0);
                                        int minuteToDepart = Integer.parseInt(time[1]);
                                        // compare timeToDepart and now
                                        if (hourToDepart > nowHour || (hourToDepart == nowHour && minuteToDepart >= nowMinute)) {
                                            // if timeToDepart is later than or equal to now, add the train to ArrayList for sort
                                            TrainItem trainItem = new TrainItem(timeToDepart, terminalForQuery, trainTypeForQuery);
                                            trains.add(trainItem);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("StationsFragment", "", e);
                }
                //===
                //=== sort trainsMap according to departure time
                //===
                // sort trainList in ascending order
                // implement compare method to decide how to compare the trainItems
                Collections.sort(trains, new Comparator<TrainItem>() {
                    // compare "timeToDepart" of two trainItems
                    // return positive number if the former trainItem depart earlier than the latter
                    // return 0 if the two trainItem depart at the same time(hardly to happen)
                    // return negative number otherwise
                    public int compare(TrainItem train1, TrainItem train2) {
                        String train1DepartureTime = train1.getTimeToDepart();
                        String train2DepartureTime = train2.getTimeToDepart();
                        if (train1DepartureTime != null && train1DepartureTime.length() != 0 && train2DepartureTime != null && train2DepartureTime.length() != 0) {
                            int time1 = Integer.parseInt(train1DepartureTime.replace(":", ""));
                            int time2 = Integer.parseInt(train2DepartureTime.replace(":", ""));
                            // 0:00-3:59 is represented as 24:00-27:59
                            return (time1 < 400 ? time1 + 2400 : time1) - (time2 < 400 ? time2 + 2400 : time2);
                        }
                        return 0;
                    }
                });
                stationItem.resetTrains(trains);
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    //===
    //=== to allow an interaction in this fragment to be communicated
    //===
//    public interface OnListFragmentInteractionListener {
//        void onListFragmentInteraction(StationItem item);
//    }

    // create stationItem from DB
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
