package mx.softux.ecobike;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class StationActivity extends ActionBarActivity implements NetworkService.HostInterface {
    private ApiService apiService = null;
    private LocalBroadcastManager broadcastManager = null;
    private StationModel station = null;
    private Integer stationRequestId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(P.Station.STATION)) {
                updateFragmentStation((StationModel) getIntent().getParcelableExtra(P.Station.STATION));
            }
        } else {
            if (savedInstanceState.containsKey(P.NetwrokService.REQUEST_ID)) {
                stationRequestId = savedInstanceState.getInt(P.NetwrokService.REQUEST_ID);
            } else {
                updateFragmentStation((StationModel) savedInstanceState.getParcelable(P.Station.STATION));
            }
        }

        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    private void updateFragmentStation(StationModel station) {
        this.station = station;

        setTitle(getString(R.string.station_activity_title, station.number));
        getSupportActionBar().setSubtitle(station.name);

        StationFragment stationFragment = (StationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_station);
        stationFragment.setStation(station);
    }

    @Override
    protected void onStart() {
        super.onStart();

        broadcastManager.registerReceiver(stationBroadcastReceiver, new IntentFilter(NetworkService.RESPONSE));

        Intent networkService = new Intent(this, ApiService.class);
        bindService(networkService, apiServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        broadcastManager.unregisterReceiver(stationBroadcastReceiver);

        if (apiService != null)
            unbindService(apiServiceConnection);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(stationRequestId != null) {
            outState.putInt(P.NetwrokService.REQUEST_ID, stationRequestId);
        } else {
            outState.putParcelable(P.Station.STATION, station);
        }
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

    private ServiceConnection apiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ApiService.Binder binder = (ApiService.Binder) service;
            apiService = binder.getApiService();

            if (station == null) {
                if (stationRequestId == null) {
                    int stationId = getIntent().getIntExtra(P.Station.STATION_ID, 0);
                    stationRequestId = apiService.requestStation(stationId);
                } else {
                    NetworkService.Response response = apiService.getResponse(stationRequestId);
                    onResponse(response);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            apiService = null;
        }
    };

    private BroadcastReceiver stationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra(P.NetwrokService.REQUEST_ID, 0) == stationRequestId) {
                onResponse(apiService.getResponse(stationRequestId));
            }
        }
    };

    private void onResponse(NetworkService.Response response) {
        if (response.getStatus() != NetworkService.Response.OK) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            return;
        }

        stationRequestId = null;
        updateFragmentStation((StationModel) response.getParcelable());
    }

    @Override
    public NetworkService getNetworkService() {
        return apiService;
    }
}
