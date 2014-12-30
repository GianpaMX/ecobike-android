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
 * Created by gianpa on 12/26/14.
 */
public abstract class ModelLoader<D> extends Loader<D> {
    private LocalBroadcastManager broadcastManager = null;
    protected ApiService apiService = null;

    public ModelLoader(Context context) {
        super(context);


        if (!ApiService.isRunning(context)) {
            Intent apiService = new Intent(context, ApiService.class);
            context.startService(apiService);
        }
    }

    public void registerReceiver(BroadcastReceiver receiver, String action) {
        broadcastManager.registerReceiver(receiver, new IntentFilter(action));
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        broadcastManager.unregisterReceiver(receiver);
    }

    /**
     * Call this method before using broadcastManager
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        broadcastManager = LocalBroadcastManager.getInstance(getContext());


        Intent networkService = new Intent(getContext(), ApiService.class);
        getContext().bindService(networkService, apiServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Call after using broadcastManager
     */
    @Override
    protected void onReset() {
        super.onReset();
        if (apiService != null)
            getContext().unbindService(apiServiceConnection);

        broadcastManager = null;
    }

    protected void cancelRequest(Integer requestId) {
        if (apiService != null && requestId != null) {
            apiService.cancelRequest(requestId);
            requestId = null;
        }
    }

    private ServiceConnection apiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ApiService.Binder binder = (ApiService.Binder) service;
            apiService = binder.getApiService();

            ModelLoader.this.onServiceConnected(apiService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            apiService = null;
        }
    };

    protected abstract void onServiceConnected(ApiService apiService);
}
