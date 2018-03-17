package com.androidapp.g_s_org.mytimetable.httpaccess;

import android.util.Log;

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
import java.util.Map;

import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_DEPARTURESTATION;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_DEPARTURETIME;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TIMETABLE;
import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TRAINNUMBER;

public class GetTrainTimeTableCallback implements HttpGetTrafficAPI.HttpGetTrafficAPICallback {
    private static GetTrainTimeTableCallback mCallback = new GetTrainTimeTableCallback();
    private StationRecyclerViewAdapter mAdapter;
    // map to store the list of trains got by calling API from each station
    private HashMap<Integer, HashMap<String, TrainItem>> mTrainMapMap;
    private Calendar mGotDate;


    private GetTrainTimeTableCallback() {
    }

    public static GetTrainTimeTableCallback getCallback() {
        return mCallback;
    }

    public void setAdapter(StationRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    public void setGotDate(Calendar date) {
        mGotDate = date;
    }

    public void newTrainMapMap() {
        mTrainMapMap = new HashMap<>();
    }

    public void putTrainMapMap(Integer key, HashMap<String, TrainItem> map) {
        mTrainMapMap.put(key, map);
    }

    @Override
    public void callback(JSONArray result, int position) {
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
