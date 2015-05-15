package mx.softux.ecobike.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mx.softux.ecobike.P;
import mx.softux.ecobike.model.Model;
import mx.softux.ecobike.model.StationModel;
import mx.softux.ecobike.services.ApiService;
import mx.softux.ecobike.services.api.ApiRequest;
import mx.softux.ecobike.services.api.StationApiRequest;

/**
 * Created by gianpa on 11/17/14.
 */
public class StationLoader extends ModelLoader<StationModel> {
    private StationModel station = null;
    private final int number;
    private ApiRequest request;

    public StationLoader(int number, ApiService apiService, Context context) {
        super(apiService, context);

        this.number = number;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (station != null) {
            deliverResult(station);
        } else {
            request = apiService.request(new StationApiRequest(number));
        }

        registerReceiver(stationBroadcastReceiver, Model.Station.READY);
        registerReceiver(stationBroadcastReceiver, Model.Station.UPDATE);
    }

    @Override
    protected void onReset() {
        unregisterReceiver(stationBroadcastReceiver);
        cancelRequest(request);

        station = null;

        super.onReset();
    }

    @Override
    protected void onForceLoad() {
        cancelRequest(request);

        if (apiService != null)
            request = apiService.request(new StationApiRequest(number));

        super.onForceLoad();
    }

    private BroadcastReceiver stationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            request = null;

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
