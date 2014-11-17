package mx.softux.ecobike;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class NetworkService extends Service {
    public static final String RESPONSE = "RESPONSE";

    private RequestQueue queue;
    private ImageLoader imageLoader;

    private Map<Integer, Response> responses = new HashMap<Integer, Response>();
    private AtomicInteger sequence = new AtomicInteger();
    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = newRequestQueue(this);
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public ImageLoader getImageLoader() {
        if(imageLoader == null) {
            imageLoader = new ImageLoader(queue, new LruBitmapCache(LruBitmapCache.getCacheSize(this)));
        }
        return imageLoader;
    }

    public RequestQueue newRequestQueue(Context context) {
        return Volley.newRequestQueue(this);
    }

    public Integer requestGet(String url, JSONObject jsonRequest, final ResponseParcelable responseParcelable) {
        return request(Request.Method.GET, url, jsonRequest, responseParcelable);
    }

    public Integer request(int method, String url, JSONObject jsonRequest, final ResponseParcelable responseParcelable) {
        final Integer i = sequence.incrementAndGet();

        JsonObjectRequest request = new JsonObjectRequest(method, url, jsonRequest, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                responses.put(i, new Response(Response.OK, responseParcelable.newInstance(jsonObject)));
                notifyResponse(i);
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                responses.put(i, new Response(Response.ERROR));
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

    public static interface HostInterface {
        public NetworkService getNetworkService();
    }

    public static class Response {
        public static final int UNKNOWN = 0;
        public static final int OK = 1;
        public static final int ERROR = 2;

        private int status;
        private Parcelable parcelable;

        public Response(int status, Parcelable parcelable) {
            this.status = status;
            this.parcelable = parcelable;
        }

        public Response(int status) {
            this(status, null);
        }

        public Response() {
            this(UNKNOWN, null);
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Parcelable getParcelable() {
            return parcelable;
        }

        public void setParcelable(Parcelable parcelable) {
            this.parcelable = parcelable;
        }
    }

    protected interface ResponseParcelable {
        public Parcelable newInstance(JSONObject jsonObject);
    }

    private static class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

        public LruBitmapCache(int maxSize) {
            super(maxSize);
        }

        public LruBitmapCache(Context ctx) {
            this(getCacheSize(ctx));
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }

        // Returns a cache size equal to approximately three screens worth of images.
        public static int getCacheSize(Context ctx) {
            final DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
            final int screenWidth = displayMetrics.widthPixels;
            final int screenHeight = displayMetrics.heightPixels;
            // 4 bytes per pixel
            final int screenBytes = screenWidth * screenHeight * 4;

            return screenBytes * 3;
        }
    }
}
