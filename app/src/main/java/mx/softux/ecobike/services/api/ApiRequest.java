package mx.softux.ecobike.services.api;

import org.json.JSONObject;

import mx.softux.ecobike.services.NetworkService;

/**
 * Created by gianpa on 1/10/15.
 */
public abstract class ApiRequest {
    public static final String API_URL = "http://192.168.56.1:3000";

    public Integer id;
    public NetworkService.Response response;

    public abstract int getMethod();

    public abstract String getUrl();

    public abstract JSONObject getJsonRequest();

    public abstract NetworkService.ResponseParcelable getParcelable();
}
