package mx.softux.ecobike.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONObject;

import java.io.File;

import mx.softux.ecobike.BroadcastManagerHelper;
import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.model.StationModel;
import mx.softux.ecobike.services.api.ApiRequest;
import mx.softux.ecobike.services.api.StationListApiRequest;
import mx.softux.ecobike.utilities.CacheUtilities;
import mx.softux.ecobike.utilities.LogUtils;

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

        LogUtils.LOGD(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.LOGD(TAG, "onDestroy");
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

    private void requestStationList(final Integer requestId) {
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
                    LogUtils.LOGE(TAG, "requestStationList", e);
                    return null;
                }
                return null;
            }
        }.execute();
    }

    public void request(ApiRequest apiRequest) {
        if (apiRequest instanceof StationListApiRequest)
            requestStationList(apiRequest.id);
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

    @Override
    public boolean onUnbind(Intent intent) {
        stopSelf();
        return false;
    }
}
