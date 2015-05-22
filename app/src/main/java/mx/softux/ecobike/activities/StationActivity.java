package mx.softux.ecobike.activities;

import android.content.Intent;

import mx.softux.ecobike.P;
import mx.softux.ecobike.R;

/**
 * Created by juan on 5/21/15.
 */
public class StationActivity extends AbstractAppCompatActivity {
    @Override
    protected int getContentView() {
        return R.layout.activity_station;
    }

    public static Intent newIntent(int number) {
        Intent intent = new Intent();
        intent.putExtra(P.StationActivity.NUMBER, number);
        return intent;
    }
}
