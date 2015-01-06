package mx.softux.ecobike.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mx.softux.ecobike.BroadcastManagerHelper;
import mx.softux.ecobike.P;
import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.model.StationModel;
import mx.softux.ecobike.utilities.Timer;

/**
 * Created by gianpa on 11/15/14.
 */
public class ApiService extends NetworkService {
    private static final String TAG = ApiService.class.getSimpleName();

    private static final String API_URL = "http://192.168.56.1:3000";

    private Timer stopSelfTimer = null;

    private final IBinder binder = new Binder();

    private CacheService cacheService;

    private Map<Integer, RequestType> requestTypes = new HashMap<Integer, RequestType>();
    private BroadcastReceiver responseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int requestId = intent.getIntExtra(P.NetwrokService.REQUEST_ID, 0);

            if (!requestTypes.containsKey(requestId)) {
                return;
            }

            NetworkService.Response response = getResponse(requestId);
            if (response.getStatus() != NetworkService.Response.OK) {
                return;
            }

            switch (requestTypes.get(requestId)) {
                case STATION: {
                    StationModel station = (StationModel) response.getParcelable();
                    BroadcastManagerHelper.sendStation(station, BroadcastManagerHelper.BroadcastSource.NETWORK, broadcastManager);
                }
                break;
                case STATION_LIST: {
                    StationList stationList = (StationList) response.getParcelable();
                    if (cacheService != null)
                        cacheService.saveStationList(stationList);
                    for (StationModel station : stationList) {
                        BroadcastManagerHelper.sendStation(station, BroadcastManagerHelper.BroadcastSource.NETWORK, broadcastManager);
                    }
                    BroadcastManagerHelper.sendStationList(stationList, requestId, BroadcastManagerHelper.BroadcastSource.NETWORK, broadcastManager);
                }
                break;
                default:
                    // Ignore
            }

            requestTypes.remove(requestId);
        }
    };

    public Integer requestStation(int number) {
        String url = String.format("%s/station/%d", API_URL, number);

        broadcastManager.registerReceiver(responseReceiver, new IntentFilter(NetworkService.RESPONSE));
        Integer requestId = requestGet(url, null, new ResponseParcelable() {
            @Override
            public Parcelable newInstance(JSONObject jsonObject) {
                return new StationModel(jsonObject);
            }
        });
        requestTypes.put(requestId, RequestType.STATION);
        return requestId;
    }

    public Integer requestStationMonitor(StationModel station, String regId) {
        String url = String.format("%s/station/%1$d/monitor", API_URL, station.number);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("regId", regId);
        } catch (JSONException e) {
            Log.e(TAG, "jsonObject.put");
            return null;
        }

        return requestPost(url, jsonObject, new ResponseParcelable() {
            @Override
            public Parcelable newInstance(JSONObject jsonObject) {
                return null;
            }
        });
    }

    public Integer requestStationList() {
        String url = String.format("%s/station", API_URL);

        broadcastManager.registerReceiver(responseReceiver, new IntentFilter(NetworkService.RESPONSE));
        Integer requestId = requestGet(url, null, new ResponseParcelable() {
            @Override
            public Parcelable newInstance(JSONObject jsonObject) {
                return new StationList(jsonObject);
            }
        });

        if (cacheService != null)
            cacheService.requestStationList(requestId);

        requestTypes.put(requestId, RequestType.STATION_LIST);
        return requestId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        Intent cacheServiceIntent = new Intent(this, CacheService.class);
        bindService(cacheServiceIntent, cacheServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(stopSelfTimer != null) {
            stopSelfTimer.destroy();
        }

        if (cacheService != null) unbindService(cacheServiceConnection);

        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(stopSelfTimer == null) stopSelfTimer = new Timer();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(stopSelfTimer == null) return false;

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

    private static enum RequestType {
        STATION,
        STATION_LIST
    }

    private ServiceConnection cacheServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CacheService.Binder binder = (CacheService.Binder) service;
            cacheService = binder.getCacheService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            cacheService = null;
        }
    };
}
