package mx.softux.ecobike.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mx.softux.ecobike.utilities.LogUtils;

/**
 * Created by gianpa on 12/26/14.
 */
public class StationList extends ModelList<StationModel> implements Parcelable {
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
        copyFrom(jsonObject);
    }

    /**
     * Searches this {@code ArrayList} for the specified object.
     *
     * @param object the object to search for.
     * @return {@code true} if {@code object} is an element of this
     * {@code ArrayList}, {@code false} otherwise
     */
    @Override
    public boolean contains(Object object) {
        if (!(object instanceof Integer))
            return super.contains(object);

        for (int i = 0; i < size(); i++) {
            return get(i).equals(object);
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this);
    }

    @Override
    public void copyFrom(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("stations");
            for (int i = 0; i < jsonArray.length(); i++) {
                this.add(new StationModel(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            LogUtils.LOGE(TAG, "stations", e);
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray stationsJsonArray = new JSONArray();
            for (StationModel station : this) {
                stationsJsonArray.put(station.toJSONObject());
            }
            jsonObject.put("stations", stationsJsonArray);
        } catch (JSONException e) {
            LogUtils.LOGE(TAG, "JSONObject.put", e);

            return null;
        }

        return jsonObject;
    }
}
