package mx.softux.ecobike.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import mx.softux.ecobike.P;
import mx.softux.ecobike.R;
import mx.softux.ecobike.StatsLineChart;
import mx.softux.ecobike.model.StationModel;

public class StationFragment extends Fragment {
    private StationModel station;
    private StatsLineChart bikesLineChart;
    private ImageView mapImageView;
    private TextView bikes;
    private TextView slots;
    private TextView updateTime;

    private Handler updateTimeHandler = new Handler();
    private Button requestMonitorButton;

    public void onMonitorRequestResponse(boolean ok, String text) {
        if(ok) {
            Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
        }
    }

    public static interface StationFragmentHostActivity {
        public void requestStationMonitor(StationModel station);
    }

    public StationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            station = savedInstanceState.getParcelable(P.Station.STATION);
        } else if (getArguments() != null && getArguments().containsKey(P.Station.STATION)) {
            station = getArguments().getParcelable(P.Station.STATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_station, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bikesLineChart = (StatsLineChart) view.findViewById(R.id.bikes_line_chart);
        mapImageView = (ImageView) view.findViewById(R.id.map_image_view);
        bikes = (TextView) view.findViewById(R.id.bikes_text_view);
        slots = (TextView) view.findViewById(R.id.slots_text_view);
        updateTime = (TextView) view.findViewById(R.id.update_time_text_view);

        requestMonitorButton = (Button) view.findViewById(R.id.request_monitor_button);

        updateView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateView();
    }

    public void updateView() {
        if (station != null) {
            getView().findViewById(R.id.progress_bar).setVisibility(View.GONE);
            getView().findViewById(R.id.layout).setVisibility(View.VISIBLE);

            String mapUrl = "https://maps.googleapis.com/maps/api/staticmap?&markers=color:red|%1$f,%2$f&zoom=%3$d&size=%4$dx%5$d";
            mapUrl = String.format(mapUrl, station.location.x, station.location.y, 18, 400, 200).replace("|", "%7C");

            Picasso.with(getActivity()).load(mapUrl).into(mapImageView);

            bikes.setText(String.valueOf(station.bikes));
            slots.setText(String.valueOf(station.slots));

            bikesLineChart.setData(station.stats);

            updateTime.setText(DateUtils.getRelativeDateTimeString(getActivity(), station.updateTime, 0, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_SHOW_TIME));

            updateTimeHandler.removeCallbacks(updateTimeTask);
            updateTimeHandler.postDelayed(updateTimeTask, 1000);

            requestMonitorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getActivity() instanceof StationFragmentHostActivity) {
                        ((StationFragmentHostActivity) getActivity()).requestStationMonitor(station);
                    }
                }
            });
        } else {
            getView().findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (station != null) {
            updateTimeHandler.postDelayed(updateTimeTask, 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (updateTimeHandler != null) {
            updateTimeHandler.removeCallbacks(updateTimeTask);
        }
    }

    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (station != null) {
                updateTime.setText(DateUtils.getRelativeDateTimeString(getActivity(), station.updateTime, 0, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_SHOW_TIME));
                updateTimeHandler.postDelayed(this, 1000);
            }
        }
    };

    public void setStation(StationModel station) {
        this.station = station;
        updateView();
    }
}
