package mx.softux.ecobike.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import mx.softux.ecobike.P;
import mx.softux.ecobike.R;
import mx.softux.ecobike.library.Station;

/**
 * Created by juan on 5/21/15.
 */
public class StationActivity extends AppCompatActivity {
    private Toolbar actionBarToolbar;
    private Station station = new Station();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }

        station.number = getIntent().getIntExtra(P.Station.NUMBER, 0);

        setTitle(getString(R.string.station_activity_title, station.number));
    }
}
