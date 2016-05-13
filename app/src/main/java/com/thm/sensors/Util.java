package com.thm.sensors;

import java.util.HashMap;

public final class Util {
    /**
     * Device Type
     */
    public final static int MASTER = 0, SLAVE = 1;

    /**
     * Audio Modes
     */
    public static final int AUDIO_MODE_1 = 0, AUDIO_MODE_2 = 1, AUDIO_MODE_3 = 2, AUDIO_MODE_4 = 3,
            AUDIO_MODE_5 = 4;

    /**
     * Data Structure
     */
    public static HashMap<String, String> beaconDeviceMap = new HashMap<>(), beaconColorMap = new HashMap<>();
    public static HashMap<String, Integer> beaconModeMap = new HashMap<>();

    /**
     * Slave connection value
     */
    public static boolean isLogin;
    public static String connectedBeacon;
}
