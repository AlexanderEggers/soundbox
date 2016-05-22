package com.thm.sensors.logic;

import org.puredata.core.PdBase;

public final class AudioModeLogic {



    //beacon1
    public void executeAudioMode1(float valueX) {

        PdBase.sendFloat("oscFreq20to69", mapValue(valueX, 40 , 50));
    }

    //beacon2
    public void executeAudioMode2(float valueX) {

    }

    //beacon3
    public void executeAudioMode3(float valueX, float valueY, float valueZ) {
        PdBase.sendFloat("freq1", mapValue(valueX, 0, 127));
        PdBase.sendFloat("allManipulator", 127.0f);
        PdBase.sendFloat("cutoff", 127.0f);
    }

    //beacon4
    public void executeAudioMode4(float valueZ) {

    }

    //beacon5
    public void executeAudioMode5(float valueY, float valueZ) {

    }

    private float mapValue(float f, int lowerEnd, int higherEnd) {

        //assuming that the value of the gyrometer ranges from -10.0f to 10.0f

        float mapValue = (higherEnd - ((lowerEnd + higherEnd) / 2.0f)) / 10.0f;
        float value = (mapValue * f) + ((lowerEnd + higherEnd) / 2.0f);

        if (value > higherEnd) {
            value = higherEnd;
        } else if (value < lowerEnd) {
            value = lowerEnd;
        }

        return value;
    }
}
