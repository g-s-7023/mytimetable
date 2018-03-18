package com.androidapp.g_s_org.mytimetable.container;

        import com.androidapp.g_s_org.mytimetable.common.DateUtil;

        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;
        import java.util.Map;

        import static com.androidapp.g_s_org.mytimetable.common.Common.*;

/**
 * Created by C170044 on 2018/01/31.
 * represents one station
 */

public class StationItem {
    // index of station
    private int mId;
    // information of the three trains nearest to this station
    private List<TrainItem> mTrains;
    // order of stations of the line which this station belongs to
    private Map<String, String> mStationsOfLine;
    // operator
    private Operator mOperator;
    // timetableType
    private int mTimeTableType;
    //===
    //=== string for display
    //===
    private String mLine;
    private String mStationName;
    private String mDirection;
    //===
    //=== string for query
    //===
    private String mLineForQuery;
    private String mStationForQuery;
    private String mDirectionForQuery;
    // protected String stationNameForQuery;
/*
    public StationItem(int id, String operator, int timeTableType, String line, String stationName, String direction, String lineForQuery, String stationForQuery, String directionForQuery) {
        mId = id;
        mOperatorName = operator;
        mTimeTableType = timeTableType;
        mLine = line;
        mStationName = stationName;
        mDirection = direction;
        mLineForQuery = lineForQuery;
        mStationForQuery = stationForQuery;
        mDirectionForQuery = directionForQuery;
        mTrains = new ArrayList<>();
    }
*/
    public StationItem(int id, Operator operator, int timeTableType, String line, String stationName, String direction, String lineForQuery, String stationForQuery, String directionForQuery) {
        mId = id;
        mOperator = operator;
        mTimeTableType = timeTableType;
        mLine = line;
        mStationName = stationName;
        mDirection = direction;
        mLineForQuery = lineForQuery;
        mStationForQuery = stationForQuery;
        mDirectionForQuery = directionForQuery;
        mTrains = new ArrayList<>();
    }

    public Operator getOperator(){ return mOperator; }

    public int getTimeTableType(){ return mTimeTableType; }

    public String getLine(){
        return mLine;
    }

    public String getStationName(){
        return mStationName;
    }

    public String getStationForQuery() {
        return mStationForQuery;
    }

    public String getDirection() {
        return mDirection;
    }

    public String getDirectionForQuery(){
        return mDirectionForQuery;
    }

    public String getLineForQuery() {
        return mLineForQuery;
    }

    public TrainItem getTrainItem(int index){
        if (index < mTrains.size()){
            return mTrains.get(index);
        } else {
            return null;
        }
    }

    public void setTrains(List<TrainItem> trains) {
        mTrains = trains;
    }

    public void setStationsOfLine(Map<String, String> map){
        mStationsOfLine = map;
    }

    public String searchNameOfStation(String nameForQuery){
        String name = "";
        if (mStationsOfLine.containsKey(nameForQuery)){
            name = mStationsOfLine.get(nameForQuery);
        }
        return name;
    }

    // get first three trains and add them to mTrains
    public void resetTrains(List<TrainItem> trains) {
        if (mTrains == null) {
            mTrains = new ArrayList<>();
        }
        mTrains.clear();
        for (int i = 0; i < trains.size() && i < TRAINSNUM_DISPLAY; i++) {
            mTrains.add(trains.get(i));
        }
    }

    public String makeURLForTrain() {
        StringBuilder url = new StringBuilder();
        url.append(PATH_API)
                .append(QUERY_TRAIN)
                .append(KEY_RAILWAY).append("=").append(this.mLineForQuery)
                .append("&")
                .append(KEY_TOKEN).append("=").append(ACCESSTOKEN);
        return url.toString();
    }

    public String makeURLForStationTimetable(Calendar now){
        String cal = DateUtil.getTypeOfDay(now, mOperator);
        StringBuilder url = new StringBuilder();
        url.append(PATH_API)
                .append(QUERY_STATIONTIMETABLE)
                .append(KEY_STATION).append("=").append(this.mStationForQuery)
                .append("&")
                .append(KEY_DIRECTION).append("=").append(this.mDirectionForQuery)
                .append("&")
                .append(KEY_CALENDAR).append("=").append(cal)
                .append("&")
                .append(KEY_TOKEN).append("=").append(ACCESSTOKEN);
        return url.toString();
    }
}
