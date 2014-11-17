package mx.softux.ecobike;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public StationModel() {
    }

    public StationModel(Parcel source) {
        number = source.readInt();
        name = source.readString();
        location = source.readParcelable(PointF.class.getClassLoader());
        bikes = source.readInt();
        slots = source.readInt();
        stats = (Stat[]) source.readArray(Stat.class.getClassLoader());
    }

    public StationModel(JSONObject jsonObject) {
        number = (Integer) jsonObject.opt("number");
        name = (String) jsonObject.opt("name");
        try {
            double x = jsonObject.getJSONObject("location").optDouble("latitude");
            double y = jsonObject.getJSONObject("location").optDouble("longitude");
            location = new PointF((float) x, (float) y);
        } catch (JSONException e) {
            Log.e(TAG, "location", e);
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
            Log.e(TAG, "stats", e);
        }
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
            time = (String) jsonObject.opt("name");
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
