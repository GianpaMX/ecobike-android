package mx.softux.ecobike.services.api;

import java.util.ArrayList;
import java.util.List;

import mx.softux.ecobike.services.NetworkService;
import mx.softux.ecobike.services.NetworkServiceInterface;

/**
 * Created by gianpa on 1/10/15.
 */
public class ApiRequestQueue {
    NetworkServiceInterface networkService;
    private List<ApiRequest> queue;

    public ApiRequestQueue(NetworkServiceInterface networkService) {
        queue = new ArrayList<ApiRequest>();
        this.networkService = networkService;
    }

    public ApiRequest request(ApiRequest request) {
        if (queue.contains(request))
            return queue.get(queue.indexOf(request));

        request.id = networkService.request(request.getMethod(), request.getUrl(), request.getJsonRequest(), request.getParcelable());
        queue.add(request);

        return request;
    }

    public ApiRequest onResponse(int requestId) {
        ApiRequest request = null;
        NetworkService.Response response = networkService.getResponse(requestId);

        for (int i = 0; i < queue.size(); i++) {
            request = queue.get(i);
            if (request.id == requestId) {
                queue.remove(i);
                break;
            }
        }

        request.response = response;

        return request;
    }
}
