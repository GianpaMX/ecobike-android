package mx.softux.ecobike.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import mx.softux.ecobike.R;

/**
 * Created by gianpa on 12/29/14.
 */
public abstract class AbstractAppCompatActivity extends AppCompatActivity {
    private Toolbar actionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        getActionBarToolbar();
    }

    protected abstract int getContentView();

    protected Toolbar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return actionBarToolbar;
    }
}
