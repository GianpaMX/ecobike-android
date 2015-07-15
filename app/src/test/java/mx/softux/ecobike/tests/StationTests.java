package mx.softux.ecobike.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import mx.softux.ecobike.BuildConfig;
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
    @Test
    public void testActivity() {
        StationActivity stationActivity = Robolectric.setupActivity(StationActivity.class);
        assertThat(stationActivity, is(notNullValue()));
    }
}
