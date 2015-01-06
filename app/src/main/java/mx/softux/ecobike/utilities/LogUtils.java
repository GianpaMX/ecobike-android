package mx.softux.ecobike.utilities;

import android.util.Log;

import mx.softux.ecobike.BuildConfig;

public class LogUtils {
    private static final String TAG = "";

    public static void LOGD(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG + tag, message);
        }
    }

    public static void LOGD(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void LOGV(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG + tag, message);
        }
    }

    public static void LOGI(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG + tag, message);
        }
    }

    public static void LOGW(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG + tag, message);
        }
    }

    public static void LOGW(final String tag, String message, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG + tag, message, e);
        }
    }


    public static void LOGE(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG + tag, message);
        }
    }

    public static void LOGE(final String tag, String message, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG + tag, message, e);
        }
    }
}
