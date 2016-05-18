package com.thm.sensors.logic;

import android.app.Activity;

import java.io.File;
import java.io.IOException;

import com.thm.sensors.R;
import com.thm.sensors.Util;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

public final class AudioLogic {

    private final Activity mContext;
    private final AudioModeLogic mAudioModeLogic;

    public AudioLogic(Activity context) {
        mContext = context;
        mAudioModeLogic = new AudioModeLogic();
    }

    public void initPD() throws IOException {
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
        PdUiDispatcher dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    public void loadPDPatch() throws IOException {
        File dir = mContext.getFilesDir();
        IoUtils.extractZipResource(mContext.getResources().openRawResource(R.raw.simplestpatch), dir, true);
        File pdPatch = new File(dir, "simplestpatch.pd");
        PdBase.openPatch(pdPatch.getAbsolutePath());
    }

    public void startAudio() {
        PdAudio.startAudio(mContext);

        //insert all audio controls here to initialize those - important part!!
    }

    public void processAudioAcceleration(int audioMode, float valueX, float valueY, float valueZ) {


        switch (audioMode) {
            case 1:
                mAudioModeLogic.executeAudioMode1(valueX);
                break;
            case 2:
                mAudioModeLogic.executeAudioMode2(valueX);
                break;
            case 3:
                mAudioModeLogic.executeAudioMode3(valueX, valueY, valueZ);
                break;
            case 4:
                mAudioModeLogic.executeAudioMode4(valueZ);
                break;
            case 5:
                mAudioModeLogic.executeAudioMode5(valueY, valueZ);
                break;
        }
    }


}
