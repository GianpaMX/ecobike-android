package mx.softux.ecobike.activities;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;

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

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @Test
    public void testActivityTitle() {
        String title = InstrumentationRegistry.getTargetContext().getString(R.string.text_station);
        matchToolbarTitle(startsWith(title));
    }
}
