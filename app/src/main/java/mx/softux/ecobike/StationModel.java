package mx.softux.ecobike;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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

    public StationModel() {
    }

    public StationModel(Parcel source) {
        number = source.readInt();
        name = source.readString();
        location = source.readParcelable(PointF.class.getClassLoader());
        bikes = source.readInt();
        slots = source.readInt();
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
}
