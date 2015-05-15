package mx.softux.ecobike.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mx.softux.ecobike.ApiServiceConnection;
import mx.softux.ecobike.P;
import mx.softux.ecobike.R;
import mx.softux.ecobike.model.StationModel;
import mx.softux.ecobike.loader.StationLoader;
import mx.softux.ecobike.services.ApiService;

public class StationFragment extends Fragment implements LoaderManager.LoaderCallbacks<StationModel>, ApiServiceConnection {
    private ApiService apiService;
    private StationModel station;

    private TextView bikes;
    private TextView slots;
    private TextView updateTime;

    private Handler updateTimeHandler = new Handler();
    private Integer stationNumber = null;

    public static StationFragment newInstance(int stationNumber) {
        StationFragment fragment = new StationFragment();
        Bundle args = new Bundle();
        args.putInt(P.Station.STATION_NUMBER, stationNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public StationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            stationNumber = (Integer) savedInstanceState.get(P.Station.STATION_NUMBER);
        } else if (getArguments() != null) {
            stationNumber = (Integer) getArguments().get(P.Station.STATION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_station, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bikes = (TextView) view.findViewById(R.id.bikes_text_view);
        slots = (TextView) view.findViewById(R.id.slots_text_view);
        updateTime = (TextView) view.findViewById(R.id.update_time_text_view);

        updateView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateView();
    }

    public void updateView() {
        if (station != null) {
            hideProgressBar();

            bikes.setText(String.valueOf(station.bikes));
            slots.setText(String.valueOf(station.slots));

            updateTime.setText(DateUtils.getRelativeDateTimeString(getActivity(), station.updateTime, 0, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_SHOW_TIME));

            updateTimeHandler.removeCallbacks(updateTimeTask);
            updateTimeHandler.postDelayed(updateTimeTask, 1000);
        } else {
            showProgressbar();
        }
    }

    private void showProgressbar() {
        if (getView() == null) return;

        getView().findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.layout).setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        if (getView() == null) return;

        getView().findViewById(R.id.progress_bar).setVisibility(View.GONE);
        getView().findViewById(R.id.layout).setVisibility(View.VISIBLE);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (stationNumber != null)
            outState.putInt(P.Station.STATION_NUMBER, stationNumber);
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

    public void setStationNumber(int number) {
        stationNumber = number;
        getLoaderManager().restartLoader(0, null, this);
        updateView();
    }

    @Override
    public void onApiServiceConnected(ApiService apiService) {
        this.apiService = apiService;

        getLoaderManager().initLoader(0, getStationLoaderArguments(), this);
    }

    private Bundle getStationLoaderArguments() {
        Bundle args = new Bundle();
        if (stationNumber != null)
            args.putInt(P.Station.STATION_NUMBER, stationNumber);
        return args;
    }

    @Override
    public ApiService getApiService() {
        return apiService;
    }

    @Override
    public Loader<StationModel> onCreateLoader(int id, Bundle args) {
        if (!args.containsKey(P.Station.STATION_NUMBER))
            return null;

        return new StationLoader((Integer) args.get(P.Station.STATION_NUMBER), apiService, getActivity());
    }

    @Override
    public void onLoadFinished(Loader<StationModel> loader, StationModel data) {
        station = data;
        updateView();
    }

    @Override
    public void onLoaderReset(Loader<StationModel> loader) {
        station = null;
        updateView();
    }
}
