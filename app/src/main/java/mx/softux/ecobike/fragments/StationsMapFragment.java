package mx.softux.ecobike.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import mx.softux.ecobike.ApiServiceConnection;
import mx.softux.ecobike.R;
import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.model.StationModel;
import mx.softux.ecobike.model.loader.ModelLoader;
import mx.softux.ecobike.model.loader.StationListLoader;
import mx.softux.ecobike.services.ApiService;

public class StationsMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<StationList>, ApiServiceConnection, OnMapReadyCallback {
    private ApiService apiService;
    private GoogleMap map;
    private StationList stationList;
    private HashMap<Integer, Marker> markers;

    public static StationsMapFragment newInstance() {
        StationsMapFragment fragment = new StationsMapFragment();
        return fragment;
    }

    public StationsMapFragment() {
        // Required empty public constructor
        super();

        markers = new HashMap<Integer, Marker>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        getMapAsync(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stations, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getLoaderManager().getLoader(0).forceLoad();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<StationList> onCreateLoader(int id, Bundle args) {
        return new StationListLoader(apiService, getActivity());
    }

    @Override
    public void onLoadFinished(Loader<StationList> loader, StationList data) {
        ModelLoader stationListLoader = (ModelLoader) loader;
        if (data == null && stationListLoader.getError() != null) {
            Toast.makeText(getActivity(), getString(R.string.request_error), Toast.LENGTH_LONG).show();
            return;
        }

        stationList = data;
        updateMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        updateMap();
    }

    private void updateMap() {
        if (map == null || stationList == null) return;

        for (StationModel station : stationList) {
            if (markers.containsKey(station.number)) {
                Marker marker = markers.get(station.number);

                marker.setPosition(new LatLng(station.location.x, station.location.y));
                marker.setTitle(station.name);
            } else {
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(new LatLng(station.location.x, station.location.y));
                markerOptions.title(station.name);

                markers.put(station.number, map.addMarker(markerOptions));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<StationList> loader) {

    }

    @Override
    public void onApiServiceConnected(ApiService apiService) {
        this.apiService = apiService;
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public ApiService getApiService() {
        return apiService;
    }
}
