package mx.softux.ecobike;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by gianpa on 11/17/14.
 */
public class StationLoader extends Loader<StationModel> {
    private ApiService apiService = null;
    private LocalBroadcastManager broadcastManager = null;
    private StationModel station = null;
    private final int number;
    private Integer requestId;

    public StationLoader(Context context, int number) {
        super(context);

        this.number = number;

        if (!ApiService.isRunning(context)) {
            Intent apiService = new Intent(context, ApiService.class);
            context.startService(apiService);
        }
    }

    @Override
    protected void onStartLoading() {
        if(station != null) {
            deliverResult(station);
        }

        broadcastManager = LocalBroadcastManager.getInstance(getContext());

        broadcastManager.registerReceiver(stationBroadcastReceiver, new IntentFilter(Model.Station.READY));
        broadcastManager.registerReceiver(stationBroadcastReceiver, new IntentFilter(Model.Station.UPDATE));

        Intent networkService = new Intent(getContext(), ApiService.class);
        getContext().bindService(networkService, apiServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onReset() {
        broadcastManager.unregisterReceiver(stationBroadcastReceiver);

        cancelRequest();

        if (apiService != null)
            getContext().unbindService(apiServiceConnection);

        broadcastManager = null;
        station = null;
    }

    private void cancelRequest() {
        if (apiService != null && requestId != null) {
            apiService.cancelRequest(requestId);
            requestId = null;
        }
    }

    @Override
    protected void onForceLoad() {
        cancelRequest();
        if (apiService != null)
            requestId = apiService.requestStation(number);
    }

    private BroadcastReceiver stationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestId = null;

            if (isAbandoned() || number != intent.getExtras().getInt(P.Station.STATION_NUMBER)) {
                return;
            }

            if (intent.getAction() == Model.Station.READY) {
                station = intent.getExtras().getParcelable(P.Station.STATION);
            } else if (intent.getAction() == Model.Station.UPDATE && station != null) {
                station = new StationModel(station);
                station.bikes = intent.getExtras().getInt(P.Station.STATION_BIKES);
                station.slots = intent.getExtras().getInt(P.Station.STATION_SLOTS);
                station.updateTime = intent.getExtras().getLong(P.Station.STATION_UPDATE_TIME);
            } else {
                return;
            }

            if (isStarted() && station != null) deliverResult(station);
        }
    };

    private ServiceConnection apiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ApiService.Binder binder = (ApiService.Binder) service;
            apiService = binder.getApiService();

            if(station == null)
                requestId = apiService.requestStation(number);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            apiService = null;
        }
    };
}
