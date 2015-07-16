package mx.softux.ecobike.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import mx.softux.ecobike.P;
import mx.softux.ecobike.R;
import mx.softux.ecobike.library.Station;

/**
 * Created by juan on 5/21/15.
 */
public class StationActivity extends AppCompatActivity {
    private static final String TAG = StationActivity.class.getSimpleName();

    private Toolbar actionBarToolbar;
    private MapView mapView;

    private Station station = new Station();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        actionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (actionBarToolbar != null) {
            setSupportActionBar(actionBarToolbar);
        }
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        RecyclerView stationListRecyclerView = (RecyclerView) findViewById(R.id.station_recycler_view);
        stationListRecyclerView.setLayoutManager(new LinearLayoutManager(stationListRecyclerView.getContext()));

        int statusCode = com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (statusCode == ConnectionResult.SUCCESS) {
            mapView = (MapView) findViewById(R.id.backdrop_map);
            mapView.onCreate(savedInstanceState);

            GoogleMap map = mapView.getMap();
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMyLocationEnabled(false);

            MapsInitializer.initialize(this);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(19.409, -99.188), 10);
            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Play Service result " + statusCode, Toast.LENGTH_SHORT).show();
        }

        station.number = getIntent().getIntExtra(P.Station.NUMBER, 0);

        String title = getString(R.string.station_activity_title, station.number);
        setTitle(title);
        collapsingToolbar.setTitle(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}
