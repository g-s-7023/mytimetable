package com.androidapp.g_s_org.mytimetable.container;

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
    private String mDestination;
    // destination of this train used for query
    private String mDestinationForQuery;
    // type of this train(e.g. "local","rapid") (some trains does not have)
    private String mTrainType;
    // type of this train used for query
    private String mTrainTypeForQuery;
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
        this.mDestination = destination;
        this.mDestinationForQuery = destinationForQuery;
        this.mTrainType = trainType;
        this.mTrainTypeForQuery = trainTypeForQuery;
        this.mDirection = direction;
    }

    public TrainItem(String timeToDepart, String destinationForQuery, String trainTypeForQuery) {
        mTimeToDepart = timeToDepart;
        mDestinationForQuery = destinationForQuery;
        mTrainTypeForQuery = trainTypeForQuery;
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
            } catch (Exception e){
                // cannot parseInt
                return "";
            }
        }
    }

    public String getDestination() {
        return mDestination;
    }

    public String getTrainType() {
        return mTrainType;
    }

    public String getDestinationName() {
        return getSplitedbyCommaColon(mDestinationForQuery);
    }

    public String getTrainTypeName() {
        if (mTrainType != null){
            // isNullOrEmpty(mTrainTypeForQuery)
            if (mTrainTypeForQuery == null) {
                switch (mTrainType) {
                    case "普通":
                    case "各停":
                    case "各駅停車":
                        return "Local";
                    case "快速":
                        return "Rapid";
                    case "中央特快":
                        return "Chuo Limited Rapid";
                    case "通勤特快":
                        return "Commuter Limited Rapid";
                    case "通勤快速":
                        return "Commuter Rapid";
                    case "急行":
                        return "Express";
                    case "準急":
                        return "Semi Express";
                    case "特急":
                        return "Limited Express";
                    default:
                        return "";
                }
            } else if (mTrainTypeForQuery.equals("")){
                switch (mTrainType) {
                    case "普通":
                    case "各停":
                    case "各駅停車":
                        return "Local";
                    case "快速":
                        return "Rapid";
                    case "中央特快":
                        return "Chuo Limited Rapid";
                    case "通勤特快":
                        return "Commuter Limited Rapid";
                    case "通勤快速":
                        return "Commuter Rapid";
                    case "急行":
                        return "Express";
                    case "準急":
                        return "Semi Express";
                    case "特急":
                        return "Limited Express";
                    default:
                        return "";
                }
            }
        }

        return getSplitedbyCommaColon(mTrainTypeForQuery);

        /*
        if (mTrainType != null) {
            if (mTrainType.equals("") == false) {
                return mTrainType;
            }
        }
        if (mTrainTypeForQuery != null) {
            if (Common.TrainTypeMap.containsKey(mTrainTypeForQuery)) {
                return Common.TrainTypeMap.get(mTrainTypeForQuery);
            }
            String[] st1 = mTrainTypeForQuery.split("\\.", 0);
            if (st1.length > 0) {
                String[] st2 = st1[st1.length - 1].split(":", 0);
                if (st2.length > 0) {
                    String type = st2[st2.length - 1];
                    for (QueryItem q : Common.trainTypeItems) {
                        if (q.getValueForQuery().equals(type)) {
                            return q.getValue();
                        }
                    }
                    return type;
                }
            }
        }
        return "";
        */
    }

    public String getSplitedbyCommaColon(String src){
        if (src != null) {
            // split by comma
            String[] st1 = src.split("\\.", 0);
            if (st1.length > 0) {
                // split by colon
                String[] st2 = st1[st1.length - 1].split(":", 0);
                if (st2.length > 0) {
                    return st2[st2.length - 1];
                }
            }
        }
        return "";
    }

}