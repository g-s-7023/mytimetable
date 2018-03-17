package com.androidapp.g_s_org.mytimetable.adapter;

        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;


        import com.androidapp.g_s_org.mytimetable.container.QueryItem;
        import com.androidapp.g_s_org.mytimetable.R;

        import java.util.List;


//===
//=== adapter for the list to add stationItem
//===
public class AddStationRecyclerViewAdapter extends RecyclerView.Adapter<AddStationRecyclerViewAdapter.ViewHolder> {

    private  List<QueryItem> mValues;
    private  OnAddItemSelectedListener mListener;

    public AddStationRecyclerViewAdapter(List<QueryItem> values, OnAddItemSelectedListener listener) {
        mValues = values;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stationaddlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mValueView.setText(mValues.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAddItemSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setValues(List<QueryItem> values){
        mValues = values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mValueView;
        public QueryItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mValueView = (TextView) view.findViewById(R.id.addListValue);
        }
    }

    public interface OnAddItemSelectedListener {
        void onAddItemSelected(QueryItem item);
    }
}
