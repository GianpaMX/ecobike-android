package mx.softux.ecobike.services.api;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import mx.softux.ecobike.services.CacheService;
import mx.softux.ecobike.services.NetworkServiceInterface;
import mx.softux.ecobike.utilities.LogUtils;

/**
 * Created by gianpa on 1/10/15.
 */
public class ApiRequestPool {
    private static final long TIMEOUT = 10 * 1000;
    private static final String TAG = ApiRequestPool.class.getSimpleName();

    NetworkServiceInterface networkService;
    private CacheService cacheService;

    private List<ApiRequest> queue;

    public ApiRequestPool(NetworkServiceInterface networkService) {
        queue = new ArrayList<ApiRequest>();
        this.networkService = networkService;
    }

    public ApiRequest request(final ApiRequest request) {
        if (queue.contains(request)) {
            ApiRequest existingRequest = queue.get(queue.indexOf(request));

            if (cacheService != null)
                cacheService.request(existingRequest);

            return existingRequest;
        }

        request.id = networkService.request(request.getMethod(), request.getUrl(), request.getJsonRequest(), request.getParcelable());
        queue.add(request);

        if (cacheService != null)
            cacheService.request(request);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (queue.contains(request)) {
                    LogUtils.LOGD(TAG, "Request " + request.id + " Timeout");
                    queue.remove(request);
                }
            }
        }, TIMEOUT);

        return request;
    }

    public void cancel(ApiRequest apiRequest) {
        queue.remove(apiRequest);
    }

    public ApiRequest retriveRequest(Integer requestId) {
        if (requestId == null) return null;

        int index = indexOf(requestId);
        if (index == -1) return null;

        ApiRequest request = queue.get(index);
        queue.remove(index);

        request.response = networkService.getResponse(requestId);

        return request;
    }

    private int indexOf(int requestId) {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).id == requestId) {
                return i;
            }
        }
        return -1;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }
}
