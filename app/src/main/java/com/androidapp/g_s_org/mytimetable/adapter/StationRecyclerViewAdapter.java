package com.androidapp.g_s_org.mytimetable.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.androidapp.g_s_org.mytimetable.R;
import com.androidapp.g_s_org.mytimetable.common.Common;
import com.androidapp.g_s_org.mytimetable.container.StationItem;
import com.androidapp.g_s_org.mytimetable.container.TrainItem;

import java.util.ArrayList;

import static com.androidapp.g_s_org.mytimetable.common.Common.SUFFIX_DIRECTION;
import static com.androidapp.g_s_org.mytimetable.common.Common.SUFFIX_LINE;
import static com.androidapp.g_s_org.mytimetable.common.Common.SUFFIX_ROTATEDIRECTION;
import static com.androidapp.g_s_org.mytimetable.common.Common.SUFFIX_SEN;
import static com.androidapp.g_s_org.mytimetable.common.Common.Operator;


public class StationRecyclerViewAdapter extends RecyclerView.Adapter<StationRecyclerViewAdapter.ViewHolder> {
    private ArrayList<StationItem> mStations;
    private onItemLongClickListener mLongListener;

    public StationRecyclerViewAdapter() {
    }

    public StationRecyclerViewAdapter(ArrayList<StationItem> items) {
        mStations = items;
    }

    public void setOnItemLongClickListener(onItemLongClickListener listener) {
        mLongListener = listener;
    }

    public void setStations(ArrayList<StationItem> stations) {
        mStations = stations;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_station, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        StationItem station = mStations.get(position);
        // name of the line
        String lineName = station.getLine();
        // add suffix if necessary and set line name
        if (lineName.length() > 1) {
            if (lineName.contains(SUFFIX_SEN) == false) {
                if (lineName.length() >= 3) {
                    if (lineName.substring(lineName.length() - 3, lineName.length()).equals(SUFFIX_LINE) == false) {
                        holder.lineView.setText(lineName + SUFFIX_SEN);
                    } else {
                        // end with "ライン"
                        holder.lineView.setText(lineName);
                    }
                } else {
                    holder.lineView.setText(lineName + SUFFIX_SEN);
                }
            } else {
                // contain "線"
                holder.lineView.setText(lineName);
            }
        } else {
            holder.lineView.setText(lineName + SUFFIX_SEN);
        }
        // set station name
        holder.stationView.setText(" " + station.getStationName());
        // name of the direction
        String directionName = station.getDirection();
        // add suffix if necessary and set direction name
        if (directionName.length() > 2) {
            if (directionName.substring(directionName.length() - 2, directionName.length()).equals(SUFFIX_DIRECTION) == false) {
                if (directionName.substring(directionName.length() - 2, directionName.length()).equals(SUFFIX_ROTATEDIRECTION) == false) {
                    holder.directionView.setText(directionName + SUFFIX_DIRECTION);
                } else {
                    // end with "回り"
                    holder.directionView.setText(directionName);
                }
            } else {
                // end with "方面"
                holder.directionView.setText(directionName);
            }
        } else {
            holder.directionView.setText(directionName + SUFFIX_DIRECTION);
        }
        // 1st train
        TrainItem train = station.getTrainItem(0);
        Operator operator = station.getOperator();
        String trainType = "";
        String trainDestination = "";
        if (train != null) {
            trainType = train.getTrainTypeName(operator);
            holder.trainType1View.setText(trainType.length() < 13 ? trainType : trainType.substring(0, 12));
            trainDestination = station.searchNameOfStation(train.getDestination().getValueForQuery());
            if (trainDestination.equals("")) {
                trainDestination = train.getDestination().getName();
            }
            holder.destination1View.setText(trainDestination.length() < 13 ? trainDestination : trainDestination.substring(0, 12));
            holder.time1View.setText(train.getDelay().equals("") ? train.getTimeToDepart() : train.getTimeToDepart() + " + " + train.getDelay());
        } else {
            // prevent adapter from setting the previous row's data
            holder.trainType1View.setText("");
            holder.destination1View.setText("");
            holder.time1View.setText("");
        }
        // 2nd train
        train = station.getTrainItem(1);
        if (train != null) {
            trainType = train.getTrainTypeName(operator);
            holder.trainType2View.setText(trainType.length() < 13 ? trainType : trainType.substring(0, 12));
            trainDestination = station.searchNameOfStation(train.getDestination().getValueForQuery());
            if (trainDestination.equals("")) {
                trainDestination = train.getDestination().getName();
            }
            holder.destination2View.setText(trainDestination.length() < 13 ? trainDestination : trainDestination.substring(0, 12));
            holder.time2View.setText(train.getDelay().equals("") ? train.getTimeToDepart() : train.getTimeToDepart() + " + " + train.getDelay());
        } else {
            // prevent adapter from setting the previous row's data
            holder.trainType2View.setText("");
            holder.destination2View.setText("");
            holder.time2View.setText("");
        }
        // 3rd train
        train = station.getTrainItem(2);
        if (train != null) {
            trainType = train.getTrainTypeName(operator);
            holder.trainType3View.setText(trainType.length() < 13 ? trainType : trainType.substring(0, 12));
            trainDestination = station.searchNameOfStation(train.getDestination().getValueForQuery());
            if (trainDestination.equals("")) {
                trainDestination = train.getDestination().getName();
            }
            holder.destination3View.setText(trainDestination.length() < 13 ? trainDestination : trainDestination.substring(0, 12));
            holder.time3View.setText(train.getDelay().equals("") ? train.getTimeToDepart() : train.getTimeToDepart() + " + " + train.getDelay());
        } else {
            // prevent adapter from setting the previous row's data
            holder.trainType3View.setText("");
            holder.destination3View.setText("");
            holder.time3View.setText("");
        }
        // when a row is longclicked
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mLongListener.onLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });
    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mStations != null) {
            return mStations.size();
        }
        return 0;
    }

    public StationItem getItem(int position) {
        if (mStations != null) {
            return mStations.get(position);
        }
        return null;
    }

    public ArrayList<StationItem> getStations() {
        return mStations;
    }

    public interface onItemLongClickListener {
        void onLongClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        // header
        public final TextView lineView;
        public final TextView stationView;
        public final TextView directionView;
        // train1
        public final TextView trainType1View;
        public final TextView destination1View;
        public final TextView time1View;
        // train2
        public final TextView trainType2View;
        public final TextView destination2View;
        public final TextView time2View;
        // train3
        public final TextView trainType3View;
        public final TextView destination3View;
        public final TextView time3View;
        /*
        // train4
        public final TextView trainType4View;
        public final TextView destination4View;
        public final TextView time4View;
        */
        public StationItem station;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            lineView = (TextView) view.findViewById(R.id.tv_line);
            stationView = (TextView) view.findViewById(R.id.tv_station);
            directionView = (TextView) view.findViewById(R.id.tv_direction);
            trainType1View = (TextView) view.findViewById(R.id.tv_trainType1);
            destination1View = (TextView) view.findViewById(R.id.tv_destination1);
            time1View = (TextView) view.findViewById(R.id.tv_time1);
            trainType2View = (TextView) view.findViewById(R.id.tv_trainType2);
            destination2View = (TextView) view.findViewById(R.id.tv_destination2);
            time2View = (TextView) view.findViewById(R.id.tv_time2);
            trainType3View = (TextView) view.findViewById(R.id.tv_trainType3);
            destination3View = (TextView) view.findViewById(R.id.tv_destination3);
            time3View = (TextView) view.findViewById(R.id.tv_time3);
            /*
            trainType4View = (TextView) view.findViewById(R.id.tv_trainType4);
            destination4View = (TextView) view.findViewById(R.id.tv_destination4);
            time4View = (TextView) view.findViewById(R.id.tv_time4);
            */
        }
    }


}

