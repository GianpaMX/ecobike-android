package mx.softux.ecobike.services.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Request;

import org.json.JSONObject;

import mx.softux.ecobike.model.StationList;
import mx.softux.ecobike.services.NetworkService;

/**
 * Created by gianpa on 1/10/15.
 */
public class StationListApiRequest extends ApiRequest {
    public StationListApiRequest() {
    }

    public StationListApiRequest(Parcel parcel) {
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getUrl() {
        return String.format("%s/station", API_URL);
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
                return new StationList(jsonObject);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 13;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
