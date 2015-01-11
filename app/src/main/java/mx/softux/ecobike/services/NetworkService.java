package mx.softux.ecobike.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import mx.softux.ecobike.P;
import mx.softux.ecobike.utilities.LogUtils;

public abstract class NetworkService extends Service implements NetworkServiceInterface {
    private static final String TAG = NetworkService.class.getSimpleName();

    protected static final String RESPONSE = "RESPONSE";

    private RequestQueue queue;

    private Map<Integer, Response> responses = new HashMap<Integer, Response>();
    private AtomicInteger sequence = new AtomicInteger();
    protected LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = newRequestQueue(this);
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    protected RequestQueue newRequestQueue(Context context) {
        return Volley.newRequestQueue(this);
    }

    protected Integer requestGet(String url, JSONObject jsonRequest, final ResponseParcelable responseParcelable) {
        return request(Request.Method.GET, url, jsonRequest, responseParcelable);
    }

    protected Integer requestPost(String url, JSONObject jsonObject, final ResponseParcelable responseParcelable) {
        return request(Request.Method.POST, url, jsonObject, responseParcelable);
    }

    @Override
    public Integer request(int method, String url, JSONObject jsonRequest, final ResponseParcelable responseParcelable) {
        final Integer i = sequence.incrementAndGet();

        JsonObjectRequest request = new JsonObjectRequest(method, url, jsonRequest, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                responses.put(i, new Response(Response.Status.OK, responseParcelable.newInstance(jsonObject)));
                notifyResponse(i);
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.LOGE(TAG, "onErrorResponse", volleyError);
                responses.put(i, new Response(Response.Status.ERROR, null, volleyError));
                notifyResponse(i);
            }
        });
        request.setTag(i);

        queue.add(request);
        return i;
    }

    private void notifyResponse(Integer i) {
        Intent intent = new Intent();
        intent.setAction(RESPONSE);
        intent.putExtra(P.NetwrokService.REQUEST_ID, i);
        broadcastManager.sendBroadcast(intent);
    }

    public Response getResponse(Integer requestId) {
        if (!responses.containsKey(requestId)) {
            return new Response();
        }
        Response response = responses.get(requestId);
        responses.remove(requestId);
        return response;
    }

    public void cancelRequest(int requestId) {
        queue.cancelAll(requestId);
    }

    public static class Response implements Parcelable {
        public static enum Status {
            UNKNOWN,
            OK,
            ERROR
        }

        public Status status;
        public Parcelable parcelable;
        public Exception error;

        public Response(Status status, Parcelable parcelable, Exception error) {
            this.status = status;
            this.parcelable = parcelable;
            this.error = error;
        }

        public Response(Status status, Parcelable parcelable) {
            this(status, parcelable, null);
        }

        public Response(Status status) {
            this(status, null);
        }

        public Response() {
            this(Status.UNKNOWN, null);
        }

        public Response(Parcel source) {
            status = (Status) source.readSerializable();
            parcelable = source.readParcelable(null);
            error = (Exception) source.readSerializable();
        }

        public boolean isOk() {
            return status == Status.OK;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSerializable(status);
            dest.writeParcelable(parcelable, flags);
            dest.writeSerializable(error);
        }
    }

    public static interface ResponseParcelable {
        public Parcelable newInstance(JSONObject jsonObject);
    }
}
