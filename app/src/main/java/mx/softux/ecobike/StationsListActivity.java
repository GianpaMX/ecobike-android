package mx.softux.ecobike;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by gianpa on 12/29/14.
 */
public class StationsListActivity extends StationsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_list);

        final Toolbar toolbar = getActionBarToolbar();
//        toolbar.setNavigationIcon(R.drawable.ic_up);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stations_list, menu);
        return true;
    }
}
