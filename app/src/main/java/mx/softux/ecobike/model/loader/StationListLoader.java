package mx.softux.ecobike.model.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mx.softux.ecobike.P;
import mx.softux.ecobike.model.Model;
import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.services.ApiService;
import mx.softux.ecobike.services.api.ApiRequest;

/**
 * Created by gianpa on 12/26/14.
 */
public class StationListLoader extends ModelLoader<StationList> {
    private StationList stationList = null;
    private ApiRequest request;

    public StationListLoader(ApiService apiService, Context context) {
        super(apiService, context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (stationList == null) {
            request = apiService.requestStationList();
        } else {
            deliverResult(stationList);
        }

        registerReceiver(stationListBroadcastReceiver, Model.StationList.READY);
    }

    @Override
    protected void onReset() {
        unregisterReceiver(stationListBroadcastReceiver);
        cancelRequest(request);

        stationList = null;

        super.onReset();
    }

    @Override
    protected void onForceLoad() {
        cancelRequest(request);

        if (apiService != null)
            request = apiService.requestStationList();

        super.onForceLoad();
    }

    private BroadcastReceiver stationListBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAbandoned() || request == null || request.id != intent.getExtras().getInt(P.NetwrokService.REQUEST_ID)) {
                return;
            }

            stationList = intent.getExtras().getParcelable(P.StationList.STATION_LIST);

            if (isStarted() && stationList != null) deliverResult(stationList);
        }
    };
}
