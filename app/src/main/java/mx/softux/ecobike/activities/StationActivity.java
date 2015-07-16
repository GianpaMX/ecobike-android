package mx.softux.ecobike.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import mx.softux.ecobike.P;
import mx.softux.ecobike.R;
import mx.softux.ecobike.library.Station;

/**
 * Created by juan on 5/21/15.
 */
public class StationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Toolbar actionBarToolbar;
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.backdrop_map);
        mapFragment.getMapAsync(this);

        station.number = getIntent().getIntExtra(P.Station.NUMBER, 0);

        String title = getString(R.string.station_activity_title, station.number);
        setTitle(title);
        collapsingToolbar.setTitle(title);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng home = new LatLng(19.409, -99.188);
        googleMap.addMarker(new MarkerOptions().position(home).title("Home"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(home));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
    }
}
