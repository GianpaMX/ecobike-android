package mx.softux.ecobike;


import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.paoloconte.smoothchart.SmoothLineChart;

public class StationFragment extends Fragment {
    private StationModel station;
    private SmoothLineChart bikesLineChart;
    private NetworkImageView mapImageView;
    private TextView bikes;
    private TextView slots;

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

        bikesLineChart = (SmoothLineChart) view.findViewById(R.id.bikes_line_chart);
        mapImageView = (NetworkImageView) view.findViewById(R.id.map_image_view);
        bikes = (TextView) view.findViewById(R.id.bikes_text_view);
        slots = (TextView) view.findViewById(R.id.slots_text_view);

        updateView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateView();
    }

    public void updateView() {
        if (station != null && ((NetworkService.HostInterface) getActivity()).getNetworkService() != null) {
            String mapUrl = "https://maps.googleapis.com/maps/api/staticmap?&markers=color:red|%1$f,%2$f&zoom=%3$d&size=%4$dx%5$d";
            mapUrl = String.format(mapUrl, station.location.x, station.location.y, 18, 400, 200);

            mapImageView.setImageUrl(mapUrl, ((NetworkService.HostInterface) getActivity()).getNetworkService().getImageLoader());

            bikes.setText(String.valueOf(station.bikes));
            slots.setText(String.valueOf(station.slots));

            bikesLineChart.setData(new PointF[]{
                    new PointF(15, 39), // {x, y}
                    new PointF(20, 21),
                    new PointF(28, 9),
                    new PointF(37, 21),
                    new PointF(40, 25),
                    new PointF(50, 31),
                    new PointF(62, 24),
                    new PointF(80, 28)
            });
        }
    }

    public void setStation(StationModel station) {
        this.station = station;
        updateView();
    }
}
