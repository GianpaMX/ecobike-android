package mx.softux.ecobike.activities;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Assert;
import org.junit.Test;

import mx.softux.ecobike.P;
import mx.softux.ecobike.R;

import static mx.softux.EspressoMatchers.matchToolbarTitle;
import static org.hamcrest.Matchers.startsWith;

/**
 * Created by juan on 5/21/15.
 */
public class StationActivityTest extends ActivityInstrumentationTestCase2<StationActivity> {
    public StationActivityTest() {
        super(StationActivity.class);
    }

    @Test
    public void testActivityTitle() {
        getActivity();
        String title = InstrumentationRegistry.getTargetContext().getString(R.string.text_station);
        matchToolbarTitle(startsWith(title));
    }

    @Test
    public void testNewIntent() {
        final int number = 1;

        Intent intent = StationActivity.newIntent(number);

        Assert.assertNotNull(intent);
        Assert.assertNotNull(intent.getExtras());
        Assert.assertTrue(intent.getExtras().containsKey(P.StationActivity.NUMBER));
        Assert.assertEquals(number, intent.getIntExtra(P.StationActivity.NUMBER, -1));
    }
}
