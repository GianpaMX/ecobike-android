package mx.softux.ecobike.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import mx.softux.ecobike.services.ApiService;
import mx.softux.ecobike.ApiServiceConnection;
import mx.softux.ecobike.R;

/**
 * Created by gianpa on 12/29/14.
 */
public class StationsListActivity extends StationsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_list);

        final Toolbar toolbar = getActionBarToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stations_list, menu);
        return true;
    }

    @Override
    public void onApiServiceConnected(ApiService apiService) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.stations_list_fragment);
        if(fragment instanceof ApiServiceConnection) {
            ((ApiServiceConnection) fragment).onApiServiceConnected(apiService);
        }
    }
}
