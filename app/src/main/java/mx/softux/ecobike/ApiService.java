package mx.softux.ecobike;

import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by gianpa on 11/15/14.
 */
public class ApiService extends NetworkService {
    private final IBinder binder = new Binder();


    public Integer requestStation(int stationId) {
        return requestGet("url", null, new ResponseParcelable() {
            @Override
            public Parcelable newInstance(JSONObject jsonObject) {
                return new StationModel(jsonObject);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class Binder extends android.os.Binder {
        public ApiService getApiService() {
            return ApiService.this;
        }
    }
}
