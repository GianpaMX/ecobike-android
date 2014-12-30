package mx.softux.ecobike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by gianpa on 12/29/14.
 */
public class StationListAdapter extends BaseAdapter {
    private Context context;
    private StationList stationList;
    private LayoutInflater layoutInflater;

    public StationListAdapter(Context context) {
        this(new StationList(), context);
    }

    public StationListAdapter(StationList stationList, Context context) {
        this.context = context;
        this.stationList = new StationList(stationList);
    }

    @Override
    public int getCount() {
        return stationList.size();
    }

    @Override
    public Object getItem(int position) {
        return stationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return stationList.get(position).number;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            if (layoutInflater == null)
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        StationModel station = stationList.get(position);

        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        text1.setText(station.name);

        return view;
    }

    public StationList getStationList() {
        return stationList;
    }

    public void setStationList(StationList newStationList) {
        stationList.clear();
        if (newStationList != null) stationList.addAll(newStationList);
        notifyDataSetChanged();
    }
}
