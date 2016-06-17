package com.thm.soundbox.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.thm.soundbox.R;
import com.thm.soundbox.Util;
import com.thm.soundbox.logic.BeaconLogic;
import com.thm.soundbox.logic.BeaconMasterLogic;

import java.text.MessageFormat;

public final class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String AUDIO_MODE_DIALOG = "audio-mode";
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
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Handler mHandler = new android.os.Handler() {
            public void handleMessage(Message msg) {
                String beaconAddress = Util.connectedSettingsBeacon;

                ((TextView) findViewById(R.id.textView3))
                        .setText(MessageFormat.format("Beacon {0}", beaconAddress));

                if (Util.beaconColorMap.containsKey(beaconAddress)) {
                    ((EditText) findViewById(R.id.editText))
                            .setText(Util.beaconColorMap.get(beaconAddress));
                    ((TextView) findViewById(R.id.textView))
                            .setText(Integer.toString(Util.beaconModeMap.get(beaconAddress)));
                }
            }
        };

        mBeaconLogic = new BeaconMasterLogic();
        ((BeaconMasterLogic) mBeaconLogic).startLogic(this, mHandler);
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
        String[] beaconValues = ((TextView) rootView.findViewById(R.id.textView3)).getText().toString().split(" ");

        switch (v.getId()) {
            case R.id.button:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                if (beaconValues.length > 1) {
                    String color = ((EditText) rootView.findViewById(R.id.editText)).getText().toString();
                    String modeValue = ((TextView) rootView.findViewById(R.id.textView)).getText().toString();
                    String beacon = beaconValues[1];
                    boolean gravity = ((Switch) rootView.findViewById(R.id.switch1)).isChecked();

                    if (!modeValue.equals("") && !color.equals("") && (color.length() == 6
                            || color.length() == 8)) {
                        int mode = Integer.parseInt(modeValue);

                        if (Util.beaconDeviceMap.get(beacon) == null) {
                            Util.beaconDeviceMap.put(beacon, null);
                            Util.beaconColorMap.put(beacon, "#" + color);
                            Util.beaconModeMap.put(beacon, mode);
                            Util.beaconLastData.put(beacon, 0L);
                            Util.beaconGravity.put(beacon, gravity);
                            createSimpleDialog("Save complete.");
                        } else {
                            createSimpleDialog("Selected beacon has still a device!");
                            Log.d(SettingsActivity.class.getName(),
                                    MessageFormat.format("Cannot apply the settings because the specific beacon " +
                                            "still has a slave device! - {0}", beacon));
                        }
                    } else {
                        createSimpleDialog("Entered values include errors!");
                        Log.d(SettingsActivity.class.getName(), "Cannot apply settings because mode or color haven't " +
                                "been set correctly!");
                    }
                } else {
                    Log.d(SettingsActivity.class.getName(), "Cannot apply settings because no beacon has been found!");
                }
                break;
            case R.id.button6:
                editMode();
                break;
            case R.id.button7:
                resetSettingInput();
                Util.scanForBeacons = true;
                break;
            case R.id.button8:
                resetSettingInput();
                break;
        }
    }

    private void resetSettingInput() {
        ((TextView) findViewById(R.id.textView3)).setText("Beacon");
        ((EditText) findViewById(R.id.editText)).setText("");
        ((TextView) findViewById(R.id.textView)).setText("");
        ((Switch) findViewById(R.id.switch1)).setChecked(false);
    }

    private void editMode() {
        DialogFragment dialog = new DialogModeFragment();
        dialog.show(getFragmentManager(), AUDIO_MODE_DIALOG);
    }

    public static class DialogModeFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String[] choices = new String[]{
                    "Mode 1", "Mode 2", "Mode 3", "Mode 4", "Mode 5"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose a audio mode for this beacon")
                    .setItems(choices, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((TextView) getActivity().findViewById(R.id.textView))
                                    .setText(choices[which].split(" ")[1]);
                        }
                    });
            return builder.create();
        }
    }

    private void createSimpleDialog(String text) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
