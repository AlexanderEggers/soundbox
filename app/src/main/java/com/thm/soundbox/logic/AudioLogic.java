package com.thm.soundbox.logic;

import android.app.Activity;

import java.io.File;
import java.io.IOException;

import com.thm.soundbox.R;
import com.thm.soundbox.Util;

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
        IoUtils.extractZipResource(mContext.getResources().openRawResource(R.raw.tryoutsynths), dir, true);
        File pdPatch = new File(dir, "tryoutsynths.pd");
        PdBase.openPatch(pdPatch.getAbsolutePath());
    }

    public void startAudio() {
        PdAudio.startAudio(mContext);
        PdBase.sendBang("START");
        //insert all audio controls here to initialize those - important part!!
    }

    public void processAudioAcceleration(int audioMode, float valueX, float valueY, float valueZ) {
        switch (audioMode) {
            case Util.AUDIO_MODE_1:
                mAudioModeLogic.executeAudioMode1(valueX);
                break;
            case Util.AUDIO_MODE_2:
                mAudioModeLogic.executeAudioMode2(valueX);
                break;
            case Util.AUDIO_MODE_3:
                mAudioModeLogic.executeAudioMode3(valueX, valueY, valueZ);
                break;
            case Util.AUDIO_MODE_4:
                mAudioModeLogic.executeAudioMode4(valueZ);
                break;
            case Util.AUDIO_MODE_5:
                mAudioModeLogic.executeAudioMode5(valueY, valueZ);
                break;
        }
    }
}
