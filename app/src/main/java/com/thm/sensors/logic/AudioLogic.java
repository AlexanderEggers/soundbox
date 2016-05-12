package com.thm.sensors.logic;

import android.app.Activity;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import com.thm.sensors.R;
import com.thm.sensors.Util;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

public final class AudioLogic {

    private Activity mContext;
    private long xAccelerationID, yAccelerationID, zAccelerationID;
    private float xAccelerationValue, yAccelerationValue, zAccelerationValue;

    public AudioLogic(Activity context) {
        mContext = context;
    }

    public void processAudioProximity(float value) {
        //send gyro value as well!!!
        //map this value!!!
        PdBase.sendFloat("toneHeight", value * 100f);
    }

    public void processAudioAcceleration(int identifier, float value, long id) {
        switch (identifier) {
            case Util.ACCELERATION_X:
                xAccelerationID = id;
                xAccelerationValue = value;
                break;
            case Util.ACCELERATION_Y:
                yAccelerationID = id;
                yAccelerationValue = value;
                break;
            case Util.ACCELERATION_Z:
                zAccelerationID = id;
                zAccelerationValue = value;
                break;
        }

        if (xAccelerationID == yAccelerationID && xAccelerationID == zAccelerationID) {
            //work with values to prepare those for the audio system

            /*
            PdBase.sendFloat("?", xAccelerationValue);
            PdBase.sendFloat("?", yAccelerationValue);
            PdBase.sendFloat("?", zAccelerationValue);
            */

            ((TextView) mContext.findViewById(R.id.textView5)).setText(
                    MessageFormat.format("Acceleration X: {0}", xAccelerationValue));
            ((TextView) mContext.findViewById(R.id.textView6)).setText(
                    MessageFormat.format("Acceleration Y: {0}", yAccelerationValue));
            ((TextView) mContext.findViewById(R.id.textView7)).setText(
                    MessageFormat.format("Acceleration Z: {0}", zAccelerationValue));
        }
    }

    public void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
        PdUiDispatcher dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    public void loadPDPatch() throws IOException {
        File dir = mContext.getFilesDir();
        IoUtils.extractZipResource(mContext.getResources().openRawResource(R.raw.simplepatch), dir, true);
        File pdPatch = new File(dir, "simplepatch.pd");
        PdBase.openPatch(pdPatch.getAbsolutePath());
        PdAudio.startAudio(mContext);
        PdBase.sendFloat("toneHeight", 127.0f);
    }
}
