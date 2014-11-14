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

import com.squareup.picasso.Picasso;

import org.paoloconte.smoothchart.SmoothLineChart;

public class StationFragment extends Fragment {
    public StationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);

        return view;
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_ITEMS = 2;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new MapFragment();
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
        public MapFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            Picasso.with(getActivity()).load("https://maps.googleapis.com/maps/api/staticmap?center=Berkeley,CA&zoom=14&size=400x200").into(imageView);
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
