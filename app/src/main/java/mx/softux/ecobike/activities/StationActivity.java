package mx.softux.ecobike.activities;

import android.content.Intent;
import android.os.Bundle;

import mx.softux.ecobike.P;
import mx.softux.ecobike.R;

/**
 * Created by juan on 5/21/15.
 */
public class StationActivity extends AbstractAppCompatActivity {
    private int number;

    @Override
    protected int getContentView() {
        return R.layout.activity_station;
    }

    public static Intent newIntent(int number) {
        Intent intent = new Intent();
        intent.putExtra(P.StationActivity.NUMBER, number);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(P.StationActivity.NUMBER)) {
            number = getIntent().getIntExtra(P.StationActivity.NUMBER, -1);
            if (number != -1)
                setTitle(getString(R.string.activity_station_title, number));
        }
    }
}
