package mx.softux.ecobike.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import mx.softux.ecobike.utilities.LogUtils;

public class StationsMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<StationList>, ApiServiceConnection, OnMapReadyCallback {
    private static final String TAG = StationsMapFragment.class.getSimpleName();

    private ApiService apiService;
    private GoogleMap map;
    private StationList stationList;
    private HashMap<Integer, Marker> markers;
    private Integer selectedStationNumber;

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

        Double minSWLat, minSWLng;
        Double maxNELat, maxNELng;

        minSWLat = minSWLng = maxNELat = maxNELng = null;

        StationModel selectedStation = null;
        for (StationModel station : stationList) {
            Marker marker;
            if (markers.containsKey(station.number)) {
                marker = markers.get(station.number);

                marker.setPosition(new LatLng(station.location.x, station.location.y));
                marker.setTitle(station.name);
            } else {
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(new LatLng(station.location.x, station.location.y));
                markerOptions.title(station.name);

                marker = map.addMarker(markerOptions);

                markers.put(station.number, marker);
            }

            try {
                minSWLat = marker.getPosition().latitude < minSWLat ? marker.getPosition().latitude : minSWLat;
                minSWLng = marker.getPosition().longitude < minSWLng ? marker.getPosition().longitude : minSWLng;

                maxNELat = marker.getPosition().latitude > maxNELat ? marker.getPosition().latitude : maxNELng;
                minSWLng = marker.getPosition().longitude < minSWLng ? marker.getPosition().longitude : minSWLng;
            } catch (NullPointerException e) {
                LogUtils.LOGD(TAG, "first marker", e);

                minSWLat = marker.getPosition().latitude;
                maxNELat = marker.getPosition().latitude;

                minSWLng = marker.getPosition().longitude;
                maxNELng = marker.getPosition().longitude;
            }

            if (selectedStationNumber == station.number)
                selectedStation = station;
        }

        if (selectedStation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(selectedStation.location.x, selectedStation.location.y), 13));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLngBounds(new LatLng(minSWLat, minSWLng), new LatLng(maxNELat, maxNELng)).getCenter(), 17));
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

    public void setSelectedStationNumber(Integer stationNumber) {
        this.selectedStationNumber = stationNumber;
    }
}
