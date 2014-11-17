package mx.softux.ecobike;


import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;

import org.paoloconte.smoothchart.SmoothLineChart;

public class StationFragment extends Fragment {
    private StationModel station;
    private ViewPager viewPager;

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
        View view = inflater.inflate(R.layout.fragment_station, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateView();
    }

    public void updateView() {
        if (station != null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(station, getFragmentManager());
            viewPager.setAdapter(adapter);
        }
    }

    public void setStation(StationModel station) {
        this.station = station;
        updateView();
    }

    public StationModel getStation() {
        return station;
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_ITEMS = 2;
        private final StationModel station;

        public ViewPagerAdapter(StationModel station, FragmentManager fm) {
            super(fm);
            this.station = station;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return MapFragment.newInstance(station);
                case 1:
                    return new StationChartFragment();
            }

            return null;
        }

        @Override
        public float getPageWidth(int position) {
            return 0.7f;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }

    public static class MapFragment extends Fragment {
        private StationModel station;

        public static MapFragment newInstance(StationModel station) {
            MapFragment mapFragment = new MapFragment();
            Bundle args = new Bundle();
            args.putParcelable(P.Station.STATION, station);
            mapFragment.setArguments(args);
            return mapFragment;
        }

        public MapFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            station = getArguments().getParcelable(P.Station.STATION);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            NetworkImageView imageView = new NetworkImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            String mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=%1$f,%2$f&zoom=%3$d&size=%4$dx%5$d";
            mapUrl = String.format(mapUrl, station.location.x, station.location.y, 17, 800, 400);
            imageView.setImageUrl(mapUrl, ((NetworkService.HostInterface) getActivity()).getNetworkService().getImageLoader());
            return imageView;
        }
    }


    public static class StationChartFragment extends Fragment {
        public StationChartFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            SmoothLineChart chart = new SmoothLineChart(getActivity());
            chart.setData(new PointF[]{
                    new PointF(15, 39), // {x, y}
                    new PointF(20, 21),
                    new PointF(28, 9),
                    new PointF(37, 21),
                    new PointF(40, 25),
                    new PointF(50, 31),
                    new PointF(62, 24),
                    new PointF(80, 28)
            });

            return chart;
        }
    }
}
