package mx.softux.ecobike;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Created by gianpa on 12/29/14.
 */
public class StationsActivity extends AbstractActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list_mode:
                Intent stationsListActivity = new Intent(this, StationsListActivity.class);
                stationsListActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(stationsListActivity);
                finish();
                return true;
            case R.id.action_map_mode:
                Intent stationsMapActivity = new Intent(this, StationsMapActivity.class);
                stationsMapActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(stationsMapActivity);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
