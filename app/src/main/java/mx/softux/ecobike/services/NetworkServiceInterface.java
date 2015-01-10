package mx.softux.ecobike.services;

import org.json.JSONObject;

/**
 * Created by gianpa on 1/10/15.
 */
public interface NetworkServiceInterface {
    public Integer request(int method, String url, JSONObject jsonRequest, final NetworkService.ResponseParcelable responseParcelable);
    public NetworkService.Response getResponse(Integer requestId);
}
