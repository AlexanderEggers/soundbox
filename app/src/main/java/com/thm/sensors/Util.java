package com.thm.sensors;

import java.util.HashMap;

public final class Util {
    /**
     * General values
     */
    public static final boolean DEV_MODE = false;

    /**
     * Device types
     */
    public static final int MASTER = 0, SLAVE = 1;

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
    public static HashMap<String, Long> beaconLastData = new HashMap<>();

    /**
     * Slave connection values
     */
    public static boolean isLogin;
    public static String connectedBeacon;

    /**
     * Master settings values
     */
    public static String connectedSettingsBeacon;
}
