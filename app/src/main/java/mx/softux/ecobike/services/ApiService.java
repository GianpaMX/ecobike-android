package mx.softux.ecobike.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import mx.softux.ecobike.BroadcastManagerHelper;
import mx.softux.ecobike.P;
import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.model.StationModel;
import mx.softux.ecobike.services.api.ApiRequest;
import mx.softux.ecobike.services.api.ApiRequestPool;
import mx.softux.ecobike.services.api.StationApiRequest;
import mx.softux.ecobike.services.api.StationListApiRequest;
import mx.softux.ecobike.utilities.LogUtils;
import mx.softux.ecobike.utilities.Timer;

/**
 * Created by gianpa on 11/15/14.
 */
public class ApiService extends NetworkService {
    private static final String TAG = ApiService.class.getSimpleName();

    private Timer stopSelfTimer = null;
    private final IBinder binder = new Binder();

    private CacheService cacheService;

    private ApiRequestPool apiRequestPool;

    private BroadcastReceiver responseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ApiRequest apiRequest = apiRequestPool.retriveRequest((Integer) intent.getExtras().get(P.NetwrokService.REQUEST_ID));
            if (apiRequest == null) {
                return;
            }

            Response response = apiRequest.response;

            if (apiRequest instanceof StationApiRequest && response.isOk()) {
                StationModel station = (StationModel) response.getParcelable();
                BroadcastManagerHelper.sendStation(station, BroadcastManagerHelper.BroadcastSource.NETWORK, broadcastManager);
            } else if (apiRequest instanceof StationListApiRequest && response.isOk()) {
                StationList stationList = (StationList) response.getParcelable();
                if (cacheService != null)
                    cacheService.saveStationList(stationList);
                for (StationModel station : stationList) {
                    BroadcastManagerHelper.sendStation(station, BroadcastManagerHelper.BroadcastSource.NETWORK, broadcastManager);
                }
                BroadcastManagerHelper.sendStationList(stationList, apiRequest.id, BroadcastManagerHelper.BroadcastSource.NETWORK, broadcastManager);
            } else {

            }
        }
    };

    public ApiRequest request(ApiRequest apiRequest) {
        return apiRequestPool.request(apiRequest);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.LOGD(TAG, "onCreate");

        Intent cacheServiceIntent = new Intent(this, CacheService.class);
        bindService(cacheServiceIntent, cacheServiceConnection, Context.BIND_AUTO_CREATE);

        broadcastManager.registerReceiver(responseReceiver, new IntentFilter(NetworkService.RESPONSE));

        apiRequestPool = new ApiRequestPool(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (stopSelfTimer != null) {
            stopSelfTimer.destroy();
        }

        if (cacheService != null) unbindService(cacheServiceConnection);

        unregisterReceiver(responseReceiver);

        LogUtils.LOGD(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (stopSelfTimer == null) stopSelfTimer = new Timer();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (stopSelfTimer == null) return false;

        stopSelfTimer.setStop(new Timer.Stop() {
            @Override
            public void onStop() {
                stopSelf();
            }
        });

        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        stopSelfTimer.cancel();
    }

    public class Binder extends android.os.Binder {
        public ApiService getApiService() {
            return ApiService.this;
        }
    }

    private ServiceConnection cacheServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CacheService.Binder binder = (CacheService.Binder) service;
            cacheService = binder.getCacheService();

            apiRequestPool.setCacheService(cacheService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            cacheService = null;
            apiRequestPool.setCacheService(null);
        }
    };
}
