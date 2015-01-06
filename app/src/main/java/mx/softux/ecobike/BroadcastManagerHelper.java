package mx.softux.ecobike;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

import mx.softux.ecobike.model.Model;
import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.model.StationModel;

/**
 * Created by gianpa on 12/30/14.
 */
public class BroadcastManagerHelper {
    public static enum BroadcastSource {
        NETWORK,
        CACHE
    }

    public static void sendStation(StationModel station, BroadcastSource source, LocalBroadcastManager broadcastManager) {
        Intent stationIntent = new Intent(Model.Station.READY);
        stationIntent.putExtra(P.BROADCAST_SOURCE, source);
        stationIntent.putExtra(P.Station.STATION, station);
        stationIntent.putExtra(P.Station.STATION_NUMBER, station.number);
        broadcastManager.sendBroadcast(stationIntent);
    }

    public static void sendStationList(StationList stationList, Integer requestId, BroadcastSource source, LocalBroadcastManager broadcastManager) {
        Intent stationIntent = new Intent(Model.StationList.READY);
        stationIntent.putExtra(P.BROADCAST_SOURCE, source);
        stationIntent.putExtra(P.NetwrokService.REQUEST_ID, requestId);
        stationIntent.putExtra(P.StationList.STATION_LIST, (Parcelable) stationList);
        broadcastManager.sendBroadcast(stationIntent);
    }

}
