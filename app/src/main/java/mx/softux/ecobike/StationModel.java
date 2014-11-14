package mx.softux.ecobike;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by gianpa on 11/14/14.
 */
public class StationModel extends Model {
    private Integer number;

    public StationModel() {
    }

    public StationModel(Parcel source) {
        number = source.readInt();
    }

    public StationModel(JSONObject jsonObject) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
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
