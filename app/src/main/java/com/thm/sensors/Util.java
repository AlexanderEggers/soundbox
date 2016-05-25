package com.thm.sensors;

import java.util.HashMap;

public final class Util {
    /**
     * General values
     */
    public static final boolean DEV_MODE = true;
    public static final double MIN_RANGE_IN_METERS = 2;

    /**
     * Device types
     */
    public static final int MASTER = 0, SLAVE = 1;

    /**
     * Audio modes
     */
    public static final int AUDIO_MODE_1 = 1, AUDIO_MODE_2 = 2, AUDIO_MODE_3 = 3, AUDIO_MODE_4 = 4,
            AUDIO_MODE_5 = 5;

    /**
     * Data structure
     */
    public static HashMap<String, String> beaconDeviceMap = new HashMap<>(), beaconColorMap = new HashMap<>();
    public static HashMap<String, Integer> beaconModeMap = new HashMap<>();
    public static HashMap<String, Long> beaconLastData = new HashMap<>();

    /**
     * Slave connection values
     */
    public static boolean isLogin, isLoggingOut;
    public static String connectedBeacon;
    public static final int DEFAULT_BACKGROUND_COLOR = 0xFFFFFF;
    public static int currentColor = -1;

    /**
     * Master settings values
     */
    public static String connectedSettingsBeacon;
    public static boolean scanForBeacons;
}
