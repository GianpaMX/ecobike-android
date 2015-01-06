package mx.softux.ecobike.services;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.RequestQueue;
import com.android.volley.mock.MockNetwork;
import com.android.volley.toolbox.DiskBasedCache;

import java.io.File;

import mx.softux.ecobike.services.ApiService;

/**
 * Created by gianpa on 11/15/14.
 */
public class MockApiService extends ApiService {
    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";

    private MockNetwork mockNetwork;

    @Override
    public RequestQueue newRequestQueue(Context context) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        mockNetwork = new MockNetwork();

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), mockNetwork);
        queue.start();

        return queue;
    }

    public void setDataToReturn(byte[] data) {
        this.mockNetwork.setDataToReturn(data);
    }
}
