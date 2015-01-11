package mx.softux.ecobike.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import mx.softux.ecobike.ApiServiceConnection;
import mx.softux.ecobike.R;
import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.model.loader.ModelLoader;
import mx.softux.ecobike.model.loader.StationListLoader;
import mx.softux.ecobike.services.ApiService;

public class StationsMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<StationList>, ApiServiceConnection {
    private ApiService apiService;

    public static StationsMapFragment newInstance() {
        StationsMapFragment fragment = new StationsMapFragment();
        return fragment;
    }

    public StationsMapFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
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
