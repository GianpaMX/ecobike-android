package mx.softux.ecobike.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;

import mx.softux.ecobike.services.ApiService;
import mx.softux.ecobike.ApiServiceConnection;
import mx.softux.ecobike.R;

/**
 * Created by gianpa on 12/29/14.
 */
public abstract class StationsActivity extends AbstractActionBarActivity implements ApiServiceConnection {
    private ApiService apiService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, ApiService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, ApiService.class), apiServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (apiService != null) {
            unbindService(apiServiceConnection);
            apiService = null;
        }
    }

    private ServiceConnection apiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ApiService.Binder binder = (ApiService.Binder) service;
            apiService = binder.getApiService();

            onApiServiceConnected(apiService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            apiService = null;
        }
    };

    @Override
    public ApiService getApiService() {
        return apiService;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list_mode:
                startActivity(new Intent(this, StationsListActivity.class));
                finish();
                return true;
            case R.id.action_map_mode:
                startActivity(new Intent(this, StationsMapActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
