package com.thm.soundbox.logic;

import org.puredata.core.PdBase;
import java.lang.Math;
public final class AudioModeLogic {



    //beacon1
    public void executeAudioMode1(float valueX) {
        //swoggle
        PdBase.sendFloat("instrument1mod", mapValue(valueX, 0 , 1));
    }

    //beacon2
    public void executeAudioMode2(float valueX) {
        //chord
        PdBase.sendFloat("instrument2mod", mapValue(valueX,  0, 1));

    }

    //beacon3
    public void executeAudioMode3(float valueX, float valueY, float valueZ) {
        //Gravity Wah-Effect
        double newVal = Math.sqrt(valueX * valueX + valueY * valueY + valueZ * valueZ);
        PdBase.sendFloat("instrument3mod", mapValue((float) newVal, 0, 0.95f));
        PdBase.
    }

    //beacon4
    public void executeAudioMode4(float valueX) {
        //Beat
        PdBase.sendFloat("instrument4mod", mapValue(valueX, 0.3f, 0.7f));

    }

    //beacon5
    public void executeAudioMode5(float valueX) {

    }

    private float mapValue(float f, float lowerEnd, float higherEnd) {

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
