package com.thm.sensors.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.logic.BeaconLogic;
import com.thm.sensors.logic.BeaconMasterLogic;

import java.text.MessageFormat;

public final class SettingsActivity extends Activity implements View.OnClickListener {

    private BeaconLogic mBeaconLogic;

    @Override
    protected void onResume() {
        super.onResume();
        mBeaconLogic.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mBeaconLogic = new BeaconMasterLogic();
        mBeaconLogic.startLogic(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBeaconLogic.onPause();
    }

    @Override
    public void onClick(View v) {
        View rootView = v.getRootView();
        String[] beaconValues = ((TextView) rootView.findViewById(R.id.textView3)).getText().toString().split(":");
        String color = ((EditText) rootView.findViewById(R.id.editText)).getText().toString();
        int mode = Integer.parseInt(((EditText) rootView.findViewById(R.id.editText2)).getText().toString());

        if (beaconValues.length > 1) {
            String beacon = beaconValues[1];

            if (Util.beaconDeviceMap.get(beacon) == null) {
                Util.beaconColorMap.put(beacon, color);
                Util.beaconModeMap.put(beacon, mode);
            } else {
                Log.d(SettingsActivity.class.getName(),
                        MessageFormat.format("Cannot save settings because the specific beacon " +
                                "still has a slave device! - {0}", beacon));
            }
        } else {
            Log.d(SettingsActivity.class.getName(), "Cannot save settings because no beacon has been found!");
        }
    }
}
