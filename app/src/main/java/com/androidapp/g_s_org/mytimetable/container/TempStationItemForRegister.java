package com.androidapp.g_s_org.mytimetable.container;

        import android.content.ContentValues;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.HashMap;
        import java.util.List;

        import static com.androidapp.g_s_org.mytimetable.common.Common.direction_circular;
        import static com.androidapp.g_s_org.mytimetable.common.Common.direction_linear;

/**
 * Created by C170044 on 2018/03/01.
 */

public class TempStationItemForRegister {
    private QueryItem mOperator;
    private QueryItem mLine;
    private QueryItem mStationName;
    private QueryItem mDirection;
    private HashMap<String, QueryItem[]> mLineAndStation;
    private List<QueryItem> mLineHistory;
    private List<QueryItem> mStationHistory;

    // constructor
    public TempStationItemForRegister(){
        mLineAndStation = new HashMap<>();
    }

    // getter and setter
    public void setOperator(QueryItem mOperator) {
        this.mOperator = mOperator;
    }

    public QueryItem getOperator(){ return mOperator; }

    public QueryItem getLine() {
        return mLine;
    }

    public void setLine(QueryItem mLine) {
        this.mLine = mLine;
    }

    public QueryItem getStationName() {
        return mStationName;
    }

    public void setStationName(QueryItem mStationName) {
        this.mStationName = mStationName;
    }

    public QueryItem getDirection() {
        return mDirection;
    }

    public void setDirection(QueryItem mDirection) {
        this.mDirection = mDirection;
    }

    public List<QueryItem> getLineHistory() {
        return mLineHistory;
    }

    public void setLineHistory(List<QueryItem> mLineHistory) {
        this.mLineHistory = mLineHistory;
    }

    public List<QueryItem> getStationHistory() {
        return mStationHistory;
    }

    public void setStationHistory(List<QueryItem> mStationHistory) {
        this.mStationHistory = mStationHistory;
    }

    public void putLineAndStation(String line, QueryItem[] stations){
        mLineAndStation.put(line, stations);
    }

    // clear values
    public void clearOperator() {
        mOperator = null;
    }
    public void clearLine() {
        mLine = null;
    }
    public void clearStasionName() {
        mStationName = null;
    }
    public void clearStationHistory(){
        mStationHistory = null;
    }
    public void clearLineHistory(){
        mLineHistory = null;
    }

    // return stations correspoinding to the given line
    public List<QueryItem> getStationsOfLine(String line){
        List<QueryItem> stations;
        if (mLineAndStation.containsKey(line)){
            stations = new ArrayList<>(Arrays.asList(mLineAndStation.get(line)));
        } else {
            // if stations corresponding to the selected line does not exist
            // return an empty QueryItem
            stations = new ArrayList<>(Arrays.asList(new QueryItem("", "")));
        }
        return stations;
    }

    // return station at each end of the given line
    public String[] getEndStationsOfLine(String line){
        if (mLineAndStation.containsKey(line)){
            QueryItem[] stations = mLineAndStation.get(line);
            String[] ends = {stations[0].getValueForQuery(), stations[stations.length -1].getValueForQuery()};
            return ends;
        }
        return null;
    }

    // return contents used for registering to DB
    public ContentValues getContentsValues(int sectionNumber, int rowNumber){
        ContentValues cv = new ContentValues();
        cv.put("tabId", sectionNumber);
        cv.put("rowId", rowNumber);
        cv.put("operator", mOperator.getValueForQuery());
        cv.put("line", mLine.getName());
        cv.put("lineForQuery", mLine.getValueForQuery());
        cv.put("stationName", mStationName.getName());
        cv.put("stationNameForQuery", mStationName.getValueForQuery());
        cv.put("direction", mDirection.getName());
        cv.put("directionForQuery", mDirection.getValueForQuery());
        return cv;
    }

    // return contents used for registering to DB
    public ContentValues getCvForLine(){
        ContentValues cv = new ContentValues();
        for (QueryItem q : mStationHistory){
            cv.put("line", mLine.getValueForQuery());
            cv.put("stationName", q.getName());
            cv.put("stationNameForQuery", q.getValueForQuery());
        }
        return cv;
    }

    // return Japanese name for the given direction
    public String searchNameForDirection(String direction){
        if (direction != null) {
            // get the last word of direction
            String[] st = direction.split("\\.", 0);
            String lastDir = st.length == 0 ? "" : st[st.length - 1];
            // search from mStationHistory
            for (QueryItem his : mStationHistory) {
                // if the last word of direction matches that of st, return the name of st
                if (his.getLastWordOfQuery().equals(lastDir)){
                    return his.getValue();
                }
            }
            // search from Common
            for (QueryItem commonDirLinear : direction_linear){
                if (commonDirLinear.getValueForQuery().equals(direction)){
                    return commonDirLinear.getValue();
                }
            }
            for (QueryItem commonDirCircular : direction_circular){
                if (commonDirCircular.getValueForQuery().equals(direction)){
                    return commonDirCircular.getValue();
                }
            }
            // if there is no entry to match, return the last word of direction
            return lastDir;
        }
        return "";
    }




}
