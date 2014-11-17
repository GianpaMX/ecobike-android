package mx.softux.ecobike;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by gianpa on 11/15/14.
 */
public class ApiService extends NetworkService {
    private static final String TAG = ApiService.class.getSimpleName();

    private static final int SECOND = 1000;
    private static final int TIMEOUT = 5 * SECOND;

    private Handler idleHandler = null;
    private Runnable idleCountertask = null;

    private final IBinder binder = new Binder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(idleCountertask != null) {
            idleHandler.removeCallbacks(idleCountertask);
            idleCountertask = null;
        }
        Log.d(TAG, "onDestroy");
    }

    public Integer requestStation(int number) {
        return requestGet("http://192.168.56.1:3000/station", null, new ResponseParcelable() {
            @Override
            public Parcelable newInstance(JSONObject jsonObject) {
                return new StationModel(jsonObject);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(idleCountertask != null) {
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
        if(idleHandler == null) return false;

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
}
