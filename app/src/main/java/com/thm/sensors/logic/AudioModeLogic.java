package com.thm.sensors.logic;

import org.puredata.core.PdBase;

public final class AudioModeLogic {



    //beacon1
    public void executeAudioMode1(float valueX) {

        PdBase.sendFloat("toneHeight", mapValue(valueX));
    }

    //beacon2
    public void executeAudioMode2(float valueX) {

    }

    //beacon3
    public void executeAudioMode3(float valueX, float valueY, float valueZ) {
        PdBase.sendFloat("freq1", mapValue(valueX));
        PdBase.sendFloat("freq2", mapValue(valueY));
        PdBase.sendFloat("allManipulator", mapValue(valueZ));

    }

    //beacon4
    public void executeAudioMode4(float valueZ) {

    }

    //beacon5
    public void executeAudioMode5(float valueY, float valueZ) {

    }

    private float mapValue(float f) {

       float value = 80.0f + (f * 20);

        if (value > 127.0f) {
            value = 127.0f;
        } else if (value < 0.0f) {
            value = 0.0f;
        }

        return value;
    }
}
