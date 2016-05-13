package com.thm.sensors;

import java.util.HashMap;

public final class Util {
    /**
     * Device types
     */
    public final static int MASTER = 0, SLAVE = 1;

    /**
     * Audio modes
     */
    public static final int AUDIO_MODE_1 = 0, AUDIO_MODE_2 = 1, AUDIO_MODE_3 = 2, AUDIO_MODE_4 = 3,
            AUDIO_MODE_5 = 4;

    /**
     * Data structure
     */
    public static HashMap<String, String> beaconDeviceMap = new HashMap<>(), beaconColorMap = new HashMap<>();
    public static HashMap<String, Integer> beaconModeMap = new HashMap<>();

    /**
     * Slave connection values
     */
    public static boolean isLogin;
    public static String connectedBeacon;

    /**
     * Master Settings values
     */
    public static String connectedSettingsBeacon;
}
