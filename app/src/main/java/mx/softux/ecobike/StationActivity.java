package mx.softux.ecobike;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class StationActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<StationModel> {
    private StationModel station = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent gcmRegisterService = new Intent(this, GcmRegisterIntentService.class);
        startService(gcmRegisterService);

        getLoaderManager().initLoader(0, null, this);
    }

    private void updateFragmentStation(StationModel station) {
        this.station = station;

        if (station != null) {
            setTitle(getString(R.string.station_activity_title, station.number));
            getSupportActionBar().setSubtitle(station.name);
        } else {
            setTitle("");
            getSupportActionBar().setSubtitle("");
        }

        StationFragment stationFragment = (StationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_station);
        if (stationFragment != null) stationFragment.setStation(station);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_station, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<StationModel> onCreateLoader(int id, Bundle args) {
        return new StationLoader(this, getIntent().getIntExtra(P.Station.STATION_NUMBER, 0));
    }

    @Override
    public void onLoadFinished(Loader<StationModel> loader, StationModel data) {
        updateFragmentStation(data);
    }

    @Override
    public void onLoaderReset(Loader<StationModel> loader) {
        updateFragmentStation(null);
    }
}
