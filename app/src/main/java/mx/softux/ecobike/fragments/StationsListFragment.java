package mx.softux.ecobike.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import mx.softux.ecobike.ApiServiceConnection;
import mx.softux.ecobike.StationListAdapter;
import mx.softux.ecobike.model.StationList;
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

        adapter = new StationListAdapter(getActivity());
        setListAdapter(adapter);
    }

    @Override
    public Loader<StationList> onCreateLoader(int id, Bundle args) {
        return new StationListLoader(apiService, getActivity());
    }

    @Override
    public void onLoadFinished(Loader<StationList> loader, StationList data) {
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
