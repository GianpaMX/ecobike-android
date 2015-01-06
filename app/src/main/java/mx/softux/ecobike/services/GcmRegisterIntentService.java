package mx.softux.ecobike.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import mx.softux.ecobike.utilities.LogUtils;

/**
 * Created by gianpa on 11/17/14.
 */
public class GcmRegisterIntentService extends IntentService {
    private static final String TAG = GcmRegisterIntentService.class.getSimpleName();

    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static String SENDER_ID = "1041354047970";

    private GoogleCloudMessaging gcm;
    private String regId;

    public GcmRegisterIntentService() {
        super(GcmRegisterIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.LOGD(TAG, "onCreate");

        if (checkPlayServices(this)) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(this);
        } else {
            LogUtils.LOGD(TAG, "Device not supported");
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.LOGD(TAG, "onHandleIntent");
        if (regId != null) {
            LogUtils.LOGD(TAG, "Already register. regid=" + regId);
            return;
        }

        try {
            regId = gcm.register(SENDER_ID);
            storeRegistrationId(GcmRegisterIntentService.this, regId);
            LogUtils.LOGD(TAG, "Registered. regId=" + regId);
        } catch (IOException e) {
            LogUtils.LOGE(TAG, "gcm.register", e);
            return;
        }
    }

    public static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            LogUtils.LOGI(TAG, "This device is not supported.");
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            LogUtils.LOGI(TAG, "Registration not found.");
            return null;
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            LogUtils.LOGI(TAG, "App version changed.");
            return null;
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(GcmRegisterIntentService.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        LogUtils.LOGI(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
