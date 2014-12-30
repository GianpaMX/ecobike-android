package mx.softux.ecobike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by gianpa on 11/17/14.
 */
public class StationLoader extends ModelLoader<StationModel> {
    private StationModel station = null;
    private final int number;
    private Integer requestId;

    public StationLoader(Context context, int number) {
        super(context);

        this.number = number;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if(station != null) {
            deliverResult(station);
        }

        registerReceiver(stationBroadcastReceiver, Model.Station.READY);
        registerReceiver(stationBroadcastReceiver, Model.Station.UPDATE);
    }

    @Override
    protected void onServiceConnected(ApiService apiService) {
        if(station == null)
            requestId = apiService.requestStation(number);
    }

    @Override
    protected void onReset() {
        unregisterReceiver(stationBroadcastReceiver);
        cancelRequest(requestId);

        station = null;

        super.onReset();
    }

    @Override
    protected void onForceLoad() {
        cancelRequest(requestId);

        if (apiService != null)
            requestId = apiService.requestStation(number);

        super.onForceLoad();
    }

    private BroadcastReceiver stationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestId = null;

            if (isAbandoned() || number != intent.getExtras().getInt(P.Station.STATION_NUMBER)) {
                return;
            }

            if (intent.getAction() == Model.Station.READY) {
                station = intent.getExtras().getParcelable(P.Station.STATION);
            } else if (intent.getAction() == Model.Station.UPDATE && station != null) {
                station = new StationModel(station);
                station.bikes = intent.getExtras().getInt(P.Station.STATION_BIKES);
                station.slots = intent.getExtras().getInt(P.Station.STATION_SLOTS);
                station.updateTime = intent.getExtras().getLong(P.Station.STATION_UPDATE_TIME);
            } else {
                return;
            }

            if (isStarted() && station != null) deliverResult(station);
        }
    };

}
