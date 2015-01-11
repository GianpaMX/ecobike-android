package mx.softux.ecobike.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import mx.softux.ecobike.ApiServiceConnection;
import mx.softux.ecobike.P;
import mx.softux.ecobike.R;
import mx.softux.ecobike.StationListAdapter;
import mx.softux.ecobike.activities.StationsMapActivity;
import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.model.loader.ModelLoader;
import mx.softux.ecobike.model.loader.StationListLoader;
import mx.softux.ecobike.services.ApiService;

/**
 * Created by gianpa on 12/29/14.
 */
public class StationsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<StationList>, ApiServiceConnection {
    private ApiService apiService;
    private StationListAdapter adapter;

    public static StationsListFragment newInstance() {
        StationsListFragment fragment = new StationsListFragment();
        return fragment;
    }

    public StationsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        adapter = new StationListAdapter(getActivity());
        setListAdapter(adapter);
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent mapActivity = new Intent(getActivity(), StationsMapActivity.class);
        mapActivity.putExtra(P.Station.STATION_NUMBER, (int) id);
        startActivity(mapActivity);
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

        adapter.setStationList(data);
    }

    @Override
    public void onLoaderReset(Loader<StationList> loader) {
        adapter.setStationList(null);
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
