package com.androidapp.g_s_org.mytimetable.httpaccess;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.androidapp.g_s_org.mytimetable.adapter.StationRecyclerViewAdapter;
import com.androidapp.g_s_org.mytimetable.common.DateUtil;
import com.androidapp.g_s_org.mytimetable.container.StationItem;
import com.androidapp.g_s_org.mytimetable.container.TrainItem;
import com.androidapp.g_s_org.mytimetable.view.StationsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import static com.androidapp.g_s_org.mytimetable.common.Common.ACCESSTOKEN;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_CALENDAR;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_DELAY;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_DIRECTION;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_FROMSTATION;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_RAILWAY;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TERMINALSTATION;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TERMINALSTATIONTITLE;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TOKEN;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TOSTATION;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TRAINNUMBER;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TRAINTYPE;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TRAINTYPETITLE;
import static com.androidapp.g_s_org.mytimetable.common.Common.PATH_API;
import static com.androidapp.g_s_org.mytimetable.common.Common.QUERY_TRAINTIMETABLE;


// callback when
public class GetTrainsPositionCallback implements HttpGetTrafficAPI.HttpGetTrafficAPICallback {
    private static GetTrainsPositionCallback mCallback = new GetTrainsPositionCallback();
    private static Fragment mCaller;
    private static StationRecyclerViewAdapter mAdapter;
    private static Calendar mGotDate;
    private static int mSectionNumWhenCreated;

    private GetTrainsPositionCallback() {
    }

    public static GetTrainsPositionCallback getCallback() {
        return mCallback;
    }

    public static GetTrainsPositionCallback newCallback(Fragment caller, StationRecyclerViewAdapter adapter, Calendar date, int section) {
        mCaller = caller;
        mAdapter = adapter;
        mGotDate = date;
        mSectionNumWhenCreated = section;
        return mCallback;
    }

    public void setCaller(Fragment caller) {
        mCaller = caller;
    }

    public void setAdapter(StationRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    public void setGotDate(Calendar date){ mGotDate = date; }

    public void setSectionNumWhenCreated(int sectionNum) {
        mSectionNumWhenCreated = sectionNum;
    }

    @Override
    public void callback(JSONArray result, int position) {
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
                    .append(KEY_CALENDAR).append("=").append(DateUtil.getTypeOfDay(Calendar.getInstance(), stationItem.getOperator()))
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
            if (mCaller instanceof StationsFragment) {
                ((StationsFragment) mCaller).addTask(http);
            }
        }
    }
}

