package mx.softux.ecobike;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gianpa on 12/26/14.
 */
public class StationList extends ArrayList<StationModel> implements Parcelable {
    private static final String TAG = StationList.class.getSimpleName();

    public StationList() {
    }

    public StationList(StationList stationList) {
        super(stationList);
    }

    public StationList(Parcel parcel) {
        parcel.readTypedList(this, StationModel.CREATOR);
    }

    public StationList(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("stations");
            for (int i = 0; i < jsonArray.length(); i++) {
                this.add(new StationModel(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "stations", e);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<StationList> CREATOR = new Parcelable.Creator<StationList>() {
        @Override
        public StationList createFromParcel(Parcel source) {
            return new StationList(source);
        }

        @Override
        public StationList[] newArray(int size) {
            return new StationList[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this);
    }
}
