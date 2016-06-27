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
        IoUtils.extractZipResource(mContext.getResources().openRawResource(R.raw.mosyosyo), dir, true);
        File pdPatch = new File(dir, "aaamosy.pd");
        PdBase.openPatch(pdPatch.getAbsolutePath());
    }

    public void startAudio() {
        PdAudio.startAudio(mContext);
        PdBase.sendBang("START");
        //insert all audio controls here to initialize those - important part!!
    }

    public void processAudio(int audioMode, float valueX, float valueY, float valueZ) {
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
                mAudioModeLogic.executeAudioMode4(valueX);
                break;
            case Util.AUDIO_MODE_5:
                mAudioModeLogic.executeAudioMode5(valueX);
                break;
        }
    }

    public void enableAudio(int audioMode) {
        switch (audioMode) {
            case Util.AUDIO_MODE_1:
                PdBase.sendFloat("instrument1volume", 1);
                break;
            case Util.AUDIO_MODE_2:
                PdBase.sendFloat("instrument2volume", 1);
                break;
            case Util.AUDIO_MODE_3:
                PdBase.sendFloat("instrument3volume", 1);
                break;
            case Util.AUDIO_MODE_4:
                PdBase.sendFloat("instrument4volume", 1);
                break;
            case Util.AUDIO_MODE_5:
               // PdBase.sendFloat("instrument5volume", 1);
                break;
        }
    }

    public void disableAudio(int audioMode) {
        switch (audioMode) {
            case Util.AUDIO_MODE_1:
                PdBase.sendFloat("instrument1volume", 0);
                break;
            case Util.AUDIO_MODE_2:
                PdBase.sendFloat("instrument2volume", 0);
                break;
            case Util.AUDIO_MODE_3:
                PdBase.sendFloat("instrument3volume", 0);
                break;
            case Util.AUDIO_MODE_4:
                PdBase.sendFloat("instrument4volume", 0);
                break;
            case Util.AUDIO_MODE_5:
                //PdBase.sendFloat("instrument5volume", 0);
                break;
        }
    }
}
