package mx.softux.ecobike.tests;

import android.content.Context;
import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import mx.softux.ecobike.BuildConfig;
import mx.softux.ecobike.P;
import mx.softux.ecobike.R;
import mx.softux.ecobike.activities.StationActivity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by juan on 7/14/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk=17, constants = BuildConfig.class)
public class StationTests {
    private static final int STATION_TEST_NUMBER = 1;

    @Test
    public void testActivity() {
        StationActivity stationActivity = Robolectric.setupActivity(StationActivity.class);
        assertThat(stationActivity, is(notNullValue()));
    }

    @Test
    public void testActivityTitle() {
        final Context context = RuntimeEnvironment.application;

        Intent intent = new Intent();
        intent.putExtra(P.Station.NUMBER, STATION_TEST_NUMBER);
        StationActivity stationActivity = Robolectric.buildActivity(StationActivity.class).withIntent(intent).create().get();

        assertThat(stationActivity.getTitle().toString(), is(context.getString(R.string.station_activity_title, STATION_TEST_NUMBER)));
    }
}
