package mx.softux.ecobike;

import mx.softux.ecobike.services.ApiService;

/**
 * Created by gianpa on 12/30/14.
 */
public interface ApiServiceConnection {
    public void onApiServiceConnected(ApiService apiService);
    public ApiService getApiService();
}
