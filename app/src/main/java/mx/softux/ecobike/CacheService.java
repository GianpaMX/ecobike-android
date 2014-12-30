package mx.softux.ecobike;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by gianpa on 12/29/14.
 */
public class CacheService extends Service {
    private static final String TAG = CacheService.class.getSimpleName();

    public static final String STATION_LIST_FILENAME = "station-list.json";

    private final IBinder binder = new Binder();
    private LocalBroadcastManager broadcastManager;
    private CacheUtilities cacheUtilities;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        cacheUtilities = new CacheUtilities(this);

        Log.d(TAG, "onCreate");
    }

    public void saveStationList(final StationList stationList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                File file = cacheUtilities.getCacheFile(STATION_LIST_FILENAME);
                cacheUtilities.writeJsonObject(file, stationList.toJSONObject());
                return null;
            }
        }.execute();

    }

    public void requestStationList(final Integer requestId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    File file = cacheUtilities.getCacheFile(STATION_LIST_FILENAME);

                    if (!file.exists()) return null;

                    JSONObject jsonObject = null;
                    jsonObject = cacheUtilities.readJsonObject(file);

                    StationList stationList = new StationList(jsonObject);
                    for (StationModel station : stationList) {
                        BroadcastManagerHelper.sendStation(station, BroadcastManagerHelper.BroadcastSource.CACHE, broadcastManager);
                    }
                    BroadcastManagerHelper.sendStationList(stationList, requestId, BroadcastManagerHelper.BroadcastSource.CACHE, broadcastManager);
                } catch (Exception e) {
                    Log.e(TAG, "requestStationList", e);
                    return null;
                }
                return null;
            }
        }.execute();
    }

    public class Binder extends android.os.Binder {
        public CacheService getCacheService() {
            return CacheService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
