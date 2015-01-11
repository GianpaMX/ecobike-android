package mx.softux.ecobike.services.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Request;

import org.json.JSONObject;

import mx.softux.ecobike.model.StationModel;
import mx.softux.ecobike.services.NetworkService;

/**
 * Created by gianpa on 1/10/15.
 */
public class StationApiRequest extends ApiRequest {
    public int number;

    public StationApiRequest(Parcel parcel) {
        number = parcel.readInt();
    }

    public StationApiRequest(int number) {
        this.number = number;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getUrl() {
        return String.format("%s/station/%d", API_URL, number);
    }

    @Override
    public JSONObject getJsonRequest() {
        return null;
    }

    @Override
    public NetworkService.ResponseParcelable getParcelable() {
        return new NetworkService.ResponseParcelable() {
            @Override
            public Parcelable newInstance(JSONObject jsonObject) {
                return new StationModel(jsonObject);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationApiRequest that = (StationApiRequest) o;

        if (number != that.number) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
    }
}
