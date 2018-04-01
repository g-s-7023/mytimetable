package com.androidapp.g_s_org.mytimetable.container;

import com.androidapp.g_s_org.mytimetable.common.Common;

import static com.androidapp.g_s_org.mytimetable.common.Common.TrainType;
import static com.androidapp.g_s_org.mytimetable.common.Common.Operator;

import static java.lang.Integer.parseInt;

/**
 * Created by nao on 2018/02/18.
 */

public class TrainItem {
    // the number of this train
    private String mTrainNumber;
    // where this train has departed from
    private String mFromStation;
    // where this train is arriving at
    // if toStation = null && fromStation = A, the train is stopped at A
    private String mToStation;
    // time to depart at the station
    private String mTimeToDepart;
    // delay from the planned arrival time
    private String mDelay;
    // destination of this train (some trains does not have)
    private QueryItem mDestination;
    // type of this train(e.g. "local","rapid") (some trains does not have)
    private QueryItem mTrainType;
    // direction of this train
    private String mDirection;

    // constructor
    public TrainItem() {
    }

    public TrainItem(String trainNumber, String fromStation, String toStation, String delay, String destination, String destinationForQuery, String trainType, String trainTypeForQuery, String direction) {
        this.mTrainNumber = trainNumber;
        this.mFromStation = fromStation;
        this.mToStation = toStation;
        this.mTimeToDepart = "";
        this.mDelay = delay;
        this.mDestination = new QueryItem(destination, destinationForQuery);
        this.mTrainType = new QueryItem(trainType, trainTypeForQuery);
        this.mDirection = direction;
    }

    public TrainItem(String timeToDepart, String destinationForQuery, String trainTypeForQuery) {
        mTimeToDepart = timeToDepart;
        this.mDestination = new QueryItem("", destinationForQuery);
        this.mTrainType = new QueryItem("", trainTypeForQuery);

    }

    public String getToStation() {
        return mToStation;
    }

    public String getFromStation() {
        return mFromStation;
    }

    public String getDirection() {
        return mDirection;
    }

    public String getTrainNumber() {
        return mTrainNumber;
    }

    public void setTimeToDepart(String time) {
        this.mTimeToDepart = time;
    }

    public String getTimeToDepart() {
        return mTimeToDepart;
    }

    public String getDelay() {
        // if null or "0", return ""
        if (mDelay == null) {
            return "";
        } else {
            try {
                int delay = Integer.parseInt(mDelay);
                if (delay < 60) {
                    // delay is assumed to be minutes
                    return mDelay.equals("0") ? "" : mDelay;
                }
                // deley is assumed to be seconds
                return Integer.toString(delay / 60);
            } catch (Exception e) {
                // cannot parseInt
                return "";
            }
        }
    }

    public QueryItem getDestination() {
        return mDestination;
    }

    public String getTrainTypeName(Operator op) {
        if (mTrainType != null) {
            // get last word of mTrainType
            String trainTypeString = mTrainType.getLastWordOfQuery();
            // search the japanese word corresponding to trainType
            TrainType trainType = TrainType.getTrainType(trainTypeString);
            if (trainType != null){
                return trainType.typeOf(op);
            }
        }
        return mTrainType.getName();
    }
}