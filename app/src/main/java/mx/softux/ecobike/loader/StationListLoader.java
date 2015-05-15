package mx.softux.ecobike.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import mx.softux.ecobike.BroadcastManagerHelper;
import mx.softux.ecobike.P;
import mx.softux.ecobike.model.Model;
import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.services.ApiService;
import mx.softux.ecobike.services.api.ApiRequest;
import mx.softux.ecobike.services.api.StationListApiRequest;

/**
 * Created by gianpa on 12/26/14.
 */
public class StationListLoader extends ModelLoader<StationList> {
    private StationList stationList = null;
    private ApiRequest request;
    private Exception error;

    public StationListLoader(ApiService apiService, Context context) {
        super(apiService, context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (stationList == null) {
            request = apiService.request(new StationListApiRequest());
        } else {
            deliverResult(stationList);
        }

        registerReceiver(stationListBroadcastReceiver, Model.StationList.READY);
        registerReceiver(errorBroadcastReceiver, Model.ERROR);
    }

    @Override
    protected void onReset() {
        unregisterReceiver(stationListBroadcastReceiver);
        unregisterReceiver(errorBroadcastReceiver);
        cancelRequest(request);

        stationList = null;

        super.onReset();
    }

    @Override
    protected void onForceLoad() {
        cancelRequest(request);

        if (apiService != null)
            request = apiService.request(new StationListApiRequest());

        super.onForceLoad();
    }

    private BroadcastReceiver stationListBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAbandoned() || request == null || request.id != intent.getExtras().getInt(P.NetwrokService.REQUEST_ID)) {
                return;
            }

            if (intent.getExtras().get(P.BROADCAST_SOURCE) == BroadcastManagerHelper.BroadcastSource.NETWORK) {
                request = null;
                error = null;
            }

            stationList = intent.getExtras().getParcelable(P.StationList.STATION_LIST);

            if (isStarted() && stationList != null) deliverResult(stationList);
        }
    };

    private BroadcastReceiver errorBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAbandoned() || request == null) {
                return;
            }

            Parcelable request = intent.getExtras().getParcelable(P.ApiService.REQUEST);
            if (request instanceof StationListApiRequest) {
                error = ((StationListApiRequest) request).response.error;

                if (isStarted())
                    deliverResult(null);
            }
        }
    };

    public Exception getError() {
        return error;
    }
}
