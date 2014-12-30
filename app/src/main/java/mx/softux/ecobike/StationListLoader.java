package mx.softux.ecobike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by gianpa on 12/26/14.
 */
public class StationListLoader extends ModelLoader<StationList> {
    private StationList stationList = null;
    private Integer requestId;

    public StationListLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if(stationList != null) {
            deliverResult(stationList);
        }

        registerReceiver(stationListBroadcastReceiver, Model.StationList.READY);
    }

    @Override
    protected void onServiceConnected(ApiService apiService) {
        if(stationList == null)
            requestId = apiService.requestStationList();
    }

    @Override
    protected void onReset() {
        unregisterReceiver(stationListBroadcastReceiver);
        cancelRequest(requestId);

        stationList = null;

        super.onReset();
    }

    @Override
    protected void onForceLoad() {
        cancelRequest(requestId);

        if (apiService != null)
            requestId = apiService.requestStationList();

        super.onForceLoad();
    }

    private BroadcastReceiver stationListBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAbandoned() || requestId == null || requestId != intent.getExtras().getInt(P.NetwrokService.REQUEST_ID)) {
                return;
            }

            stationList = intent.getExtras().getParcelable(P.StationList.STATION_LIST);

            if (isStarted() && stationList != null) deliverResult(stationList);
        }
    };
}
