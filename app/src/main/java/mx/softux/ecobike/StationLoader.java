package mx.softux.ecobike;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

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

        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        broadcastManager.registerReceiver(stationBroadcastReceiver, new IntentFilter(NetworkService.RESPONSE));

        Intent networkService = new Intent(getContext(), ApiService.class);
        getContext().bindService(networkService, apiServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        cancelLoad();
    }

    @Override
    protected boolean onCancelLoad() {
        if (requestId == null)
            return false;

        apiService.cancelRequest(requestId);
        requestId = null;

        return true;
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        broadcastManager.unregisterReceiver(stationBroadcastReceiver);

        cancelLoad();

        if (apiService != null)
            getContext().unbindService(apiServiceConnection);
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        cancelLoad();
        requestId = apiService.requestStation(number);
    }

    private BroadcastReceiver stationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (requestId != null && intent.getIntExtra(P.NetwrokService.REQUEST_ID, 0) == requestId) {
                if (isAbandoned()) return;

                NetworkService.Response response = apiService.getResponse(requestId);
                if (response.getStatus() != NetworkService.Response.OK) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                    return;
                }
                station = (StationModel) response.getParcelable();

                requestId = null;
                deliverResult(station);
            }
        }
    };

    private ServiceConnection apiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ApiService.Binder binder = (ApiService.Binder) service;
            apiService = binder.getApiService();
            requestId = apiService.requestStation(number);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            apiService = null;
        }
    };

    @Override
    protected void onReset() {
        super.onReset();
        broadcastManager = null;
        station = null;
    }
}
