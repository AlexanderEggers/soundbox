package com.thm.soundbox;

import java.util.HashMap;

public final class Util {
    /**
     * General values
     */
    public static final boolean DEV_MODE = true;
    public static final long MIN_RANGE = 3L;

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
    public static HashMap<String, Boolean> beaconGravity = new HashMap<>();

    /**
     * Slave connection values
     */
    public static boolean isLogin, isLoggingOut;
    public static String connectedBeacon;
    public static final int DEFAULT_BACKGROUND_COLOR = 0xFFFFFF;
    public static int currentColor = DEFAULT_BACKGROUND_COLOR;
    public static boolean gravity;

    /**
     * Master settings values
     */
    public static String connectedSettingsBeacon;
    public static boolean scanForBeacons;

    /**
     * Master-Slave values
     */
    public static final int SAVED_VALUE_AMOUNT = 3;
    public static final boolean INTERPOLATION = false;
    public static int valueCounter;
    public static float[][] lastSensorValues = new float[Util.SAVED_VALUE_AMOUNT][3];

    public static float processArrayValues(int k) {
        float sum = 0;
        for (int i = 0; i < SAVED_VALUE_AMOUNT; i++) {
            sum += lastSensorValues[i][k];
        }

        return sum / SAVED_VALUE_AMOUNT;
    }
}
