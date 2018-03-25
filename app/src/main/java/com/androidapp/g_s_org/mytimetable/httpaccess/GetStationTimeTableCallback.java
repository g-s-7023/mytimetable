package com.androidapp.g_s_org.mytimetable.httpaccess;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.androidapp.g_s_org.mytimetable.adapter.StationRecyclerViewAdapter;
import com.androidapp.g_s_org.mytimetable.container.StationItem;
import com.androidapp.g_s_org.mytimetable.container.TrainItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_DEPARTURETIME;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_DESTINATIONSTATION;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_STATIONTIMETABLE;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TRAINTYPE;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TRAINTYPETITLE;

public class GetStationTimeTableCallback implements HttpGetTrafficAPI.HttpGetTrafficAPICallback {
    private static GetStationTimeTableCallback mCallback = new GetStationTimeTableCallback();
    private static StationRecyclerViewAdapter mAdapter;
    private static Calendar mGotDate;
    // key:row, val:number of trains to get in this callback
    private static SparseIntArray mTrainNumToGet;
    // get trains whose departure time is after filter date
    // key:row, val:{hour, minute}
    private static SparseArray<int[]> mFilterDates;

    private GetStationTimeTableCallback() {
    }

    public static GetStationTimeTableCallback getCallback() {
        return mCallback;
    }

    public static GetStationTimeTableCallback newCallback(StationRecyclerViewAdapter adapter, Calendar date){
        mAdapter = adapter;
        mGotDate = date;
        mTrainNumToGet = new SparseIntArray();
        mFilterDates = new SparseArray();
        return mCallback;
    }

    public void setAdapter(StationRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    public void setGotDate(Calendar date) {
        mGotDate = date;
    }

    public void setTrainNumToGet(int row, int trainNumToGet){ mTrainNumToGet.append(row, trainNumToGet); }

    public void setFilterDate(int row, int hour, int min) {
        int[] date = {hour, min};
        mFilterDates.put(row, date);
    }

    public void setFilterDate(int row, Calendar date){
        // get hour of date
        // 0:00-2:59 is represented as 24:00-26:59
        int hour = date.get(Calendar.HOUR);
        if (date.get(Calendar.AM_PM) == Calendar.AM && hour < 3) {
            hour += 24;
        } else if (date.get(Calendar.AM_PM) == Calendar.PM) {
            hour += 12;
        }
        int min = date.get(Calendar.MINUTE);
        // get minute of date
        int[] filterDate = {hour, min};
        mFilterDates.put(row, filterDate);
    }

    @Override
    public void callback(JSONArray result, int position) {
        // display information of for trains nearest to this station
        List<TrainItem> trains = new ArrayList<>();
        HashMap<String, TrainItem> trainMap = new HashMap<>();
        // get stationItem at the "position" in the list
        StationItem stationItem = mAdapter.getItem(position);
        //===
        //=== Parse JSON array
        //===
        try {
            int[] filterDate = mFilterDates.get(position);
            int filterHour = filterDate[0];
            int filterMinute = filterDate[1];
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
                        /*
                        // get hour of now
                        // 0:00-2:59 is represented as 24:00-26:59
                        int filterHour = mGotDate.get(Calendar.HOUR);
                        if (mGotDate.get(Calendar.AM_PM) == Calendar.AM && filterHour < 3) {
                            filterHour += 24;
                        } else if (mGotDate.get(Calendar.AM_PM) == Calendar.PM) {
                            filterHour += 12;
                        }
                        int filterMinute = mGotDate.get(Calendar.MINUTE);
                        */
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
                                if (hourToDepart > filterHour || (hourToDepart == filterHour && minuteToDepart >= filterMinute)) {
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
        //stationItem.resetTrains(trains);
        stationItem.appendTrains(trains, mTrainNumToGet.get(position));
        mAdapter.notifyItemChanged(position);
    }
}