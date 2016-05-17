package com.thm.sensors.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;

public final class SettingsActivity extends Activity implements View.OnClickListener {

    private static final String SETTINGS_FILE_NAME = "settings_data", AUDIO_MODE_DIALOG = "audio-mode";
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
        String[] beaconValues = ((TextView) rootView.findViewById(R.id.textView3)).getText().toString().split(" ");
        System.out.println("VALUE BEACON: " + Arrays.toString(beaconValues));

        switch (v.getId()) {
            case R.id.button:
                System.out.println("LENGTH: " + beaconValues.length);
                if (beaconValues.length > 1) {
                    System.out.println("INSIDE 1. ARG SAVE SETTINGS");

                    String color = ((EditText) rootView.findViewById(R.id.editText)).getText().toString();
                    String modeValue = ((TextView) rootView.findViewById(R.id.textView)).getText().toString();
                    String beacon = beaconValues[1];


                    System.out.println("MODE: " + !modeValue.equals(""));
                    System.out.println("COLOR" + !color.equals(""));
                    System.out.println("COLOR 2" + color.contains("0x"));

                    if (!modeValue.equals("") && !color.equals("") && color.contains("0x")) {
                        System.out.println("INSIDE 2. ARG SAVE SETTINGS");

                        int mode = Integer.parseInt(modeValue);

                        if (Util.beaconDeviceMap.get(beacon) == null) {
                            Util.beaconDeviceMap.put(beacon, null);
                            Util.beaconColorMap.put(beacon, color);
                            Util.beaconModeMap.put(beacon, mode);
                            Util.beaconLastData.put(beacon, 0L);
                        } else {
                            Log.d(SettingsActivity.class.getName(),
                                    MessageFormat.format("Cannot apply the settings because the specific beacon " +
                                            "still has a slave device! - {0}", beacon));
                        }
                    } else {
                        Log.d(SettingsActivity.class.getName(), "Cannot apply settings because mode or color haven't " +
                                "been set correctly!");
                    }
                    saveSettings();
                } else {
                    Log.d(SettingsActivity.class.getName(), "Cannot apply settings because no beacon has been found!");
                }
                break;
            case R.id.button4:
                loadSettings();
                break;
            case R.id.button5:
                resetSettings();
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
    }

    private void editMode() {
        DialogFragment dialog = new DialogModeFragment();
        dialog.show(getFragmentManager(), AUDIO_MODE_DIALOG);
    }

    private void saveSettings() {
        File file = null;
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;

        try {
            file = new File(getFilesDir() + "/" + SETTINGS_FILE_NAME + "_temp");
            fileOut = new FileOutputStream(file);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(Util.beaconDeviceMap);
            out.writeObject(Util.beaconColorMap);
            out.writeObject(Util.beaconModeMap);
            out.writeObject(Util.beaconLastData);
        } catch (Exception e) {
            if (file != null) {
                file.delete();
                file = null;
            }

            Log.d(SettingsActivity.class.getName(), "saveSettings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException e) {
                Log.d(SettingsActivity.class.getName(), "saveSettings: " + e.getMessage());
                e.printStackTrace();
            }

            if (file != null) {
                new File(getFilesDir() + "/" + SETTINGS_FILE_NAME).delete();
                file.renameTo(new File(getFilesDir() + "/" + SETTINGS_FILE_NAME));
            }
        }

    }

    private void loadSettings() {
        ObjectInputStream in = null;
        File file = new File(getFilesDir() + "/" + SETTINGS_FILE_NAME);

        if (file.exists()) {
            try {
                in = new ObjectInputStream(new FileInputStream(file));
                Util.beaconDeviceMap = (HashMap<String, String>) in.readObject();
                Util.beaconColorMap = (HashMap<String, String>) in.readObject();
                Util.beaconModeMap = (HashMap<String, Integer>) in.readObject();
                Util.beaconLastData = (HashMap<String, Long>) in.readObject();
            } catch (Exception e) {
                Log.d(SettingsActivity.class.getName(), "loadSettings: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.d(SettingsActivity.class.getName(), "loadSettings: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void resetSettings() {
        Util.beaconDeviceMap.clear();
        Util.beaconColorMap.clear();
        Util.beaconModeMap.clear();

        if (!new File(getFilesDir() + "/" + SETTINGS_FILE_NAME).delete()) {
            Log.d(SettingsActivity.class.getName(), "Found no settings file");
        }
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
}
