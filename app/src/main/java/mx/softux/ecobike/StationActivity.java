package mx.softux.ecobike;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class StationActivity extends ActionBarActivity implements StationFragment.StationFragmentHostActivity, LoaderManager.LoaderCallbacks<StationModel> {
    private ApiService apiService = null;
    private ServiceConnection apiServiceConnection;
    private LocalBroadcastManager broadcastManager;
    private Integer requestId;
    private BroadcastReceiver requestStationMonitorReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent gcmRegisterService = new Intent(this, GcmRegisterIntentService.class);
        startService(gcmRegisterService);

        broadcastManager = LocalBroadcastManager.getInstance(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(P.NetwrokService.REQUEST_ID)) {
                requestId = savedInstanceState.getInt(P.NetwrokService.REQUEST_ID);
                onResponse(apiService.getResponse(requestId));
            }
        }

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (requestStationMonitorReceiver != null) {
            broadcastManager.registerReceiver(requestStationMonitorReceiver, new IntentFilter(NetworkService.RESPONSE));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (requestId != null) {
            outState.putInt(P.NetwrokService.REQUEST_ID, requestId);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(apiService != null) {
            unbindService(apiServiceConnection);
        }

        if (requestStationMonitorReceiver != null) {
            broadcastManager.unregisterReceiver(requestStationMonitorReceiver);
        }
    }

    private void updateFragmentStation(StationModel station) {
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
        return new StationLoader(this, getIntent().getIntExtra(P.Station.STATION_NUMBER, 1));
    }

    @Override
    public void onLoadFinished(Loader<StationModel> loader, StationModel data) {
        updateFragmentStation(data);
    }

    @Override
    public void onLoaderReset(Loader<StationModel> loader) {
        updateFragmentStation(null);
    }

    @Override
    public void requestStationMonitor(final StationModel station) {
        final String registrationId = GcmRegisterIntentService.getRegistrationId(this);
        if (registrationId == null) {
            StationFragment stationFragment = (StationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_station);
            stationFragment.onMonitorRequestResponse(false, "No registration id");
            return;
        }

        broadcastManager.registerReceiver(requestStationMonitorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (requestId != null && intent.getIntExtra(P.NetwrokService.REQUEST_ID, 0) == requestId) {
                    NetworkService.Response response = apiService.getResponse(requestId);

                    broadcastManager.unregisterReceiver(requestStationMonitorReceiver);
                    requestStationMonitorReceiver = null;

                    onResponse(response);
                }
            }
        }, new IntentFilter(NetworkService.RESPONSE));

        Intent apiServiceIntent = new Intent(this, ApiService.class);
        bindService(apiServiceIntent, apiServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ApiService.Binder binder = (ApiService.Binder) service;
                apiService = binder.getApiService();
                requestId = apiService.requestStationMonitor(station, registrationId);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                apiService = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void onResponse(NetworkService.Response response) {
        if(response.getStatus() == NetworkService.Response.UNKNOWN)
            return;

        requestId = null;

        StationFragment stationFragment = (StationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_station);
        if (response.getStatus() != NetworkService.Response.OK) {
            stationFragment.onMonitorRequestResponse(false, "Error requesting monitor");
            return;
        }

        stationFragment.onMonitorRequestResponse(true, "OK");
    }
}
