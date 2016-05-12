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

    public AudioLogic(Activity context) {
        mContext = context;
    }

    public void processAudioAcceleration(int audioMode, float valueX, float valueY, float valueZ) {

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
