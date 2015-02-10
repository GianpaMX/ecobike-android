package mx.softux.ecobike.model;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.softux.ecobike.utilities.LogUtils;

/**
 * Created by gianpa on 11/14/14.
 */
public class StationModel extends Model {
    private static final String TAG = StationModel.class.getSimpleName();

    public Integer number;
    public String name;
    public PointF location;
    public Integer bikes;
    public Integer slots;
    public Stat[] stats;
    public Long updateTime;

    public StationModel() {
    }

    public StationModel(Parcel source) {
        number = source.readInt();
        name = source.readString();
        location = source.readParcelable(PointF.class.getClassLoader());
        bikes = source.readInt();
        slots = source.readInt();
        stats = (Stat[]) source.readArray(Stat.class.getClassLoader());
        updateTime = source.readLong();
    }

    public StationModel(JSONObject jsonObject) {
        copyFrom(jsonObject);
    }

    public StationModel(StationModel station) {
        copyFrom(station);
    }

    private void copyFrom(StationModel station) {
        number = station.number;
        name = station.name;
        location = station.location;
        bikes = station.bikes;
        slots = station.slots;
        if (station.stats != null) {
            stats = new Stat[station.stats.length];
            for (int i = 0; i < station.stats.length; i++) {
                stats[i] = station.stats[i];
            }
        }
        updateTime = station.updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Integer) {
            return number == (Integer) o;
        }

        return super.equals(o);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeString(name);
        dest.writeParcelable(location, flags);
        dest.writeInt(bikes);
        dest.writeInt(slots);
        dest.writeArray(stats);
        dest.writeLong(updateTime);
    }

    public static final Parcelable.Creator<StationModel> CREATOR = new Parcelable.Creator<StationModel>() {
        @Override
        public StationModel createFromParcel(Parcel source) {
            return new StationModel(source);
        }

        @Override
        public StationModel[] newArray(int size) {
            return new StationModel[size];
        }
    };

    @Override
    public void copyFrom(JSONObject jsonObject) {
        number = (Integer) jsonObject.opt("number");
        name = (String) jsonObject.opt("name");
        try {
            double x = jsonObject.getJSONObject("location").optDouble("latitude");
            double y = jsonObject.getJSONObject("location").optDouble("longitude");
            location = new PointF((float) x, (float) y);
        } catch (JSONException e) {
            LogUtils.LOGE(TAG, "location", e);
        }
        bikes = (Integer) jsonObject.opt("bikes");
        slots = (Integer) jsonObject.opt("slots");
        try {
            JSONArray statsJSONArray = jsonObject.getJSONArray("stats");
            stats = new Stat[statsJSONArray.length()];
            for (int i = 0; i < statsJSONArray.length(); i++) {
                stats[i] = new Stat(statsJSONArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            LogUtils.LOGE(TAG, "stats", e);
        }
        updateTime = (Long) jsonObject.opt("updateTime");
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("number", number);
            jsonObject.put("name", name);

            JSONObject locationJsonObject = new JSONObject();
            locationJsonObject.put("latitude", location.x);
            locationJsonObject.put("longitude", location.y);
            jsonObject.put("location", locationJsonObject);

            jsonObject.put("bikes", bikes);
            jsonObject.put("slots", slots);

            JSONArray statsJsonArray = new JSONArray();
            for(Stat stat : stats) {
                JSONObject statJsonObject = new JSONObject();
                statJsonObject.put("time", stat.time);
                statJsonObject.put("bikes", stat.bikes);
                statJsonObject.put("slots", stat.slots);
                statsJsonArray.put(statJsonObject);
            }
            jsonObject.put("stats", statsJsonArray);

            jsonObject.put("updateTime", updateTime);
        } catch (JSONException e) {
            LogUtils.LOGE(TAG, "JSONObject.put", e);

            return null;
        }

        return jsonObject;
    }

    public static class Stat implements Parcelable {
        public String time;
        public Integer bikes;
        public Integer slots;

        public Stat() {
        }

        public Stat(Parcel source) {
            time = source.readString();
            bikes = source.readInt();
            slots = source.readInt();
        }

        public Stat(JSONObject jsonObject) {
            time = (String) jsonObject.opt("time");
            bikes = (Integer) jsonObject.opt("bikes");
            slots = (Integer) jsonObject.opt("slots");
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(time);
            dest.writeInt(bikes);
            dest.writeInt(slots);
        }

        public static final Parcelable.Creator<Stat> CREATOR = new Parcelable.Creator<Stat>() {
            @Override
            public Stat createFromParcel(Parcel source) {
                return new Stat(source);
            }

            @Override
            public Stat[] newArray(int size) {
                return new Stat[size];
            }
        };
    }
}
