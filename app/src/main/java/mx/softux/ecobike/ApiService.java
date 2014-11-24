package mx.softux.ecobike;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gianpa on 11/15/14.
 */
public class ApiService extends NetworkService {
    private static final String TAG = ApiService.class.getSimpleName();

    private static final String API_URL = "http://192.168.0.3:3000";
    private static final int SECOND = 1000;
    private static final int TIMEOUT = 5 * SECOND;

    private Handler idleHandler = null;
    private Runnable idleCountertask = null;

    private final IBinder binder = new Binder();

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
                case STATION:
                    Intent stationIntent = new Intent(Model.Station.READY);
                    StationModel station = (StationModel) response.getParcelable();
                    stationIntent.putExtra(P.Station.STATION, station);
                    stationIntent.putExtra(P.Station.STATION_NUMBER, station.number);
                    broadcastManager.sendBroadcast(stationIntent);
                    break;
                default:
                    // Ignore
            }

            requestTypes.remove(requestId);
        }
    };

    public Integer requestStation(int number) {
        broadcastManager.registerReceiver(responseReceiver, new IntentFilter(NetworkService.RESPONSE));
        Integer requestId = requestGet(API_URL + "/station", null, new ResponseParcelable() {
            @Override
            public Parcelable newInstance(JSONObject jsonObject) {
                return new StationModel(jsonObject);
            }
        });
        requestTypes.put(requestId, RequestType.STATION);
        return requestId;
    }

    public Integer requestStationMonitor(StationModel station, String regId) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("regId", regId);
        } catch (JSONException e) {
            Log.e(TAG, "jsonObject.put");
            return null;
        }

        String path = String.format("/station/%1$d/monitor", station.number);
        return requestPost(API_URL + path, jsonObject, new ResponseParcelable() {
            @Override
            public Parcelable newInstance(JSONObject jsonObject) {
                return null;
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (idleCountertask != null) {
            idleHandler.removeCallbacks(idleCountertask);
            idleCountertask = null;
        }
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (idleCountertask != null) {
            throw new IllegalStateException("idleCountertask should be null");
        }
        idleHandler = new Handler();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (idleHandler == null) return false;

        idleCountertask = new Runnable() {
            int timeout = TIMEOUT;

            @Override
            public void run() {
                idleHandler.postDelayed(this, SECOND);
                Log.d(TAG, "time = " + timeout);

                if ((timeout -= 1 * SECOND) <= 0) stopSelf();
            }
        };

        idleHandler.postDelayed(idleCountertask, SECOND);

        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        idleHandler.removeCallbacks(idleCountertask);
    }

    public class Binder extends android.os.Binder {
        public ApiService getApiService() {
            return ApiService.this;
        }
    }

    public static boolean isRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ApiService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static enum RequestType {
        STATION
    }
}
