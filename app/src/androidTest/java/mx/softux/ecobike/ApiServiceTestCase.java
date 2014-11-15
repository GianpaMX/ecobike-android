package mx.softux.ecobike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ServiceTestCase;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.concurrent.TimeoutException;

/**
 * Created by gianpa on 11/15/14.
 */
public class ApiServiceTestCase extends ServiceTestCase<MockApiService> {
    private static final int SECOND = 1000;

    private ApiService apiService;
    private LocalBroadcastManager broadcastManager;

    public ApiServiceTestCase() {
        super(MockApiService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        broadcastManager = LocalBroadcastManager.getInstance(getContext());

        Intent mockApiService = new Intent(getContext(), MockApiService.class);
        MockApiService.Binder binder = (MockApiService.Binder) bindService(mockApiService);

        apiService = binder.getApiService();
    }

    private static class Receiver extends BroadcastReceiver {
        public Object lock = new Object();
        public boolean received = false;
        public OnResponseListener onResponseListener = null;

        public static interface OnResponseListener {
            public void onResponse(int requestId);
            public NetworkService.Response getResponse();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (lock) {
                if(onResponseListener != null) {
                    onResponseListener.onResponse(intent.getIntExtra(P.NetwrokService.REQUEST_ID, 0));
                }

                received = true;
                lock.notify();
            }
        }

        public void waitUpTo(int timeout) throws InterruptedException, TimeoutException {
            synchronized (lock) {
                while (!received) {
                    lock.wait(1 * SECOND);
                    if ((timeout -= 1 * SECOND) <= 0) {
                        throw new TimeoutException("Timeout");
                    }
                }
            }
        }
    }

    public void testRequest() throws InterruptedException, TimeoutException {
        Receiver receiver = new Receiver();
        broadcastManager.registerReceiver(receiver, new IntentFilter(NetworkService.RESPONSE));

        Integer id = apiService.request(Request.Method.GET, "", null, new NetworkService.ResponseParcelable() {
            @Override
            public Parcelable newInstance(JSONObject jsonObject) {
                return new Parcelable() {
                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {
                    }
                };
            }
        });

        receiver.waitUpTo(10 * SECOND);

        broadcastManager.unregisterReceiver(receiver);
    }

    public void testStationRequest() throws InterruptedException, TimeoutException {
        Receiver receiver = new Receiver();
        broadcastManager.registerReceiver(receiver, new IntentFilter(NetworkService.RESPONSE));

        Integer id = apiService.requestStation(1);
        receiver.onResponseListener = new Receiver.OnResponseListener() {
            private NetworkService.Response response;

            @Override
            public void onResponse(int requestId) {
                response = apiService.getResponse(requestId);
            }

            @Override
            public NetworkService.Response getResponse() {
                return response;
            }
        };
        receiver.waitUpTo(10 * SECOND);

        assert(receiver.onResponseListener.getResponse().getParcelable() instanceof StationModel);
        StationModel station = (StationModel) receiver.onResponseListener.getResponse().getParcelable();
        assertEquals((int) station.number, 1);

        broadcastManager.unregisterReceiver(receiver);
    }
}