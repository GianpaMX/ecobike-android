package mx.softux.ecobike.services.api;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import mx.softux.ecobike.services.NetworkServiceInterface;
import mx.softux.ecobike.utilities.LogUtils;

/**
 * Created by gianpa on 1/10/15.
 */
public class ApiRequestQueue {
    private static final long TIMEOUT = 10 * 1000;
    private static final String TAG = ApiRequestQueue.class.getSimpleName();

    NetworkServiceInterface networkService;
    private List<ApiRequest> queue;

    public ApiRequestQueue(NetworkServiceInterface networkService) {
        queue = new ArrayList<ApiRequest>();
        this.networkService = networkService;
    }

    public ApiRequest request(final ApiRequest request) {
        if (queue.contains(request))
            return queue.get(queue.indexOf(request));

        request.id = networkService.request(request.getMethod(), request.getUrl(), request.getJsonRequest(), request.getParcelable());
        queue.add(request);

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

    public ApiRequest findRequest(int requestId) {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).id == requestId) {
                ApiRequest request = queue.get(i);

                queue.remove(i);

                request.response = networkService.getResponse(requestId);
                return request;
            }
        }

        return null;
    }
}
