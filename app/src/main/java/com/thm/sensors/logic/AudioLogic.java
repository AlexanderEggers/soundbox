package com.thm.sensors.logic;

import android.app.Activity;

import java.io.File;
import java.io.IOException;
import com.thm.sensors.R;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

public final class AudioLogic {

    private Activity mContext;

    public AudioLogic(Activity context) {
        mContext = context;
    }

    public void processAudioProximity(float value) {
        //send gyro value as well!!!
        //map this value!!!
        PdBase.sendFloat("toneHeight", value * 100f);
    }

    public void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
        PdUiDispatcher dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    public void loadPDPatch() throws IOException{
        File dir = mContext.getFilesDir();
        IoUtils.extractZipResource(mContext.getResources().openRawResource(R.raw.simplepatch), dir, true);
        File pdPatch = new File(dir, "simplepatch.pd");
        PdBase.openPatch(pdPatch.getAbsolutePath());
        PdAudio.startAudio(mContext);
        PdBase.sendFloat("toneHeight", 127.0f);
    }
}
