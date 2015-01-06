package mx.softux.ecobike.utilities;

import android.os.Handler;
import android.util.Log;

/**
 * Created by gianpa on 12/30/14.
 */
public class Timer {
    private static final String TAG = Timer.class.getSimpleName();

    private static final int SECOND = 1000;
    private static final int TIMEOUT = 5 * SECOND;

    private Handler idleHandler = null;
    private Runnable idleCountertask = null;

    public Timer() {
        idleHandler = new Handler();
    }

    public void destroy() {
        if (idleCountertask != null)
            idleHandler.removeCallbacks(idleCountertask);

        idleHandler = null;
        idleCountertask = null;
    }

    public void setStop(final Stop stop) {
        idleCountertask = new Runnable() {
            int timeout = TIMEOUT;

            @Override
            public void run() {
                idleHandler.postDelayed(this, SECOND);
                Log.d(TAG, "time = " + timeout);

                if ((timeout -= 1 * SECOND) <= 0) stop.onStop();
            }
        };

        idleHandler.postDelayed(idleCountertask, SECOND);
    }

    public void cancel() {
        idleHandler.removeCallbacks(idleCountertask);
        idleCountertask = null;
    }

    public static interface Stop {
        public void onStop();
    }

}
