package mx.softux.ecobike;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by gianpa on 11/14/14.
 */
public class StationModel extends Model {
    public Integer number;
    public String name;

    public StationModel() {
    }

    public StationModel(Parcel source) {
        number = source.readInt();
        name = source.readString();
    }

    public StationModel(JSONObject jsonObject) {
        number = (Integer) jsonObject.opt("number");
        name = (String) jsonObject.opt("name");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeString(name);
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
