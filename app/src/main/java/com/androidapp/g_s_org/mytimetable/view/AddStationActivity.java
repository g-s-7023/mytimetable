package com.androidapp.g_s_org.mytimetable.view;

        import android.content.ContentValues;
        import android.content.Context;
        import android.content.Intent;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;

        import com.androidapp.g_s_org.mytimetable.common.Common;
        import com.androidapp.g_s_org.mytimetable.httpaccess.HttpGetTrafficAPI;
        import com.androidapp.g_s_org.mytimetable.container.QueryItem;
        import com.androidapp.g_s_org.mytimetable.R;
        import com.androidapp.g_s_org.mytimetable.container.TempStationItemForRegister;
        import com.androidapp.g_s_org.mytimetable.adapter.AddStationRecyclerViewAdapter;
        import com.androidapp.g_s_org.mytimetable.dbaccess.StationAccessHelper;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;

        import static com.androidapp.g_s_org.mytimetable.common.Common.ACCESSTOKEN;
        import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_DIRECTION;
        import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_OPERATOR;
        import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_RAILWAY;
        import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_SAMEAS;
        import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_STATION;
        import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_STATIONORDER;
        import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_STATIONTITLE;
        import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TITLE;
        import static com.androidapp.g_s_org.mytimetable.common.Common.KEY_TOKEN;
        import static com.androidapp.g_s_org.mytimetable.common.Common.PATH_API;
        import static com.androidapp.g_s_org.mytimetable.common.Common.QUERY_RAILWAY;
        import static com.androidapp.g_s_org.mytimetable.common.Common.QUERY_STATION;
        import static com.androidapp.g_s_org.mytimetable.common.Common.QUERY_STATIONTIMETABLE;
        import static com.androidapp.g_s_org.mytimetable.common.Common.REALTIME;
        import static com.androidapp.g_s_org.mytimetable.common.Common.SELECT_DIRECTION;
        import static com.androidapp.g_s_org.mytimetable.common.Common.SELECT_LINE;
        import static com.androidapp.g_s_org.mytimetable.common.Common.SELECT_OPERATOR;
        import static com.androidapp.g_s_org.mytimetable.common.Common.SELECT_STATION;
        import static com.androidapp.g_s_org.mytimetable.common.Common.STATIC;
        import static com.androidapp.g_s_org.mytimetable.common.Common.YAMANOTE_LINE;

public class AddStationActivity extends AppCompatActivity {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_ROW_NUMBER = "row_number";
    private AddStationRecyclerViewAdapter mAdapter;
    private TempStationItemForRegister mStation;

    private static int mPhase;
    private int mSectionNumber;
    private int mRowNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //===
        //=== get intent from previous activity
        //===
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mSectionNumber = extras.getInt(ARG_SECTION_NUMBER);
            mRowNumber = extras.getInt(ARG_ROW_NUMBER);
        }
        //===
        //=== set layout
        //===
        setContentView(R.layout.activity_add_station);
        //===
        //=== set phase
        //===
        mPhase = SELECT_OPERATOR;
        //===
        //=== initialize toolbar
        //===
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.select_operator);
        setSupportActionBar(toolbar);
        //===
        //=== initialize list
        //===
        View listView = findViewById(R.id.stationadd_list);
        // create TempStationItemForRegister
        mStation = new TempStationItemForRegister();
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            RecyclerView recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            // create the first values of the list(= train operators)
            List<QueryItem> values = createOperatorList();
            // create listener
            AddStationSelectListener listener = new AddStationSelectListener();
            // create adapter
            mAdapter = new AddStationRecyclerViewAdapter(values, listener);
            // set adapter
            recyclerView.setAdapter(mAdapter);
        }
        //===
        //=== initialize buttons
        //===
        // left(return) button
        Button leftButton = (Button)findViewById(R.id.bt_stationadd_left);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // back to previous phase
                switch (mPhase){
                    case SELECT_OPERATOR:
                        // cannot be called(button is invisible)
                        break;
                    case SELECT_LINE:
                        // clear selected value
                        mStation.clearOperator();
                        // set the phase to previous one
                        mPhase = SELECT_OPERATOR;
                        // set title
                        ((Toolbar) findViewById(R.id.toolbar)).setTitle(R.string.select_operator);
                        // show list
                        showOperators();
                        // make right button invisible
                        findViewById(R.id.bt_stationadd_left).setVisibility(View.INVISIBLE);
                        break;
                    case SELECT_STATION:
                        // clear selected value
                        mStation.clearLine();
                        // set the phase to previous one
                        mPhase = SELECT_LINE;
                        // set title
                        ((Toolbar) findViewById(R.id.toolbar)).setTitle(R.string.select_line);
                        // show list
                        mAdapter.setValues(mStation.getLineHistory());
                        mAdapter.notifyDataSetChanged();
                        break;
                    case SELECT_DIRECTION:
                        // clear selected value
                        mStation.clearStasionName();
                        // set the phase to previous one
                        mPhase = SELECT_STATION;
                        // set title
                        ((Toolbar) findViewById(R.id.toolbar)).setTitle(R.string.select_station);
                        // show list
                        mAdapter.setValues(mStation.getStationHistory());
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        // right(cancel) button
        Button rightButton = (Button)findViewById(R.id.bt_stationadd_right);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // quit activity
                // create intent
                Intent intent = new Intent(AddStationActivity.this, MainActivity.class);
                // start activity(MainActivity)
                startActivity(intent);
                // finish this activity
                AddStationActivity.this.finish();
            }
        });
        // make right button invisible
        findViewById(R.id.bt_stationadd_left).setVisibility(View.INVISIBLE);
    }

    public void showOperators(){
        // get operators
        List<QueryItem> operators = createOperatorList();
        // set operators to adapter
        mAdapter.setValues(operators);
        // show list
        mAdapter.notifyDataSetChanged();
    }

    public void showLines(QueryItem item){
        // set title
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(R.string.select_line);
        // make right button visible
        findViewById(R.id.bt_stationadd_left).setVisibility(View.VISIBLE);
        // update operator of station to register
        mStation.setOperator(item);
        // send "operator" to API and get corresponding "line"s
        // make url
        StringBuilder urlForRailway = new StringBuilder();
        urlForRailway.append(PATH_API)
                .append(QUERY_RAILWAY)
                .append(KEY_OPERATOR).append("=").append(item.getValueForQuery())
                .append("&")
                .append(KEY_TOKEN).append("=").append(ACCESSTOKEN);
        // make callback
        GetRailwayCallback grCallback = GetRailwayCallback.getCallback();
        grCallback.setAdapter(mAdapter);
        grCallback.setStation(mStation);
        // http access
        HttpGetTrafficAPI httpForRailway = new HttpGetTrafficAPI(urlForRailway.toString(), grCallback);
        httpForRailway.execute();
        // after execution, grCallback.callback will be done
    }

    public void showStations(QueryItem item){
        // set title
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(R.string.select_station);
        // update line of station to register
        mStation.setLine(item);
        // send "station" to API and get the name of the station
        // make URL
        StringBuilder urlForStationName = new StringBuilder();
        urlForStationName.append(PATH_API)
                .append(QUERY_STATION)
                .append(KEY_RAILWAY).append("=").append(item.getValueForQuery())
                .append("&")
                .append(KEY_TOKEN).append("=").append(ACCESSTOKEN);
        // make callback
        GetStationsCallback gsCallback = GetStationsCallback.getCallback();
        gsCallback.setAdapter(mAdapter);
        gsCallback.setStation(mStation);
        // http access
        HttpGetTrafficAPI httpForStationName = new HttpGetTrafficAPI(urlForStationName.toString(), gsCallback);
        httpForStationName.execute();
        // after execution, grCallback.callback will be done
    }

    public void showDirections(QueryItem item){
        // set title
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(R.string.select_direction);
        // update name of station to register
        mStation.setStationName(item);

        if (mStation.getOperator().getValueForQuery().equals(Common.Operator.JREast.getNameForQuery())){
            //===
            //=== get direction from Common.direction(cannot get stationTimeTable of JR-East's stations)
            //===
            if (mStation.getLine().getValueForQuery().equals(YAMANOTE_LINE)){
                // set direction_JRcircular
                mAdapter.setValues(new ArrayList<QueryItem>(Arrays.asList(Common.direction_JRcircular)));
            } else {
                // set direction_JRlinear
                mAdapter.setValues(new ArrayList<QueryItem>(Arrays.asList(Common.direction_JRlinear)));
            }
            // go to next phase
            AddStationActivity.mPhase = SELECT_DIRECTION;
            // show list
            mAdapter.notifyDataSetChanged();
        } else {
            //===
            //=== get direction from stationTimeTable at each end of the line
            //===
            // get stations corresponding to the selected line
            String line = mStation.getLine().getValueForQuery();
            // get stations at each end of the line
            String[] ends = mStation.getEndStationsOfLine(line);
            // send "station"s at each end of the line to API and get corresponding "timetable"s
            // make url
            StringBuilder urlForTimetable = new StringBuilder();
            urlForTimetable.append(PATH_API)
                    .append(QUERY_STATIONTIMETABLE)
                    .append(KEY_STATION).append("=");
            for (String st : ends) {
                urlForTimetable.append(st).append(",");
            }
            // replace the last char(',') for '&'
            urlForTimetable.setCharAt(urlForTimetable.length() - 1, '&');
            urlForTimetable.append(KEY_TOKEN).append("=").append(ACCESSTOKEN);
            // make callback
            GetDirectionCallback gdCallback = GetDirectionCallback.getCallback();
            gdCallback.setAdapter(mAdapter);
            gdCallback.setStation(mStation);
            // http access
            HttpGetTrafficAPI httpForTimetable = new HttpGetTrafficAPI(urlForTimetable.toString(), gdCallback);
            httpForTimetable.execute();
            // after execution, gdCallback.callback will be done
        }
    }

    public void storeStation(QueryItem item){
        mStation.setDirection(item);
        //===
        //=== register the information of the station to DB
        //===
        // create AccessHelper
        StationAccessHelper saHelper = new StationAccessHelper(AddStationActivity.this);
        // SQLiteDatabase
        SQLiteDatabase db = null;
        // ContentValues object for storing values to insert
        ContentValues cvStation = mStation.getContentsValuesOfStation(mSectionNumber, mRowNumber);
        // register to DB
        try {
            // get DB object
            db = saHelper.getWritableDatabase();
            // begin transaction
            db.beginTransaction();
            // insert to table_station
            long insertedId = db.insert(StationAccessHelper.TABLE_STATION, null, cvStation);
            // get id of the inserted entry and put it to the line table
            List<ContentValues> stationList = mStation.getContentsValuesOfLine((int)insertedId);
            for (ContentValues s : stationList){
                // insert to table_stationsOfLine
                db.insert(StationAccessHelper.TABLE_STATIONOFLINE, null, s);
            }
            // commit
            db.setTransactionSuccessful();
        } catch (Exception e) {
            // output log
            Log.e("ERROR", e.toString());
        } finally {
            // end transaction
            db.endTransaction();
            // close DB
            db.close();
            // create intent
            Intent intent = new Intent(AddStationActivity.this, MainActivity.class);
            // put tabId to show after moving activity
            intent.putExtra(ARG_SECTION_NUMBER, mSectionNumber);
            // start activity(MainActivity)
            startActivity(intent);
            // finish this activity
            AddStationActivity.this.finish();
        }
    }

    // listener called when an item of the list selected to add station
    public class AddStationSelectListener implements AddStationRecyclerViewAdapter.OnAddItemSelectedListener {

        // constructor
        public AddStationSelectListener() {
        }

        public void onAddItemSelected(QueryItem item) {
            switch (mPhase) {
                case SELECT_OPERATOR:
                    showLines(item);
                    break;
                case SELECT_LINE:
                    showStations(item);
                    break;
                case SELECT_STATION:
                    showDirections(item);
                    break;
                case SELECT_DIRECTION:
                    storeStation(item);
                    break;
            }
        }
    }

    // callback when get the information of the railway
    public static class GetRailwayCallback implements HttpGetTrafficAPI.HttpGetTrafficAPICallback {
        private static GetRailwayCallback mCallback = new GetRailwayCallback();
        private static AddStationRecyclerViewAdapter mAdapter;
        private static TempStationItemForRegister mStation;

        private GetRailwayCallback() {
        }

        public static GetRailwayCallback getCallback() {
            return mCallback;
        }

        public static void setAdapter(AddStationRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }

        public static void setStation(TempStationItemForRegister station) {
            mStation = station;
        }

        @Override
        public void callback(JSONArray result, int position) {
            List<QueryItem> lines = new ArrayList<>();
            //===
            //=== parse JSON Array
            //===
            try {
                // check each line
                for (int i = 0; i < result.length(); i++) {
                    // get line
                    JSONObject trainObject = result.getJSONObject(i);
                    QueryItem line = new QueryItem();
                    line.setValue(trainObject.isNull(KEY_TITLE) ? "" : trainObject.getString(KEY_TITLE));
                    line.setValueForQuery(trainObject.isNull(KEY_SAMEAS) ? "" : trainObject.getString(KEY_SAMEAS));
                    // add line to the list for display
                    lines.add(line);
                    // get stations of the line
                    JSONArray stationOrder = trainObject.isNull(KEY_STATIONORDER) ? null : trainObject.getJSONArray(KEY_STATIONORDER);
                    QueryItem[] stations = null;
                    if (stationOrder != null) {
                        stations = new QueryItem[stationOrder.length()];
                        for (int j = 0; j < stationOrder.length(); j++) {
                            JSONObject stationObject = stationOrder.getJSONObject(j);
                            stations[j] = new QueryItem();
                            stations[j].setValue(stationObject.isNull(KEY_STATIONTITLE) ? "" : stationObject.getString(KEY_STATIONTITLE));
                            stations[j].setValueForQuery(stationObject.isNull(KEY_STATION) ? "" : stationObject.getString(KEY_STATION));
                        }
                    }
                    // store line and stations to temp
                    mStation.putLineAndStation(line.getValueForQuery(), stations);
                }
            } catch (Exception e) {
                Log.e("Station Fragment", "", e);
            }
            //===
            //=== update all items and phase of adapter
            //===
            // go to next phase
            AddStationActivity.mPhase = SELECT_LINE;
            // add lineHistory
            mStation.setLineHistory(lines);
            // show list
            mAdapter.setValues(lines);
            mAdapter.notifyDataSetChanged();
        }
    }

    // callback when get the information of the stations
    public static class GetStationsCallback implements HttpGetTrafficAPI.HttpGetTrafficAPICallback{
        private static GetStationsCallback mCallback = new GetStationsCallback();
        private static AddStationRecyclerViewAdapter mAdapter;
        private static TempStationItemForRegister mStation;

        private GetStationsCallback(){}

        public static GetStationsCallback getCallback() { return mCallback; }
        public static void setAdapter(AddStationRecyclerViewAdapter adapter){mAdapter = adapter;}
        public static void setStation(TempStationItemForRegister station){mStation = station;}

        @Override
        public void callback(JSONArray result, int position) {
            List<QueryItem> stations = new ArrayList<>();
            //===
            //=== parse JSON Array
            //===
            try {
                // check each timetable
                for (int i = 0; i < result.length(); i++) {
                    // get information of the station
                    JSONObject stationObject = result.getJSONObject(i);
                    // get name of the station
                    String stationName = stationObject.isNull(KEY_TITLE) ? "" : stationObject.getString(KEY_TITLE);
                    String stationForQuery = stationObject.isNull(KEY_SAMEAS) ? "" : stationObject.getString(KEY_SAMEAS);
                    stations.add(new QueryItem(stationName, stationForQuery));
                }
            } catch (Exception e) {
                Log.e("Station Fragment", "", e);
            }
            //===
            //=== update values and phase of adapter
            //===
            // go to next phase
            mPhase = SELECT_STATION;
            // add lineHistory
            mStation.setStationHistory(stations);
            // show list
            mAdapter.setValues(stations);
            mAdapter.notifyDataSetChanged();
        }
    }

    // callback when get the information of the direction from stationTimetable at each end of the line
    public static class GetDirectionCallback implements HttpGetTrafficAPI.HttpGetTrafficAPICallback {
        private static GetDirectionCallback mCallback = new GetDirectionCallback();
        private static AddStationRecyclerViewAdapter mAdapter;
        private static TempStationItemForRegister mStation;

        private GetDirectionCallback() {
        }

        public static GetDirectionCallback getCallback() {
            return mCallback;
        }

        public static void setAdapter(AddStationRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }

        public static void setStation(TempStationItemForRegister station) {
            mStation = station;
        }

        @Override
        public void callback(JSONArray result, int position) {
            // list of directions
            List<QueryItem> directions = new ArrayList<>();
            //===
            //=== parse JSON Array
            //===
            try {
                // check each timetable
                for (int i = 0; i < result.length(); i++) {
                    // get timetable
                    JSONObject timetableObject = result.getJSONObject(i);
                    if (timetableObject.isNull(KEY_DIRECTION) == false) {
                        // get direction
                        String directionForQuery = timetableObject.getString(KEY_DIRECTION);
                        // check whether the direction is already added
                        boolean isDuplicated = false;
                        for (QueryItem q : directions) {
                            if (q.getValueForQuery().equals(directionForQuery)) {
                                isDuplicated = true;
                            }
                        }
                        if (isDuplicated == false) {
                            String directionName = mStation.searchNameForDirection(directionForQuery);
                            //  add the direction if it has not been added
                            directions.add(new QueryItem(directionName, directionForQuery));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Station Fragment", "", e);
            }
            //===
            //=== update all items and phase of adapter
            //===
            // go to next phase
            AddStationActivity.mPhase = SELECT_DIRECTION;
            // show list
            mAdapter.setValues(directions);
            mAdapter.notifyDataSetChanged();
        }
    }

    // create list of Operators
    public List<QueryItem> createOperatorList() {
        List<QueryItem> values = new ArrayList<>();
        for (Common.Operator op : Common.Operator.values()) {
            if(op.getTypeOfTimetable() == REALTIME || op.getTypeOfTimetable() == STATIC) {
                values.add(new QueryItem(op.getName(), op.getNameForQuery()));
            }
        }
        return values;
    }


}
