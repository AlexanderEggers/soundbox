package com.thm.sensors.logic;

import org.puredata.core.PdBase;

public final class AudioModeLogic {

    //slaveseitig immer den mittelwert aus den letzten (ca) 30 werten senden (probieren)

    //beacon1
    public void executeAudioMode1(float valueX) {
        valueX = 80.0f + Math.abs(valueX * 10);
        System.out.println("TESTING___________");

        if (valueX > 127.0f) {
            valueX = 127.0f;
        }

        PdBase.sendFloat("toneHeight", valueX);
    }

    //beacon2
    public void executeAudioMode2(float valueX) {

    }

    //beacon3
    public void executeAudioMode3(float valueX, float valueY, float valueZ) {

    }

    //beacon4
    public void executeAudioMode4(float valueZ) {

    }

    //beacon5
    public void executeAudioMode5(float valueY, float valueZ) {

    }
}
