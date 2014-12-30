package mx.softux.ecobike;

import android.os.Parcelable;

/**
 * Created by gianpa on 11/14/14.
 */
public abstract class Model implements Parcelable {
    public static class Station {
        public static final String READY = "STATION_READY";
        public static final String UPDATE = "STATION_UPDATE";
    }

    public class StationList {
        public static final String READY = "STATION_LIST_READY";
    }
}
