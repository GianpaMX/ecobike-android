package mx.softux.ecobike;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by gianpa on 11/17/14.
 */
public class GcmIntentService extends IntentService {
    private static final String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService() {
        super(GcmIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR)) {
                Log.d(TAG, "MESSAGE_TYPE_SEND_ERROR");
            } else if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_DELETED)) {
                Log.d(TAG, "MESSAGE_TYPE_DELETED");
            } else if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
                if(extras.getString("collapse_key").equals("station")) {
                    Intent stationUpdateIntent = new Intent(Model.Station.UPDATE);

                    stationUpdateIntent.putExtra(P.Station.STATION_NUMBER, Integer.valueOf(extras.getString("number")));
                    stationUpdateIntent.putExtra(P.Station.STATION_BIKES, Integer.valueOf(extras.getString("bikes")));
                    stationUpdateIntent.putExtra(P.Station.STATION_SLOTS, Integer.valueOf(extras.getString("slots")));
                    stationUpdateIntent.putExtra(P.Station.STATION_UPDATE_TIME, Long.valueOf(extras.getString("updateTime")));

                    LocalBroadcastManager.getInstance(this).sendBroadcast(stationUpdateIntent);
                }
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
