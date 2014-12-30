package mx.softux.ecobike;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.android.gms.maps.SupportMapFragment;

public class StationsMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<StationList> {
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

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<StationList> onCreateLoader(int id, Bundle args) {
        return new StationListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<StationList> loader, StationList data) {

    }

    @Override
    public void onLoaderReset(Loader<StationList> loader) {

    }
}
